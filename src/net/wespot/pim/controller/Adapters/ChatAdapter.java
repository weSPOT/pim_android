package net.wespot.pim.controller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.wespot.pim.R;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.MessageLocalObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
 * Date: 13/10/14
 * ****************************************************************************
 */

public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MessageLocalObject> messages;

    private HashMap<MessageLocalObject, View> messages_views = new HashMap<MessageLocalObject, View>();

    public ChatAdapter(Context context, ArrayList<MessageLocalObject> messages, HashMap<MessageLocalObject, View> messages_views) {
        mContext = context;
        this.messages = messages;
        this.messages_views = messages_views;
    }

    public View getViewFromMessage( MessageLocalObject message){
        return messages_views.get(message);
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageLocalObject message = (MessageLocalObject) this.getItem(position);

        if(INQ.accounts.getLoggedInAccount().getFullId().equals(message.getAuthor())){
            if (!message.getSynced()){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages_not_sync, parent, false);
            }else{
                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages, parent, false);
            }
        }else{
            convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages_others, parent, false);
            ((TextView) convertView.findViewById(R.id.author_entry_list)).setText(message.getAuthor());
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.message);
        TextView timestampTextView = (TextView) convertView.findViewById(R.id.timeStampMessage);

        convertView.setId((int) (long) message.getTime());

        messageTextView.setText(message.getBody());

        Date date = new Date(message.getTime());
        Format format = new SimpleDateFormat("HH:mm:ss");

        timestampTextView.setText(format.format(date));

        if (messages_views.get(message) == null){
            messages_views.put(message, convertView);
        }

        return convertView;
    }
}
