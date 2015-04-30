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
import net.wespot.pim.view.InqCommunicateFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.events.MessageEvent;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.MessageLocalObjectDao;

public class InquiryPhasesActivity extends BaseFragmentActivity implements ListItemClickInterface<View> {

    private static final String TAG = "InquiryActivity";
    private static int numberMessages;
    private View chatView;

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

        INQ.init(this);
//        INQ.eventBus.register(this);

        if (savedInstanceState != null) {
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
                Log.e(TAG, "RUN ID: " + INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId()+" "+INQ.inquiry.getCurrentInquiry().getRunLocalObject().getTitle());
            }
            Log.e(TAG, "INQUIRY ID: " + INQ.inquiry.getCurrentInquiry().getId()+" "+INQ.inquiry.getCurrentInquiry().getTitle());
        }

        ////////////////////////////////////
        // When coming from notification bar
        ////////////////////////////////////
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long inquiry_id = extras.getLong(InqCommunicateFragment.INQUIRY_ID);

            INQ.inquiry.setCurrentInquiry(DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(inquiry_id));

//            if (INQ.inquiry.getCurrentInquiry().getRunLocalObject() != null) {
//                GameLocalObject gameLocalObject = INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject();
//                if (gameLocalObject != null) {
//                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                        Intent intent = new Intent(getApplicationContext(), InquiryActivityBack.class);
//                        intent.putExtra(InquiryActivity.PHASE, Constants.ID_COMMUNICATE);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), InquiryActivity.class);
//                        intent.putExtra(InquiryActivity.PHASE, Constants.ID_COMMUNICATE);
//                        startActivity(intent);
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Add data collection task on IWE", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "Game is not sync yet", Toast.LENGTH_SHORT).show();
//            }
//
//            extras = null;
//
//            return;
        }

        INQ.inquiry.syncDataCollectionTasks(INQ.inquiry.getCurrentInquiry());
        INQ.threads.syncThreads(INQ.inquiry.getCurrentInquiry().getRunId());

        setContentView(R.layout.activity_phases);
        getActionBar().setTitle(R.string.actionbar_inquiry_list);

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

        numberMessages = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
            .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()), MessageLocalObjectDao.Properties.Read.isNull())
                .list().size();

        LinearLayout listPhasesContainer = (LinearLayout) findViewById(R.id.list_phases);

        // Instantiation of the buttonManager
        ButtonManager buttonManager = new ButtonManager(this);
        buttonManager.setOnListItemClickCallback(this);

        // Creation of layout params
        LinearLayout.LayoutParams zeroLayoutParams = buttonManager.generateLayoutParams(
                R.dimen.mainscreen_margintop_zero,
                (int)getResources().getDimension(R.dimen.mainscreen_margintop_zero));

        // Description button_old
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams,
                Constants.ID_DESCRIPTION,
                R.string.inquiry_title_description,
                R.drawable.ic_description, "", false);

        // Question button_old
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams,
                Constants.ID_QUESTION,
                R.string.inquiry_title_question,
                R.drawable.ic_question, "", false);

        // Data Collection button_old
        buttonManager.generateButton(listPhasesContainer, zeroLayoutParams,
                Constants.ID_DATA,
                R.string.inquiry_title_data,
                R.drawable.ic_data, "", false);

        // Messaging button
        chatView = buttonManager.generateButton(listPhasesContainer, zeroLayoutParams,
                Constants.ID_COMMUNICATE,
                R.string.inquiry_title_communicate,
                R.drawable.ic_communicate, "", true);

//        String.valueOf(numberMessages)

        // Invite friends button_old
//        buttonManager.generateButton(listPhasesContainer, separatorLayoutParams, Constants.ID_FRIENDS,
//                R.string.phases_invite_new_friend, R.drawable.ic_invite_friend, "");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onEventMainThread(MessageEvent messageEvent){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListItemClick(View v, int id, View object) {
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
                    Toast.makeText(getApplicationContext(), "Please wait: data collection  is syncing", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please wait: game is syncing", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, View object) {
        return false;
    }
}