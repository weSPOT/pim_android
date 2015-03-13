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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.AnswersLazyListAdapter;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.InquiryQuestionAnswerLocalObject;

public class InqQuestionAnswerFragment extends BaseFragmentActivity implements ListItemClickInterface<InquiryQuestionAnswerLocalObject> {

    private ListView listView;
    private TextView questions_title;
    private TextView answer_description;

    private AnswersLazyListAdapter answersLazyListAdapter;

    public InqQuestionAnswerFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
//
        String questionId = extras.getString("QuestionId");
        String questionTitle = extras.getString("QuestionTitle");
        String questionDescription = extras.getString("QuestionDescription");

        answersLazyListAdapter =  new AnswersLazyListAdapter(this, INQ.inquiry.getCurrentInquiry(), questionId);
        answersLazyListAdapter.setOnListItemClickCallback(this);
        listView.setAdapter(answersLazyListAdapter);

        questions_title.setText(questionTitle);
//        answer_description.setText(questionDescription);
        answer_description.setText(android.text.Html.fromHtml(questionDescription).toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_questions_answer);

        listView = (ListView) findViewById(R.id.question_answers);
        questions_title = (TextView) findViewById(R.id.question_title);
        answer_description = (TextView) findViewById(R.id.question_description);
    }


    @Override
    public void onListItemClick(View v, int position, InquiryQuestionAnswerLocalObject object) {

    }

    @Override
    public boolean setOnLongClickListener(View v, int position, InquiryQuestionAnswerLocalObject object) {
        return false;
    }
}
