package net.wespot.pim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.DataCollectionLazyListAdapter;
import net.wespot.pim.utils.VideoLibManager;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.dataCollection.PictureLibManager;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GeneralItemLocalObject;

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

public class ShareSecondActivity extends BaseFragmentActivity implements ListItemClickInterface<GeneralItemLocalObject> {

    private DataCollectionLazyListAdapter datAdapter;
    private ListView data_collection_tasks;
    private PictureLibManager man_pic;
    private VideoLibManager man_vid;
    private ArrayList<Uri> imageUris;
    private ArrayList<Uri> videoUris;
    private Uri imageUri;
    private Uri videoUri;
    private String sharedText;
    private Intent intent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();

        imageUris = intent.getParcelableArrayListExtra("arrayImages");
        videoUris = intent.getParcelableArrayListExtra("arrayVideos");
        imageUri = intent.getParcelableExtra("image");
        videoUri = intent.getParcelableExtra("video");
        sharedText =  intent.getStringExtra("string");

        setContentView(R.layout.fragment_data_collection);

        data_collection_tasks = (ListView) findViewById(R.id.data_collection_tasks);

        context = this;

        datAdapter =  new DataCollectionLazyListAdapter(this, INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameId());
        datAdapter.setOnListItemClickCallback(this);
        data_collection_tasks.setAdapter(datAdapter);
    }

    @Override
    public void onListItemClick(View view, int i, final GeneralItemLocalObject generalItemLocalObject) {
        if (imageUri != null){
            Uri image = Uri.parse(String.valueOf(imageUri));
            man_pic = new PictureLibManager((Activity) context);
            man_pic.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
            man_pic.setGeneralItem(generalItemLocalObject);
            man_pic.uploadPicture(image);
        }

        if (imageUris != null){
            for (Uri im : imageUris) {
                Uri image = Uri.parse(String.valueOf(im));
                man_pic = new PictureLibManager((Activity) context);
                man_pic.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_pic.setGeneralItem(generalItemLocalObject);
                man_pic.uploadPicture(image);
            }
        }

        if (videoUri != null){
            Uri video = Uri.parse(String.valueOf(videoUri));
            man_vid = new VideoLibManager((Activity) context);
            man_vid.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
            man_vid.setGeneralItem(generalItemLocalObject);
            man_vid.uploadPicture(video);
        }

        if (videoUris != null){
            for (Uri im : videoUris) {
                Uri video = Uri.parse(String.valueOf(im));
                man_vid = new VideoLibManager((Activity) context);
                man_vid.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_vid.setGeneralItem(generalItemLocalObject);
                man_vid.uploadPicture(video);
            }
        }

        Toast.makeText(context, "Sharing files...", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Sharing files completed.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean setOnLongClickListener(View view, int i, GeneralItemLocalObject generalItemLocalObject) {
        return false;
    }
}
