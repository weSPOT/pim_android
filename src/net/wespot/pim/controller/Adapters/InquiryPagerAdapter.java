package net.wespot.pim.controller.Adapters;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Angel Suarez
 * ****************************************************************************
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.view.*;

import java.util.ArrayList;

/**
 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
 * representing an object in the collection.
 */
public class InquiryPagerAdapter extends FragmentPagerAdapter {

    public static final int PAGE_COUNT = 4;
    public static final int FIRST_PAGE = 0;
    public static final int SECOND_PAGE = 1;
    public static final int THIRD_PAGE = 2;
    public static final int FOURTH_PAGE = 3;



    public static final String INQUIRY_ID = "inquiry_id";
    private final ArrayList<Fragment> mFragments;
    private final ArrayList<Fragment> mBackFragments;
    private ViewPager mContainer;
    private FragmentManager mFragmentManager;

    public InquiryPagerAdapter(FragmentManager fm, ViewPager mViewPager) {
        super(fm);
        mContainer = mViewPager;
        mFragments = new ArrayList<Fragment>(){};
        mBackFragments = new ArrayList<Fragment>(){};
        for (int i = 0; i < PAGE_COUNT; i++) {
            mFragments.add(null);
            mBackFragments.add(null);
        }
    }

    /**
     * Replaces the view pager fragment at specified position.
     */
    public void replace(int position, Fragment fragment) {
        // Get currently active fragment.
        Fragment old_fragment = mFragments.get(position);
        if (old_fragment == null) {
            return;
        }

        // Replace the fragment using transaction and in underlaying array list.
        // NOTE .addToBackStack(null) doesn't work
        this.startUpdate(mContainer);
        mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .remove(old_fragment).add(mContainer.getId(), fragment)
                .commit();
        mFragments.set(position, fragment);
        this.notifyDataSetChanged();
        this.finishUpdate(mContainer);
    }

    /**
     * Replaces the fragment at specified position and stores the current fragment to back stack
     * so it can be restored by #back().
     */
    public void start(int position, Fragment fragment) {
        // Remember current fragment.
        mBackFragments.set(position, mFragments.get(position));

        // Replace the displayed fragment.
        this.replace(position, fragment);
    }

    /**
     * Replaces the current fragment by fragment stored in back stack. Does nothing and returns
     * false if no fragment is back-stacked.
     */
    public boolean back() {
        int position = mContainer.getCurrentItem();
        Fragment fragment = mBackFragments.get(position);
        if (fragment == null) {
            // Nothing to go back.
            return false;
        }

        // Restore the remembered fragment and remove it from back fragments.
        this.replace(position, fragment);
        mBackFragments.set(position, null);
        return true;
    }

    @Override
    public Fragment getItem(int i) {

        Fragment frag;
        Bundle args = new Bundle();

        switch (i) {
            case Constants.ID_DESCRIPTION:
                mFragments.set(FIRST_PAGE, new InqDescriptionFragment());
                break;
            case Constants.ID_QUESTION:
                mFragments.set(SECOND_PAGE, new InqQuestionFragment());
                break;
            case Constants.ID_DATA:
                mFragments.set(THIRD_PAGE, new InqDataCollectionFragment());
                break;
            case Constants.ID_COMMUNICATE:
                mFragments.set(FOURTH_PAGE, new InqCommunicateFragment());
                break;
            default:
                // The other sections of the app are dummy placeholders.
                frag = new DemoObjectFragment();
                args.putInt(INQUIRY_ID, i + 1); // Our object is just an integer :-P
                frag.setArguments(args);
                mFragments.set(FIRST_PAGE, frag);
        }

        return mFragments.get(i);
    }


    /**
     * Custom item ID resolution. Needed for proper page fragment caching.
     * @see FragmentPagerAdapter#getItemId(int).
     */
    @Override
    public long getItemId(int position) {
        // Fragments from second level page hierarchy have their ID raised above 100. This is
        // important to FragmentPagerAdapter because it is caching fragments to FragmentManager with
        // this item ID key.
        Fragment item = mFragments.get(position);
        if (item != null) {
            if ((item instanceof InqDescriptionFragment) || (item instanceof InqQuestionFragment) ||
                    (item instanceof InqDataCollectionFragment)||
                    (item instanceof InqCommunicateFragment)) {
                return 100 + position;
            }
        }

        return position;
    }

    @Override
    public int getItemPosition(Object object)
    {
        int position = POSITION_UNCHANGED;
        if ((object instanceof InqDescriptionFragment)) {
            if (object.getClass() != mFragments.get(FIRST_PAGE).getClass()) {
                position = POSITION_NONE;
            }
        }
        if ((object instanceof InqQuestionFragment)) {
            if (object.getClass() != mFragments.get(SECOND_PAGE).getClass()) {
                position = POSITION_NONE;
            }
        }
        if ((object instanceof InqDataCollectionFragment)) {
            if (object.getClass() != mFragments.get(THIRD_PAGE).getClass()) {
                position = POSITION_NONE;
            }
        }
        if ((object instanceof InqCommunicateFragment)) {
            if (object.getClass() != mFragments.get(FOURTH_PAGE).getClass()) {
                position = POSITION_NONE;
            }
        }
        return position;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}