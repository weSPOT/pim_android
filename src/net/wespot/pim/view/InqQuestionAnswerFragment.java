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
import android.widget.ListView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.QuestionsLazyListAdapter;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import org.celstec.dao.gen.InquiryQuestionLocalObject;

public class InqQuestionAnswerFragment extends BaseFragmentActivity {

    private ListView listView;
    private TextView questions_title;
    private TextView questions_description;

    private QuestionsLazyListAdapter questionsLazyListAdapter;

    public InqQuestionAnswerFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();

        String questionId = extras.getString("QuestionId");
        InquiryQuestionLocalObject questionLocalObject = DaoConfiguration.getInstance().getInquiryQuestionLocalObjectDao().load(questionId);

        questions_title.setText(questionLocalObject.getTitle());
        questions_description.setText(android.text.Html.fromHtml(questionLocalObject.getDescription()).toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_questions_answer);

        listView = (ListView) findViewById(R.id.question_answers);
        questions_title = (TextView) findViewById(R.id.question_title);
        questions_description = (TextView) findViewById(R.id.question_description);
    }
}
