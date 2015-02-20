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

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import daoBase.DaoConfiguration;
import net.wespot.pim.MainActivity;
import net.wespot.pim.R;
import net.wespot.pim.SplashActivity;
import net.wespot.pim.controller.Adapters.ChatAdapter;
import net.wespot.pim.controller.InquiryPhasesActivity;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MessageEvent;
import org.celstec.arlearn2.android.gcm.NotificationListenerInterface;
import org.celstec.dao.gen.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InqCommunicateFragment extends Fragment implements NotificationListenerInterface {

    private static final String TAG = "InqCommunicateFragment";
    private static final String NUMBER = "0123";
    public static final String INQUIRY_ID = "runId";
    private static final String COME_FROM_NOTIFICATION = "comeFromNotification";
    private static final String NO_ENTER = "no_enter";
    private EditText message;
    private ImageButton send;
    private View rootView;

    private ListView listViewMessages;

    private int numMessages = 0;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private NotificationCompat.InboxStyle mNotificationStyle;

    private HashMap<String, AccountLocalObject> accounts = new HashMap<String, AccountLocalObject>();

    private ChatAdapter chatAdapter;
    private HashMap<MessageLocalObject, View> messages_views = new HashMap<MessageLocalObject, View>();
    ArrayList<MessageLocalObject> messages;

    private List<MessageLocalObject> messageLocalObjectList;

    private static final String CURRENT_INQUIRY = "currentInquiry";
    private static final String CURRENT_INQUIRY_RUN = "currentInquiryRun";
    private static final String CURRENT_MESSAGES = "currentMessages";

    public InqCommunicateFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(CURRENT_INQUIRY, INQ.inquiry.getCurrentInquiry().getId());
        outState.putLong(CURRENT_INQUIRY_RUN, INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "entering communication fragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_section_communicate, container, false);

        message = (EditText) rootView.findViewById(R.id.communication_enter_message);
        send = (ImageButton) rootView.findViewById(R.id.communication_enter_message_button);
        listViewMessages = (ListView) rootView.findViewById(R.id.list_messages);
        listViewMessages.setAdapter(chatAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.getText().toString().equals("")) {
                    MessageLocalObject messageLocalObject = new MessageLocalObject();
                    messageLocalObject.setBody(message.getText().toString());
                    messageLocalObject.setAuthor(INQ.accounts.getLoggedInAccount().getFullId());
                    messageLocalObject.setTime(INQ.time.getServerTime());
                    messageLocalObject.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                    messageLocalObject.setSynced(false);
                    messageLocalObject.setSubject("");

                    DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
                    INQ.messages.postMessagesToServer();

                    messages.add(messageLocalObject);
                    messages_views.put(messageLocalObject, null);

                    message.setText("");
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO issue here when resuming the application
        // It seems that the ARL.getContext() is null

        INQ.init(ARL.getContext());
        ARL.eventBus.register(this);
        INQ.accounts.syncMyAccountDetails();
        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());

        if (savedInstanceState != null) {
            INQ.inquiry.setCurrentInquiry(DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(
                    savedInstanceState.getLong(CURRENT_INQUIRY)));

            INQ.inquiry.getCurrentInquiry().setRunLocalObject(DaoConfiguration.getInstance().getRunLocalObjectDao().load(
                    savedInstanceState.getLong(CURRENT_INQUIRY_RUN) ));

            Log.e(TAG, "recovery from InqCommunicateFragment");
        }

        messages = new ArrayList<MessageLocalObject>();

        messageLocalObjectList = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
            messages.add(messageLocalObject);
            messageLocalObject.setRead(true);
            DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
        }

        numMessages = 0;
        mBuilder = null;

        chatAdapter = new ChatAdapter(getActivity(), messages, messages_views);
    }

    @Override
    public boolean acceptNotificationType(String notificationType) {
        return true;
    }

    @Override
    public void handleNotification(HashMap<String, String> map) {
        if (map.containsKey("type")) {
            if (map.containsValue("org.celstec.arlearn2.beans.notification.MessageNotification")) {
                Log.i(TAG, "retrieve messages");
                if (!ARL.eventBus.isRegistered(this)){
                    ARL.eventBus.register(this);
                }
                Long runId = Long.parseLong(map.get("runId"));
                INQ.runs.syncRun(runId);
                INQ.messages.syncMessagesForDefaultThread(runId);
            }
        }
    }

    public synchronized void onEventMainThread(MessageEvent messageEvent) {
        Log.e(TAG, "message synced: " + messageEvent.getRunId());

        receiveMessage(messageEvent.getRunId());
    }

    private void receiveMessage(Long runId) {
        messageLocalObjectList = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(runId))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        ////////////////////////////////////
        // First check if the PIM is running
        ////////////////////////////////////
        if (isPimRunning()){

            //////////////////////////////////////////
            // If there is no INQ.inquiry we create it
            //////////////////////////////////////////
            if (INQ.inquiry.getCurrentInquiry() == null){

                InquiryLocalObject inquiryLocalObject = DaoConfiguration.getInstance().getInquiryLocalObjectDao().queryBuilder().where(
                    InquiryLocalObjectDao.Properties.RunId.eq(runId)
                ).list().get(0);

                if (!inquiryLocalObject.equals(null)) {
                    INQ.inquiry.setCurrentInquiry(inquiryLocalObject);
                }
            }

            /////////////////////////////////////////////////////
            // If the current run is the same as the that belongs
            // to the message display messages
            /////////////////////////////////////////////////////
            if (INQ.inquiry.getCurrentInquiry().getRunId() == runId){

                for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
                    /////////////////////////////////////////////////////
                    // For those messages that are not shown process them
                    /////////////////////////////////////////////////////
                    if (messageLocalObject.getRead() == null) {
                        // No notification but we need to update the view
                        //////////////////
                        // If the message
                        //////////////////
                        ///////////////////////////////////////////////////////////////////////
                        // We are inside the inquiry but maybe we need an internal notification
                        ///////////////////////////////////////////////////////////////////////




                        if (!messages_views.containsKey(messageLocalObject)){
                            if (messages != null){
                                messages.add(messageLocalObject);
                            }
                        }else{
                            ViewGroup viewUpdate = (ViewGroup) chatAdapter.getViewFromMessage(messageLocalObject);
                            if (viewUpdate != null){
                                viewUpdate.inflate(getActivity(),R.layout.entry_messages, null);
                                viewUpdate.invalidate();
                            }
                        }
                        if (chatAdapter != null){
                            chatAdapter.notifyDataSetChanged();
                            messageLocalObject.setRead(true);
                            DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
                        }




                    }
                }
            }else{
                for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
                    if (messageLocalObject.getRead() == null) {

                        createNotification(messageLocalObject, runId);

                        ///////////////////////////////////////////////////////
                        // We need to mark them read when click on notification
                        ///////////////////////////////////////////////////////
                        messageLocalObject.setRead(true);

                        DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
                    }
                }
            }
        }else{

            for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
                if (messageLocalObject.getRead() == null) {

                    createNotification(messageLocalObject, runId);

                    messageLocalObject.setRead(true);

                    DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
                }
            }
        }
    }

    private void createNotification(MessageLocalObject messageLocalObject, Long runId) {

        if (mBuilder == null){

            if (mNotificationStyle == null){
                mNotificationStyle = new NotificationCompat.InboxStyle();
            }

            InquiryLocalObject inquiryLocalObject = DaoConfiguration.getInstance().getInquiryLocalObjectDao().queryBuilder().where(
                    InquiryLocalObjectDao.Properties.RunId.eq(runId)
            ).list().get(0);

            mBuilder = new NotificationCompat.Builder(ARL.getContext())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(inquiryLocalObject.getTitle())
                    .setAutoCancel(true)
                    .setSortKey("0")
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                    .setStyle(mNotificationStyle
                            .addLine(getNameUser(messageLocalObject.getAuthor()) + ": " + messageLocalObject.getBody())
                            .setSummaryText(++numMessages != 1 ? numMessages + " new messages" : numMessages + " new message"));

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(ARL.getContext(), InquiryPhasesActivity.class);
            resultIntent.putExtra(INQUIRY_ID, inquiryLocalObject.getId());

            Intent parent = new Intent(ARL.getContext(), PimInquiriesFragment.class);
            Intent parent1 = new Intent(ARL.getContext(), MainActivity.class);
            Intent parent2 = new Intent(ARL.getContext(), SplashActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ARL.getContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addNextIntentWithParentStack(parent2);
            stackBuilder.addNextIntentWithParentStack(parent1);
            stackBuilder.addNextIntentWithParentStack(parent);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
        }else{

            mNotificationStyle
                    .addLine(getNameUser(messageLocalObject.getAuthor()) + ": " + messageLocalObject.getBody())
                    .setSummaryText(++numMessages!= 1?numMessages + " new messages":numMessages + " new message");
        }

        if (mNotificationManager == null){
            mNotificationManager = (NotificationManager) ARL.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // mId allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(NUMBER), mBuilder.build());
    }


    public boolean isPimRunning(){
        ActivityManager activityManager = (ActivityManager) ARL.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(ARL.getContext().getPackageName().toString())) {
            return true;
        }
        else{
            return false;
        }
    }



    public String getNameUser(String author) {
        if (accounts.containsKey(author)){
            return accounts.get(author).getName();
        }else {
            List<AccountLocalObject> list = DaoConfiguration.getInstance().getAccountLocalObjectDao().queryBuilder()
                    .where(AccountLocalObjectDao.Properties.FullId.eq(author)).list();

            if (list.size() > 0){
                if (list.get(0) != null){
                    accounts.put(author, list.get(0) );
                    return list.get(0).getName();
                }else{
                    return author;
                }
            }else{
                return author;
            }
        }
    }


    @Override
    public void onDestroy() {
        ARL.eventBus.unregister(this);
        mBuilder = null;
        super.onDestroy();
    }

//    public synchronized void onEventMainThread(TimeMessageEvent timeMessageEvent) {
////        Log.i(TAG, "retrieve messages");
////
////        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());
////
////        messageLocalObjectList_newMessages = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
////                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
////                .orderAsc(MessageLocalObjectDao.Properties.Time)
////                .list();
////
////        if (messageLocalObjectList_newMessages.size() != messageLocalObjectList.size()){
////
////            messageLocalObjectList_newMessages.removeAll(messageLocalObjectList);
////            for (MessageLocalObject messageLocalObject : messageLocalObjectList_newMessages) {
////
////                if (!messages_views.containsKey(messageLocalObject)){
////                    messages.add(messageLocalObject);
////                }else{
////                    ViewGroup viewUpdate = (ViewGroup) chatAdapter.getViewFromMessage(messageLocalObject);
////                    if (viewUpdate != null){
////                        viewUpdate.inflate(getActivity(),R.layout.entry_messages, null);
////                        viewUpdate.invalidate();
////                    }
////                }
////                messageLocalObjectList.add(messageLocalObject);
////                messageLocalObjectList_newMessages.remove(messageLocalObject);
////            }
////
////            chatAdapter.notifyDataSetChanged();
////        }
//    }

//    public static void disableSoftKeyboard(final EditText v) {
//        v.setInputType(InputType.TYPE_NULL);
//        if (Build.VERSION.SDK_INT >= 11) {
//            v.setRawInputType(InputType.TYPE_CLASS_TEXT);
//            v.setTextIsSelectable(true);
//        } else {
//            v.setRawInputType(InputType.TYPE_NULL);
//            v.setFocusable(true);
//        }
//    }

//    private class SendMessage extends AsyncTask<Void, String, String>
//    {
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//                Thread.sleep(2000); //simulate a network call
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            this.publishProgress(String.format("%s started writing", INQ.accounts.getLoggedInAccount().getFullId()));
//            try {
//                Thread.sleep(2000); //simulate a network call
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            this.publishProgress(String.format("%s has entered text", INQ.accounts.getLoggedInAccount().getFullId()));
//            try {
//                Thread.sleep(3000);//simulate a network call
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return "";
//        }
//        @Override
//        public void onProgressUpdate(String... v) {
//
//            if(messages.get(messages.size()-1).isStatusMessage)//check wether we have already added a status message
//            {
//                messages.get(messages.size()-1).setMessage(v[0]); //update the status for that
//                chatAdapter.notifyDataSetChanged();
////                getListView().setSelection(messages.size()-1);
//            }
//            else{
////                addNewMessage(new Message(true,v[0])); //add new message, if there is no existing status message
//            }
//        }
//        @Override
//        protected void onPostExecute(String text) {
//            if(messages.get(messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
//            {
//                messages.remove(messages.size()-1);
//            }
//        }
//    }


}



