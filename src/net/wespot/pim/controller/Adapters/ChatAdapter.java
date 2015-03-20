package net.wespot.pim.controller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.AccountLocalObject;
import org.celstec.dao.gen.AccountLocalObjectDao;
import org.celstec.dao.gen.MessageLocalObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    private static final String TAG = "ChatAdapter";
    private Context mContext;
    private ArrayList<MessageLocalObject> messages;

    private HashMap<MessageLocalObject, View> messages_views = new HashMap<MessageLocalObject, View>();
    private HashMap<String, AccountLocalObject> accounts = new HashMap<String, AccountLocalObject>();

    public ChatAdapter(Context context) {
        mContext = context;
    }


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

        convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages, parent, false);

        try{
            if(INQ.accounts.getLoggedInAccount().getFullId().equals(message.getAuthor())){
//            if (!message.getRead()){
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages_not_sync, parent, false);
//            }else{
                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages, parent, false);
//            }
            }else{
                convertView = LayoutInflater.from(mContext).inflate(R.layout.entry_messages_others, parent, false);
                ((TextView) convertView.findViewById(R.id.author_entry_list)).setText(getNameUser(message.getAuthor()));
            }
        }catch (Exception e){
            System.out.println("Exception while retrieving the author of the message: " + e);
        }

        TextView messageTextView;
        TextView timestampTextView;

        if(convertView != null){
            messageTextView = (TextView) convertView.findViewById(R.id.message);
            timestampTextView = (TextView) convertView.findViewById(R.id.timeStampMessage);

            convertView.setId((int) (long) message.getTime());

            messageTextView.setText(message.getBody());

            Date date = new Date(message.getTime());
            Format format = new SimpleDateFormat("HH:mm:ss dd-MMM-y");

            timestampTextView.setText(format.format(date));
        }

        if (messages_views.get(message) == null){
            messages_views.put(message, convertView);
        }

        return convertView;
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


    public HashMap<MessageLocalObject, View> getMessages_views() {
        return messages_views;
    }

    public void setMessages_views(HashMap<MessageLocalObject, View> messages_views) {
        this.messages_views = messages_views;
    }

    public ArrayList<MessageLocalObject> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageLocalObject> messages) {
        this.messages = messages;
    }

}
