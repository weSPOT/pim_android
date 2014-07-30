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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.utils.RetrieveMessageTask;
import net.wespot.pim.utils.TimeMessageEvent;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MessageEvent;
import org.celstec.dao.gen.MessageLocalObject;
import org.celstec.dao.gen.MessageLocalObjectDao;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class InqCommunicateFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "InqCommunicateFragment";
    private EditText message;
    private ImageButton send;
    private ScrollView scroll;

    public static Timer timer;

    List<MessageLocalObject> messageLocalObjectList;
    List<MessageLocalObject> messageLocalObjectList_newMessages;

    private ViewGroup mContainerView;
    private static final long INTERVAL = 5; /* seconds */


    public InqCommunicateFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARL.eventBus.register(this);
        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());
        //TODO issues when resuming
        messageLocalObjectList = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        timer = new Timer();
        timer.schedule(new RetrieveMessageTask(), INTERVAL * 1000, INTERVAL * 1000);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (messageLocalObjectList.size() > 0) {
            getActivity().findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_section_communicate, container, false);

        mContainerView = (ViewGroup) rootView.findViewById(R.id.list_threads);
        message = (EditText) rootView.findViewById(R.id.communication_enter_message);
        send = (ImageButton) rootView.findViewById(R.id.communication_enter_message_button);

        scroll = (ScrollView) rootView.findViewById(R.id.list_threads_scroll);
        send.requestFocus();

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

                    addMessage(messageLocalObject);
                    message.setText("");
//                    INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());

                }
            }
        });

        for (MessageLocalObject messageLocalObject : messageLocalObjectList) {
            addMessage(messageLocalObject);
        }

        return rootView;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void addMessage(MessageLocalObject messageLocalObject) {

        final ViewGroup newView;

        if (INQ.accounts.getLoggedInAccount().getFullId().equals(messageLocalObject.getAuthor())) {
            newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                    R.layout.entry_messages, mContainerView, false);
        } else {
            newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                    R.layout.entry_messages_others, mContainerView, false);
        }

        ((TextView) newView.findViewById(R.id.name_entry_list)).setText(messageLocalObject.getBody().toString());

        Date date = new Date(messageLocalObject.getTime());
        Format format = new SimpleDateFormat("HH:mm:ss");
        ((TextView) newView.findViewById(R.id.timeStampMessage)).setText(format.format(date));

        mContainerView.addView(newView, mContainerView.getChildCount());
        scrollDown();

    }

    private void onEventMainThread(TimeMessageEvent timeMessageEvent) {
        Log.e(TAG, "retrieve messages");

        INQ.messages.syncMessagesForDefaultThread(INQ.inquiry.getCurrentInquiry().getRunId());
        messageLocalObjectList_newMessages = DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                .where(MessageLocalObjectDao.Properties.RunId.eq(INQ.inquiry.getCurrentInquiry().getRunId()))
                .orderAsc(MessageLocalObjectDao.Properties.Time)
                .list();

        messageLocalObjectList_newMessages.removeAll(messageLocalObjectList);


        for (MessageLocalObject messageLocalObject : messageLocalObjectList_newMessages) {
            if(!INQ.accounts.getLoggedInAccount().getFullId().equals(messageLocalObject.getAuthor())){
                addMessage(messageLocalObject);
            }
            messageLocalObjectList.add(messageLocalObject);
            messageLocalObjectList_newMessages.remove(messageLocalObject);
        }
    }

    private void onEventBackgroundThread(MessageEvent messageEvent) {
        Log.e(TAG, "message synced: " + messageEvent.getRunId());
    }

    @Override
    public void onDestroy() {
        ARL.eventBus.unregister(this);
        timer.cancel();
        super.onDestroy();
    }

    void scrollDown() {
        Thread scrollThread = new Thread() {
            public void run() {
                try {
                    sleep(500);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            scroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scrollThread.start();
    }
}

