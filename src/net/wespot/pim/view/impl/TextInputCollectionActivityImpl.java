package net.wespot.pim.view.impl;

import android.app.ActionBar;
import android.os.Bundle;
import net.wespot.pim.R;
import org.celstec.arlearn2.android.dataCollection.activities.TextInputCollectionActivity;

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
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class TextInputCollectionActivityImpl extends TextInputCollectionActivity {

    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayShowTitleEnabled(true);

        setTitle("Text data collection");
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public int getGameGeneralItemDcTextInput() {
        return R.layout.game_general_item_dc_text_input;
    }

    @Override
    public int getDataCollectionText() {
        return R.id.dataCollectionText;
    }

    @Override
    public int getDataCollectionSubmit() {
        return R.id.dataCollectionSubmit;
    }

    @Override
    public int getCancelButton() {
        return 0;
    }

    @Override
    public int getSubmitButton() {
        return 0;
    }
}
