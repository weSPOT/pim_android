package net.wespot.pim.view.impl;

import android.app.ActionBar;
import android.os.Bundle;
import net.wespot.pim.R;
import org.celstec.arlearn2.android.dataCollection.activities.ValueInputCollectionActivity;

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
public class ValueInputCollectionActivityImpl extends ValueInputCollectionActivity {

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

        setTitle("Numeric data collection");
    }
    @Override
    public int getTextView() {
        return R.id.textView1;
    }

    @Override
    public int getHeaderTextView() {
        return 0; //TODO
    }

    @Override
    public int getGameGeneralItemDcNumberInput() {
        return R.layout.game_general_item_dc_number_input;
    }

    @Override
    public int getDataCollectionSubmit() {
        return R.id.dataCollectionSubmit;
    }

    @Override
    public int getCancelButton() {
        return 0; //TODO
    }

    @Override
    public int getButton0() {
        return R.id.button0;
    }

    @Override
    public int getButton1() {
        return R.id.button1;
    }

    @Override
    public int getButton2() {
        return R.id.button2;
    }

    @Override
    public int getButton3() {
        return R.id.button3;
    }

    @Override
    public int getButton4() {
        return R.id.button4;

    }

    @Override
    public int getButton5() {
        return R.id.button5;

    }

    @Override
    public int getButton6() {
        return R.id.button6;
    }

    @Override
    public int getButton7() {
        return R.id.button7;

    }

    @Override
    public int getButton8() {
        return R.id.button8;
    }

    @Override
    public int getButton9() {
        return R.id.button9;
    }

    @Override
    public int getButtonDot() {
        return R.id.buttonDot;
    }

    @Override
    public int getButtonBack() {
        return R.id.buttonBack;
    }
}
