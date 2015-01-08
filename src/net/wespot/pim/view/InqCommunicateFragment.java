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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.ChatAdapter;
import net.wespot.pim.utils.RetrieveMessageTask;
import net.wespot.pim.utils.TimeMessageEvent;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MessageEvent;
import org.celstec.dao.gen.MessageLocalObject;
import org.celstec.dao.gen.MessageLocalObjectDao;

import java.util.*;

public class InqCommunicateFragment extends Fragment {

    private static final String TAG = "InqCommunicateFragment";
    private EditText message;
    private ImageButton send;
    private View rootView;

    private ListView listViewMessages;

    ChatAdapter chatAdapter;
    private HashMap<MessageLocalObject, View> messages_views = new HashMap<MessageLocalObject, View>();
    ArrayList<MessageLocalObject> messages;

    public static Timer timer;

    List<MessageLocalObject> messageLocalObjectList;
    List<MessageLocalObject> messageLocalObjectList_newMessages;

    Map<String, String> accountNamesID = new HashMap<String, String>();

    private ViewGroup mContainerView;
    private static final long INTERVAL = 5; /* seconds */


    public InqCommunicateFragment() {
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
        outState.putLong("currentInquiry", INQ.inquiry.getCurrentInquiry().getId());
        if(INQ.inquiry.getCurrentInquiry().getRunLocalObject()!=null){
            outState.putLong("currentInquiryRunLocalObject", INQ.inquiry.getCurrentInquiry().getRunLocalObject().getId());
            Log.i(TAG, "Recover in InqCommunicateFragment > onSaveInstanceState & current inq = null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_section_communicate, container, false);

        message = (EditText) rootView.findViewById(R.id.communication_enter_message);
        send = (ImageButton) rootView.findViewById(R.id.communication_enter_message_button);
        listViewMessages = (ListView) rootView.findViewById(R.id.list_messages);
        listViewMessages.setAdapter(chatAdapter);

//        send.requestFocus();

//        disableSoftKeyboard(message);
//
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
//
//        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(message.getWindowToken(), 0);

//        InputMethodService keyboard = new InputMethodService();
//        keyboard.hideWindow();


        message.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

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
//                    new SendMessage().execute();
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });
        return rootView;
    }

    public static void disableSoftKeyboard(final EditText v) {
        v.setInputType(InputType.TYPE_NULL);
        if (Build.VERSION.SDK_INT >= 11) {
            v.setRawInputType(InputType.TYPE_CLASS_TEXT);
            v.setTextIsSelectable(true);
        } else {
            v.setRawInputType(InputType.TYPE_NULL);
            v.setFocusable(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            INQ.init(getActivity());
            INQ.accounts.syncMyAccountDetails();
            INQ.inquiry.setCurrentInquiry(DaoConfiguration.getInstance().getInquiryLocalObjectDao().load(savedInstanceState.getLong("currentInquiry")));
        }

        messages = new ArrayList<MessageLocalObject>();

        ARL.eventBus.register(this);
        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());

        messageLocalObjectList = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        timer = new Timer();
        timer.schedule(new RetrieveMessageTask(), INTERVAL * 1000, INTERVAL * 1000);
        Log.e(TAG, "timer");

        for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
            messages.add(messageLocalObject);
        }

        chatAdapter = new ChatAdapter(getActivity(), messages, messages_views);
    }

    public synchronized void onEventMainThread(TimeMessageEvent timeMessageEvent) {
        Log.i(TAG, "retrieve messages");

        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());

        messageLocalObjectList_newMessages = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        if (messageLocalObjectList_newMessages.size() != messageLocalObjectList.size()){

            messageLocalObjectList_newMessages.removeAll(messageLocalObjectList);
            for (MessageLocalObject messageLocalObject : messageLocalObjectList_newMessages) {

                if (!messages_views.containsKey(messageLocalObject)){
                    messages.add(messageLocalObject);
                }else{
                    ViewGroup viewUpdate = (ViewGroup) chatAdapter.getViewFromMessage(messageLocalObject);
                    if (viewUpdate != null){
                        viewUpdate.inflate(getActivity(),R.layout.entry_messages, null);
                        viewUpdate.invalidate();
                    }
                }
                messageLocalObjectList.add(messageLocalObject);
                messageLocalObjectList_newMessages.remove(messageLocalObject);
            }

            chatAdapter.notifyDataSetChanged();
        }
    }

    public void onEventBackgroundThread(MessageEvent messageEvent) {
        Log.e(TAG, "message synced: " + messageEvent.getRunId());
    }

    @Override
    public void onDestroy() {
        ARL.eventBus.unregister(this);
        timer.cancel();
        super.onDestroy();
    }

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

