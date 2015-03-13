package net.wespot.pim.controller.Adapters.Answers;

import android.util.Log;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AbstractDelegator;
import org.celstec.arlearn2.client.InquiryClient;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.InquiryQuestionAnswerLocalObject;
import org.celstec.dao.gen.InquiryQuestionLocalObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * ****************************************************************************
 * Copyright (C) 2015 Open Universiteit Nederland
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
 * Date: 02/03/15
 * ****************************************************************************
 */

public class AnswerDelegator extends AbstractDelegator {
    private static AnswerDelegator instance;
    private AnswerDelegator loggedInAccount;

    private AnswerDelegator() {
        ARL.eventBus.register(this);
    }

    public static AnswerDelegator getInstance() {
        if (instance == null) {
            instance = new AnswerDelegator();
        }
        return instance;
    }

    public void syncAnswers(InquiryLocalObject inquiryLocalObject) {
        ARL.eventBus.post(new SyncAnswersTask(inquiryLocalObject));

    }

    public void onEventAsync(SyncAnswersTask sge) {
        Log.i(SYNC_TAG, "Syncing answers for inquiry " + sge.inquiryLocalObject.getTitle() + " " + sge.inquiryLocalObject.getId());
        String token =returnTokenIfOnline();
        if (token != null) {
            String questions = InquiryClient.getInquiryClient().answers(token, sge.getInquiryLocalObject().getId());
            if (questions == null) return;
            JSONObject json = null;
            AnswerEvent event = null;
            try {
                json = new JSONObject(questions);
                JSONArray array = json.getJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject inqJsonObject = array.getJSONObject(i);
//                    String question = inqJsonObject.getString("question");
//                    Log.i(SYNC_TAG, "Question found " + question);
                    InquiryQuestionAnswerLocalObject inquiryQuestionAnswerLocalObject = new InquiryQuestionAnswerLocalObject();

                    inquiryQuestionAnswerLocalObject.setQuestion(inqJsonObject.getString("question"));
                    inquiryQuestionAnswerLocalObject.setDescription(inqJsonObject.getString("description"));

                    inquiryQuestionAnswerLocalObject.setIdentifier(inqJsonObject.getString("answerId"));
                    inquiryQuestionAnswerLocalObject.setAnswer(inqJsonObject.getString("answer"));

                    inquiryQuestionAnswerLocalObject.setInquiryId(sge.inquiryLocalObject.getId());
                    inquiryQuestionAnswerLocalObject.setInquiryLocalObject(sge.inquiryLocalObject);

                    InquiryQuestionLocalObject inquiryQuestionLocalObject  = DaoConfiguration.getInstance().getInquiryQuestionLocalObjectDao().load(inqJsonObject.getString("questionId"));
                    if (inquiryQuestionLocalObject != null)
                        inquiryQuestionAnswerLocalObject.setInquiryQuestionLocalObject(inquiryQuestionLocalObject);

                    inquiryQuestionAnswerLocalObject.setQuestionId(inqJsonObject.getString("questionId"));

                    DaoConfiguration.getInstance().getInquiryQuestionAnswerLocalObjectDao().insertOrReplace(inquiryQuestionAnswerLocalObject);
                    event = new AnswerEvent();
                    event.setInquiryId(sge.getInquiryLocalObject().getId());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (event != null) ARL.eventBus.post(event);
        }
    }

    public class SyncAnswersTask {
        InquiryLocalObject inquiryLocalObject;
        public SyncAnswersTask(InquiryLocalObject inquiryLocalObject) {
            this.inquiryLocalObject = inquiryLocalObject;
        }

        public InquiryLocalObject getInquiryLocalObject() {
            return inquiryLocalObject;
        }

        public void setInquiryLocalObject(InquiryLocalObject inquiryLocalObject) {
            this.inquiryLocalObject = inquiryLocalObject;
        }
    }

}
