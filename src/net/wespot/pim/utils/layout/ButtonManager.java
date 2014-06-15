package net.wespot.pim.utils.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.wespot.pim.R;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;

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
public class ButtonManager {

    private Context context;
    private ListItemClickInterface<View> callback;

    public ButtonManager(Context context) {
        this.context = context;
    }

    public LinearLayout.LayoutParams generateLayoutParams(int marginTop) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) context.getResources().getDimension(marginTop), 0, 0);
        return layoutParams;
    }

    public View generateButton(LinearLayout linearLayout, ViewGroup.LayoutParams firstLayoutParams, final int id, int phases_invite_new_friend, int ic_invite_friend, String notification) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.entry_main_list, null);
        assert view != null;
        view.setLayoutParams(firstLayoutParams);

        ((TextView) view.findViewById(R.id.name_entry_list)).setText(context.getResources().getString(phases_invite_new_friend));
        ((TextView) view.findViewById(R.id.notificationText)).setText(notification);
        ((ImageView) view.findViewById(R.id.inquiry_entry_icon)).setImageDrawable(context.getResources().getDrawable(ic_invite_friend));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onListItemClick(v, id, null);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return callback != null && callback.setOnLongClickListener(v, id, null);
            }
        });

        linearLayout.addView(view);
        return view;
    }

    public void setOnListItemClickCallback(ListItemClickInterface<View> callback) {
        this.callback = callback;
    }
}
