package net.wespot.pim.view;

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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.ResponsesLazyListAdapter;
import net.wespot.pim.controller.ImageDetailActivity;
import net.wespot.pim.controller.ImageGridFragment;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import net.wespot.pim.view.impl.AudioCollectionActivityImpl;
import net.wespot.pim.view.impl.TextInputCollectionActivityImpl;
import net.wespot.pim.view.impl.ValueInputCollectionActivityImpl;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.dataCollection.*;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.ResponseEvent;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.ResponseLocalObject;

import java.io.File;

/**
 * Fragment to display responses from a Data Collection Task (General Item)
 */
public class InqDataCollectionTaskFragment extends BaseFragmentActivity implements ListItemClickInterface<ResponseLocalObject> {

    private static final String TAG = "InqDataCollectionTaskFragment";
    private ListView data_collection_tasks_items;
    private InquiryLocalObject inquiry;
    private long generalItemId;

    private ResponsesLazyListAdapter datAdapter;
    private GeneralItemLocalObject genObject;
    private PictureManager man_pic = new PictureManager(this);
    private VideoManager man_vid = new VideoManager(this);
    private AudioInputManager man_aud = new AudioInputManager(this);
    private ValueInputManager man_val = new ValueInputManager(this);
    private TextInputManager man_tex = new TextInputManager(this);

    private final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    private ImageGridFragment frag;

    private File bitmapFile;

    ResponseLocalObject response;

    public InqDataCollectionTaskFragment() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARL.eventBus.register(this);


        if (savedInstanceState != null) {
            INQ.init(this);
            INQ.accounts.syncMyAccountDetails();
            INQ.inquiry.setCurrentInquiry(DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(savedInstanceState.getLong("currentInquiry")));
        }

        setContentView(R.layout.fragment_data_collection_task);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Log.d(TAG,"Entering in data collection number: "+extras.getLong("DataCollectionTask")+"");

            generalItemId = extras.getLong("DataCollectionTask");

            genObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId);
            genObject.getResponses();

            TextView data_collection_tasks_description = (TextView) findViewById(R.id.data_collection_tasks_description_list);
            TextView data_collection_tasks_title = (TextView) findViewById(R.id.data_collection_tasks_title_list);

            data_collection_tasks_title.setText(genObject.getTitle());
            data_collection_tasks_description.setText(genObject.getDescription());

            if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {

                Bundle data = new Bundle();
                data.putLong("generalItemId", generalItemId);

                frag = new ImageGridFragment();
                frag.setArguments(data);

                ft.add(R.id.content_images, frag, TAG);
                ft.commit();
            }

            getActionBar().setTitle(getResources().getString(R.string.actionbar_list_data_collection_task));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ARL.eventBus.unregister(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_data_collection, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_data_collection_image:
                man_pic.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_pic.setGeneralItem(genObject);
                man_pic.takeDataSample(null);
                break;
            case R.id.menu_data_collection_video:
                man_vid.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_vid.setGeneralItem(genObject);
                man_vid.takeDataSample(null);
                break;
            case R.id.menu_data_collection_audio:
                man_aud.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_aud.setGeneralItem(genObject);
                man_aud.takeDataSample(AudioCollectionActivityImpl.class);
                break;
            case R.id.menu_data_collection_text:
                man_tex.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_tex.setGeneralItem(genObject);
                man_tex.takeDataSample(TextInputCollectionActivityImpl.class);
                break;
            case R.id.menu_data_collection_numeric:
                man_val.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_val.setGeneralItem(genObject);
                man_val.takeDataSample(ValueInputCollectionActivityImpl.class);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(View v, int position, ResponseLocalObject object) {
        Intent intent = new Intent(getApplicationContext(), ImageDetailActivity.class);
        intent.putExtra("DataCollectionTask", object.getId());
        intent.putExtra("DataCollectionTaskGeneralItemId", generalItemId);
        intent.putExtra(ImageDetailActivity.RESPONSE_POSITION, position);
        startActivity(intent);
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, ResponseLocalObject object) {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//          public static final int PICTURE_RESULT = 1;
//        public static final int AUDIO_RESULT = 2;
//        public static final int VIDEO_RESULT = 3;
//        public static final int TEXT_RESULT = 4;
//        public static final int VALUE_RESULT = 5;

        switch (requestCode){
            case DataCollectionManager.PICTURE_RESULT:
                man_pic.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.AUDIO_RESULT:
                man_aud.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.VIDEO_RESULT:
                man_vid.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.TEXT_RESULT:
                man_tex.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.VALUE_RESULT:
                man_val.onActivityResult(requestCode, resultCode, data);
                break;
        }

        ImageGridFragment.ImageAdapter adapter = frag.getmAdapter();
        Log.e(TAG, String.valueOf(adapter.getCount()+" "+genObject.getResponses().size()));

        adapter.updateReceiptsList(genObject);

        Log.e(TAG, String.valueOf(adapter.getCount()+" "+genObject.getResponses().size()));

        INQ.responses.syncResponses(INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());
    }

    private void onEventBackgroundThread(ResponseEvent responseEvent){
        Log.e(TAG, " response for "+responseEvent.getRunId());

        genObject.resetResponses();
    }


}
