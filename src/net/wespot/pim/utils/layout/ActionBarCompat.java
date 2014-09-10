package net.wespot.pim.utils.layout;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * ****************************************************************************
 * Copyright (C) 2014 Open Universiteit Nederland
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
 * Date: 05/09/14
 * ****************************************************************************
 */

public class ActionBarCompat extends ActionBarActivity implements ActionBarGeneric<ActionBar.Tab> {

    private ActionBar mActionBar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    public ActionBarCompat() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar = getSupportActionBar();

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        //
        mActionBar.setHomeButtonEnabled(false);

        // Hide/show home icon
        mActionBar.setDisplayShowHomeEnabled(false);

        // Hide/show title
        mActionBar.setDisplayShowTitleEnabled(true);

        // Enable/disable button home
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // Enable/disable entire button
        mActionBar.setHomeButtonEnabled(true);
        mTitle = mDrawerTitle = getTitle();

    }

    @Override
    public void init() {
//        mActionBar = getSupportActionBar();
//        mActionBar.setHomeButtonEnabled(false);
//        mActionBar.setDisplayShowHomeEnabled(false);
//        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        mActionBar.setDisplayShowTitleEnabled(false);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setHomeButtonEnabled(true);
//        mTitle = mDrawerTitle = getTitle();
    }

    @Override
    public void onDrawerClosed() {
        mActionBar.setTitle(mTitle);
    }

    @Override
    public void onDrawerOpened() {
        mActionBar.setTitle(mDrawerTitle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    @Override
    public void setSelectedNavigationItem(int pos) {
        mActionBar.setSelectedNavigationItem(pos);
    }

    @Override
    public void setNavigationMode(int a) {
        mActionBar.setNavigationMode(a);
    }

    @Override
    public void addTab(ActionBar.Tab tab) {
//        mActionBar.addTab(tab);
    }

    @Override
    public ActionBar.Tab newTab() {
        return null;
    }
}
