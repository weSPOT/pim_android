package net.wespot.pim.utils.layout;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

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

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActionBarCurrent extends Activity implements ActionBarGeneric<ActionBar.Tab> {

    private ActionBar mActionBar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    public ActionBarCurrent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void init() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mTitle = mDrawerTitle = getTitle();
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
        mActionBar.addTab(tab);
    }

    @Override
    public ActionBar.Tab newTab() {
        return mActionBar.newTab();
    }
}
