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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.QuestionsLazyListAdapter;
import net.wespot.pim.utils.layout.QuestionDialogFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.db.PropertiesAdapter;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.client.InquiryClient;
import org.celstec.dao.gen.InquiryQuestionLocalObject;

public class InqQuestionFragment extends Fragment implements ListItemClickInterface<InquiryQuestionLocalObject> {

    private static final Object LIST_STATE = "j";
    private ListView listView;
    private TextView text_default;
    private TextView dataCollectionTasksTitleList;

    private Parcelable mListState;

    private QuestionsLazyListAdapter questionsLazyListAdapter;
    private Bundle state;

    private QuestionDialogFragment dialog;
    private static final int DIALOG_FRAGMENT = 0;
    private String TAG = "InqQuestionFragment";

    private class CreateQuestionObject {
        public InquiryQuestionLocalObject question;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    public InqQuestionFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        questionsLazyListAdapter =  new QuestionsLazyListAdapter(this.getActivity(), INQ.inquiry.getCurrentInquiry());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inquiry, menu);

        menu.setGroupVisible(R.id.actions_general, false);
        menu.setGroupVisible(R.id.actions_wonder_moment, false);
        menu.setGroupVisible(R.id.actions_data_collection, false);
        menu.setGroupVisible(R.id.actions_questions, true);
        menu.setGroupVisible(R.id.actions_friends, false);
        menu.setGroupVisible(R.id.actions_usersite, false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_question:
                create_question();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void create_question() {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialog = new QuestionDialogFragment();

        dialog.setTargetFragment(this, DIALOG_FRAGMENT);

        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                    Log.i(TAG, "ok code");

                    InquiryQuestionLocalObject questionLocalObject = new InquiryQuestionLocalObject();
                    questionLocalObject.setDescription(data.getExtras().get(QuestionDialogFragment.DESCRIPTION).toString());
                    questionLocalObject.setTitle(data.getExtras().get(QuestionDialogFragment.TITLE).toString());
                    questionLocalObject.setInquiryId(INQ.inquiry.getCurrentInquiry().getId());
                    questionLocalObject.setInquiryLocalObject(INQ.inquiry.getCurrentInquiry());
                    questionLocalObject.setTags(data.getExtras().get(QuestionDialogFragment.TAGS).toString().equals("") ? "-": data.getExtras().get(QuestionDialogFragment.TAGS).toString());

                    CreateQuestionObject createQuestionObject = new CreateQuestionObject();
                    createQuestionObject.question = questionLocalObject;

                    ARL.eventBus.register(this);
                    ARL.eventBus.post(createQuestionObject);


                    Toast.makeText(getActivity(), getResources().getString(R.string.question_sync), Toast.LENGTH_SHORT).show();
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                    Log.i(TAG, "cancel code");
                }
                break;
        }
    }

    private synchronized void onEventBackgroundThread(CreateQuestionObject questionObject){
        PropertiesAdapter pa = PropertiesAdapter.getInstance();
        if (pa != null) {
            String token = pa.getAuthToken();
            if (token != null && ARL.isOnline()) {
                InquiryClient.getInquiryClient().createQuestions(token, questionObject.question.getInquiryId(), questionObject.question.getTitle(), questionObject.question.getDescription(), INQ.accounts.getLoggedInAccount(),questionObject.question.getTags() );
                INQ.questions.syncQuestions(INQ.inquiry.getCurrentInquiry());
                Log.e(TAG, "create and sync question");

            }
        }
    }


    @Override
    public boolean setOnLongClickListener(View v, int position, InquiryQuestionLocalObject object) {
        return false;
    }
}
