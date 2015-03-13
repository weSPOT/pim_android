package net.wespot.pim.controller.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.Answers.AbstractAnswersQuestionLazyListAdapter;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.InquiryQuestionAnswerLocalObject;

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
 * Date: 04/11/14
 * ****************************************************************************
 */

public class AnswersLazyListAdapter extends AbstractAnswersQuestionLazyListAdapter {

    public AnswersLazyListAdapter(Context context, InquiryLocalObject inquiryLocalObject, String questionId) {
        super(context, inquiryLocalObject.getId(), questionId);
    }

    @Override
    public View newView(Context context, InquiryQuestionAnswerLocalObject item, ViewGroup parent) {
        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.entry_question_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, InquiryQuestionAnswerLocalObject item) {
        TextView firstLineView =(TextView) view.findViewById(R.id.name_entry_question_list);
        firstLineView.setText(android.text.Html.fromHtml(item.getAnswer()).toString());


        ImageView icon = (ImageView) view.findViewById(R.id.inquiry_entry_question_icon);
        icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_question));
    }
}
