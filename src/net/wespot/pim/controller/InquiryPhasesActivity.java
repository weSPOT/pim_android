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
 * ****************************************************************************
 */
package net.wespot.pim.controller;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.compat.controller.InquiryActivityBack;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.utils.images.BitmapWorkerTask;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import net.wespot.pim.utils.layout.ButtonManager;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GameLocalObject;

public class InquiryPhasesActivity extends BaseFragmentActivity implements ListItemClickInterface<View>{

    private static final String TAG = "InquiryActivity";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("currentInquiry", INQ.inquiry.getCurrentInquiry().getId());
        if(INQ.inquiry.getCurrentInquiry().getRunLocalObject()!=null){
            outState.putLong("currentInquiryRunLocalObject", INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            INQ.init(this);
            INQ.accounts.syncMyAccountDetails();
            INQ.inquiry.setCurrentInquiry(
                    DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(
                            savedInstanceState.getLong("currentInquiry")
                    )
            );
            if(savedInstanceState.getLong("currentInquiryRunLocalObject")!=0){
                INQ.inquiry.getCurrentInquiry().setRunLocalObject(
                        DaoConfiguration.getInstance().getRunLocalObjectDao().load(
                                savedInstanceState.getLong("currentInquiryRunLocalObject")
                        )
                );
                Log.e(TAG, "go through savedInstanceState currentInquiryRunLocalObject" + savedInstanceState + " " + DaoConfiguration.getInstance().getRunLocalObjectDao());
            }
            Log.e(TAG, "go through savedInstanceState currentInquiry" + savedInstanceState + " " + INQ.inquiry.getCurrentInquiry());
        }

        INQ.inquiry.syncDataCollectionTasks(INQ.inquiry.getCurrentInquiry());
        INQ.threads.syncThreads(INQ.inquiry.getCurrentInquiry().getRunId());

        setContentView(R.layout.activity_phases);

//        if (!(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)) {
//            if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)){
        getActionBar().setTitle(R.string.actionbar_inquiry_list);
//            }
//        }

        TextView inquiry_description_title = (TextView) findViewById(R.id.list_phases_title);
        ImageView inquiry_description_image = (ImageView) findViewById(R.id.list_phases_image);

        if (INQ.inquiry.getCurrentInquiry().getIcon() != null){
            BitmapWorkerTask task = new BitmapWorkerTask(inquiry_description_image);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                task.execute(INQ.inquiry.getCurrentInquiry().getIcon());
            } else {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, INQ.inquiry.getCurrentInquiry().getIcon());
            }
        }
        else{
            inquiry_description_image.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_placeholder));
        }

        inquiry_description_title.setText(INQ.inquiry.getCurrentInquiry().getTitle());


        LinearLayout listPhasesContainer = (LinearLayout) findViewById(R.id.list_phases);

        // Instantiation of the buttonManager
        ButtonManager buttonManager = new ButtonManager(this);
        buttonManager.setOnListItemClickCallback(this);

        // Creation of layout params
        LinearLayout.LayoutParams separatorLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_second, (int)getResources().getDimension(R.dimen.zero_space_between_list_items));
        LinearLayout.LayoutParams zeroLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_zero, (int)getResources().getDimension(R.dimen.space_between_list_items));

        // Description button_old
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams,
                Constants.ID_DESCRIPTION,
                Constants.INQUIRY_PHASES_LIST.get(Constants.ID_DESCRIPTION),
                Constants.INQUIRY_ICON_PHASES_LIST.get(Constants.ID_DESCRIPTION), "");

        if (INQ.config.getProperty("question_phase").equals("true")) {
            // Question button_old
            buttonManager.generateButton(listPhasesContainer, zeroLayoutParams, Constants.ID_QUESTION,
                    Constants.INQUIRY_PHASES_LIST.get(Constants.ID_QUESTION),
                    Constants.INQUIRY_ICON_PHASES_LIST.get(Constants.ID_QUESTION), "");
        }
        // Data Collection button_old
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams, Constants.ID_DATA,
                Constants.INQUIRY_PHASES_LIST.get(Constants.ID_DATA),
                Constants.INQUIRY_ICON_PHASES_LIST.get(Constants.ID_DATA), "");

        // Messaging button
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams, Constants.ID_COMMUNICATE,
                Constants.INQUIRY_PHASES_LIST.get(Constants.ID_COMMUNICATE),
                Constants.INQUIRY_ICON_PHASES_LIST.get(Constants.ID_COMMUNICATE), "");

        // Invite friends button_old
//        buttonManager.generateButton(listPhasesContainer, separatorLayoutParams, Constants.ID_FRIENDS,
//                R.string.phases_invite_new_friend, R.drawable.ic_invite_friend, "");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onListItemClick(View v, int id, View object) {
        Log.e(TAG, "Access to phase number "+id);

        if (id != Constants.ID_FRIENDS){
            if (INQ.inquiry.getCurrentInquiry().getRunLocalObject() != null) {
                GameLocalObject gameLocalObject = INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject();
                if (gameLocalObject != null) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        Intent intent = new Intent(getApplicationContext(), InquiryActivityBack.class);
                        intent.putExtra(InquiryActivity.PHASE, id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), InquiryActivity.class);
                        intent.putExtra(InquiryActivity.PHASE, id);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Add data collection task on IWE", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Game is not sync yet", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
//            INQ.friendsDelegator.
        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, View object) {
        return false;
    }
}