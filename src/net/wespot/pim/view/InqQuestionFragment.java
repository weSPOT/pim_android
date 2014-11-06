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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.QuestionsLazyListAdapter;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.InquiryQuestionLocalObject;

public class InqQuestionFragment extends Fragment implements ListItemClickInterface<InquiryQuestionLocalObject> {

    private static final Object LIST_STATE = "j";
    private ListView listView;
    private TextView text_default;
    private TextView dataCollectionTasksTitleList;

    private Parcelable mListState;

    private QuestionsLazyListAdapter questionsLazyListAdapter;
    private Bundle state;

    public InqQuestionFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        questionsLazyListAdapter =  new QuestionsLazyListAdapter(this.getActivity());
        questionsLazyListAdapter.setOnListItemClickCallback(this);
        listView.setAdapter(questionsLazyListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_questions, container, false);
        listView = (ListView) rootView.findViewById(R.id.questions);
        text_default = (TextView) rootView.findViewById(R.id.text_default);
        dataCollectionTasksTitleList = (TextView) rootView.findViewById(R.id.questions_title_list);
        return rootView;
    }

    @Override
    public void onListItemClick(View v, int position, InquiryQuestionLocalObject object) {
        Intent intent = null;
        intent = new Intent(getActivity(), InqQuestionAnswerFragment.class);
        intent.putExtra("QuestionId", object.getIdentifier());
        startActivity(intent);
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, InquiryQuestionLocalObject object) {
        return false;
    }
}
