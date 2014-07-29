package net.wespot.pim.controller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.wespot.pim.R;
import org.celstec.arlearn2.android.listadapter.AbstractMessagesLazyListAdapter;
import org.celstec.dao.gen.MessageLocalObject;

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
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class MessagesLazyListAdapter extends AbstractMessagesLazyListAdapter {

    public MessagesLazyListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View newView(Context context, MessageLocalObject item, ViewGroup parent) {
        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view =  inflater.inflate(R.layout.entry_messages, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, MessageLocalObject item) {
        TextView firstLineView =(TextView) view.findViewById(R.id.name_entry_list);
        firstLineView.setText(item.getBody());
        ImageView icon = (ImageView) view.findViewById(R.id.inquiry_entry_icon);
        TextView notificationText = (TextView) view.findViewById(R.id.notificationTextInquiry);
    }



}
