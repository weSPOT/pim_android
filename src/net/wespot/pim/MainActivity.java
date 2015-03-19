package net.wespot.pim;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.utils.RemindTask;
import net.wespot.pim.utils.TimeEvent;
import net.wespot.pim.utils.generic.NetworkHandle;
import net.wespot.pim.utils.layout.ActionBarCurrent;
import net.wespot.pim.utils.layout.ButtonManager;
import net.wespot.pim.utils.layout.ViewItemClickInterface;
import net.wespot.pim.view.*;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.events.MyAccount;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.events.FriendEvent;
import org.celstec.events.InquiryEvent;

import java.util.LinkedList;
import java.util.Timer;


/**
 * ****************************************************************************
 * Copyright (C) 2013-2014 Open Universiteit Nederland
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ActionBarCurrent implements ListItemClickInterface<View> {
    private static final String TAG = "MainActivity";

    private static int numberInquiries;
    private static int numberResponses;
    private static int numberDataCollections;
    private static int numberBadges;
    private static int numberFriends;
    private LinearLayout linearLayout;
    private View myInquiryView;
    private View myMediaView;
    private View myBadges;
    private View myFriends;
    private static final long INTERVAL = 10; /* seconds */

    private LinkedList<InquiryLocalObject> queueInqDatCol;

    private ViewItemClickInterface callback;
    public static Timer timer;
    private static boolean firstTime = true;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Recover in MainActivity > onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "Recover in MainActivity > onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "Recover in MainActivity > onSaveInstanceState");
    }

    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_main);

        if (savedInstanceState != null) {
            INQ.init(this);
            INQ.accounts.syncMyAccountDetails();
        }

        INQ.eventBus.register(this);

        timer = new Timer(true);

        queueInqDatCol = new LinkedList<InquiryLocalObject>();

        numberInquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();
        numberBadges = DaoConfiguration.getInstance().getBadgesLocalObjectDao().loadAll().size();
        numberResponses = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().loadAll().size();
        numberFriends = DaoConfiguration.getInstance().getFriendsLocalObjectDao().loadAll().size();


        linearLayout = (LinearLayout)findViewById(R.id.content_main_screen);

        // Instantiation of the buttonManager
        ButtonManager buttonManager = new ButtonManager(this);
        buttonManager.setOnListItemClickCallback(this);

        // Creation of layout params
        LinearLayout.LayoutParams firstLayoutParams = buttonManager.generateLayoutParams(
                R.dimen.mainscreen_margintop_first,
                (int)getResources().getDimension(R.dimen.mainscreen_margintop_zero));
        LinearLayout.LayoutParams secondLayoutParams = buttonManager.generateLayoutParams(
                R.dimen.mainscreen_margintop_second,
                (int)getResources().getDimension(R.dimen.space_between_list_items));
        LinearLayout.LayoutParams thirdLayoutParams = buttonManager.generateLayoutParams(
                R.dimen.mainscreen_margintop_zero,
                (int)getResources().getDimension(R.dimen.mainscreen_margintop_zero));


        // My Inquiries button_old
        myInquiryView = buttonManager.generateButton(linearLayout,firstLayoutParams, Constants.ID_MYINQUIRIES,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MYINQUIRIES),
                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MYINQUIRIES), String.valueOf(numberInquiries), true);

        // My media button_old
        myMediaView = buttonManager.generateButton(linearLayout,secondLayoutParams, Constants.ID_MYMEDIA,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MYMEDIA),
                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MYMEDIA), String.valueOf(numberResponses), false);

        // Profile button_old
        buttonManager.generateButton(linearLayout, thirdLayoutParams, Constants.ID_PROFILE,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_PROFILE),
                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_PROFILE), "", false);

        // Badges button_old
//        myBadges = buttonManager.generateButton(linearLayout, thirdLayoutParams, Constants.ID_BADGES,
//                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_BADGES),
//                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_BADGES), String.valueOf(numberBadges), false);

        // Friends button_old
        myFriends = buttonManager.generateButton(linearLayout, thirdLayoutParams, Constants.ID_MAIN_FRIENDS,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MAIN_FRIENDS),
                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MAIN_FRIENDS), String.valueOf(numberFriends), true);

//        Toast.makeText(this, "Mobile "+Connectivity.isConnectedMobile(this)+"", Toast.LENGTH_LONG).show();
//        Toast.makeText(this,"Wifi "+Connectivity.isConnectedWifi(this)+"", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListItemClick(View v, int id, View object) {
        switch (id){
            case Constants.ID_MYINQUIRIES:
                Intent intent_inquiries = new Intent(getApplicationContext(), PimInquiriesFragment.class);
                startActivity(intent_inquiries);
                break;
            case Constants.ID_MYMEDIA:
                Intent intent_media = new Intent(getApplicationContext(), InqMyMediaFragment.class);
                startActivity(intent_media);
                break;
            case Constants.ID_PROFILE:
                Intent intent_profile = new Intent(getApplicationContext(), PimProfileFragment.class);
                startActivity(intent_profile);
                break;
            case Constants.ID_BADGES:
                Intent intent_badges = new Intent(getApplicationContext(), PimBadgesFragment.class);
                startActivity(intent_badges);
                break;
            case Constants.ID_MAIN_FRIENDS:
                Intent intent_friends = new Intent(getApplicationContext(), PimFriendsFragment.class);
                startActivity(intent_friends);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "on resume Main activity. Number of inquiries: "+ numberInquiries);

        numberInquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();
    }

    public void onEventMainThread(GeneralItemEvent generalItemEvent){



        numberDataCollections = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().loadAll().size();
        ((TextView)myMediaView.findViewById(R.id.notificationText)).setText(String.valueOf(numberDataCollections));

    }

    public void onEventBackgroundThread(TimeEvent inquiryEvent){
        Log.i(TAG, "time's up");

        if (!queueInqDatCol.isEmpty()){
            INQ.inquiry.syncDataCollectionTasks(queueInqDatCol.remove());
            timer.schedule(new RemindTask(), INTERVAL * 1000);
        }
    }

    public void onEventBackgroundThread(InquiryEvent inquiryEvent){
        InquiryLocalObject inquiryLocalObject = DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(inquiryEvent.getInquiryId());

        timer.purge();

        queueInqDatCol.add(inquiryLocalObject);

        Log.i(TAG, "sync and reset counter 30 second more: "+inquiryLocalObject.getId());
    }

    public void onEventMainThread(InquiryEvent event) {
        numberInquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();

        ((TextView)myInquiryView.findViewById(R.id.notificationText)).setText(String.valueOf(numberInquiries));

        Log.i(TAG, "onEventMainThread. Number of inquiries: " + numberInquiries);
    }

//    public void onEventMainThread(BadgeEvent event) {
//        numberBadges = DaoConfiguration.getInstance().getBadgesLocalObjectDao().loadAll().size();
//
//        ((TextView)myBadges.findViewById(R.id.notificationText)).setText(String.valueOf(numberBadges));
//
//        Log.i(TAG, "onEventMainThread. Number of badges: " + numberBadges);
//    }

    public void onEventMainThread(FriendEvent event) {
        numberFriends = DaoConfiguration.getInstance().getFriendsLocalObjectDao().loadAll().size();

        ((TextView)myFriends.findViewById(R.id.notificationText)).setText(String.valueOf(numberFriends));

        Log.i(TAG, "onEventMainThread. Number of friends: " + numberFriends);
    }

    public void onEventBackgroundThread(MyAccount myAccount){
        INQ.inquiry.syncInquiries();
//        INQ.badges.syncBadges();

        INQ.friendsDelegator.syncFriends();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        INQ.eventBus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_default, menu);

        return super.onCreateOptionsMenu(menu);
    }


    public void onEventMainThread(NetworkHandle.NetworkResult result) {
        if (result.isResult()) {
            Log.d(TAG, "Before logout: " + INQ.accounts.isAuthenticated());
            INQ.accounts.disAuthenticate();
            INQ.properties.setAccount(0l);

            Intent myIntent = new Intent(this, SplashActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
            startActivity(myIntent);
            finish();

            Log.d(TAG, "After logout: " + INQ.accounts.isAuthenticated());
        } else {
            notOnline();
        }
    }

    public void notOnline(){
        Toast.makeText(this, "Logout functionality has been disabled to save your offline progress", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:

                if (INQ.isOnline()) {
                    INQ.eventBus.post(new NetworkHandle());
                } else {
                    notOnline();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onEventAsync(NetworkHandle networkTest) {
        networkTest.executeTest();
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, View object) {
        return false;
    }
}
