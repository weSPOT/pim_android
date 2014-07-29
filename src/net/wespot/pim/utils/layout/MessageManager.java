package net.wespot.pim.utils.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.wespot.pim.R;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.MessageLocalObject;

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
 * ****************************************************************************
 */
public class MessageManager {

    private Context context;

    public MessageManager(Context context) {
        this.context = context;
    }

    public LinearLayout.LayoutParams generateLayoutParams(int marginTop, int marginBottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(-1, (int) context.getResources().getDimension(marginTop), -2, marginBottom);
        return layoutParams;
    }

    public View generateMessage(ViewGroup linearLayout, ViewGroup.LayoutParams firstLayoutParams, MessageLocalObject messageLocalObject) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = null;
        if (INQ.accounts.getLoggedInAccount().getFullId().equals(messageLocalObject.getAuthor())){
            view = inflater.inflate(R.layout.entry_messages, null);
        }else {
            view = inflater.inflate(R.layout.entry_messages_others, null);
        }

        assert view != null;
        if (firstLayoutParams != null){
            view.setLayoutParams(firstLayoutParams);
        }
        ((TextView) view.findViewById(R.id.name_entry_list)).setText(messageLocalObject.getBody().toString());

//        linearLayout.addView(view);

        linearLayout.addView(view, linearLayout.getChildCount());

        return view;

    }
}
