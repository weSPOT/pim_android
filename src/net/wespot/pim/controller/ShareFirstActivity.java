package net.wespot.pim.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.InquiryLazyListAdapter;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.RunLocalObject;
import org.celstec.events.InquiryEvent;

import java.io.File;
import java.util.ArrayList;

/**
 * ****************************************************************************
 * Copyright (C) 2015 Open Universiteit Nederland
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
 * Date: 20/04/15
 * ****************************************************************************
 */

public class ShareFirstActivity extends BaseFragmentActivity implements ListItemClickInterface<InquiryLocalObject> {

    private static final String TAG = "Share";
    private InquiryLazyListAdapter adapterInq;
    private File bitmapFile;
    private ArrayList<Uri> imageUris;
    private Uri imageUri;
    private String sharedText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INQ.init(this);
        INQ.accounts.syncMyAccountDetails();
        ARL.eventBus.register(this);

        if (savedInstanceState != null) {
        }

        INQ.inquiry.syncInquiries();
        INQ.games.syncMyGames();

        setContentView(R.layout.fragment_inquiries);

        ListView inquiries = (ListView) findViewById(R.id.list_inquiries);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.inquiries_new_inquiry);

        adapterInq =  new InquiryLazyListAdapter(this);
        inquiries.setAdapter(adapterInq);
        adapterInq.setOnListItemClickCallback(this);


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            for (Uri imageUri : imageUris) {

            }
        }
    }

    public void onEventAsync(InquiryEvent inquiryObject){
//        Toast.makeText(getApplicationContext(), "Loaded: "+DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size()+" inquiries",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(View view, int i, InquiryLocalObject inquiryLocalObject) {
        INQ.inquiry.setCurrentInquiry(inquiryLocalObject);
        if (INQ.inquiry.getCurrentInquiry().getRunLocalObject() != null) {
            GameLocalObject gameLocalObject = INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject();

            Log.e(TAG, gameLocalObject+" - "+INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId()+" "+INQ.inquiry.getCurrentInquiry().getId()+" - "+INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameId());
            if (gameLocalObject != null) {

                INQ.inquiry.syncDataCollectionTasks();

                Intent select_dc_task = new Intent(this, ShareSecondActivity.class);

                select_dc_task.putParcelableArrayListExtra("array", imageUris);

                select_dc_task.putExtra("string", sharedText);

                select_dc_task.putExtra("image", imageUri);

                startActivity(select_dc_task);
            } else {
                long gameId = INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameId();
                GameLocalObject game = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
                if (game != null){
                    INQ.inquiry.getCurrentInquiry().getRunLocalObject().setGameLocalObject(game);
                }else{
                    Toast.makeText(getApplicationContext(), "Not sync yet. Try again (2)", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            INQ.runs.syncRun(INQ.inquiry.getCurrentInquiry().getRunId());
            RunLocalObject run = DaoConfiguration.getInstance()
                    .getRunLocalObjectDao().load(INQ.inquiry.getCurrentInquiry().getRunId());
            if (run != null){
                INQ.inquiry.getCurrentInquiry().setRunLocalObject(run);
            }else{
                Toast.makeText(getApplicationContext(), "Not sync yet. Try again (1)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean setOnLongClickListener(View view, int i, InquiryLocalObject inquiryLocalObject) {
        return false;
    }
}
