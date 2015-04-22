package net.wespot.pim.controller.Adapters.Answers;

import android.content.Context;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.LazyListAdapter;
import org.celstec.dao.gen.InquiryQuestionAnswerLocalObject;
import org.celstec.dao.gen.InquiryQuestionAnswerLocalObjectDao;
import org.celstec.dao.gen.InquiryQuestionLocalObjectDao;
import org.celstec.events.QuestionEvent;

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

public abstract class AbstractAnswersQuestionLazyListAdapter extends LazyListAdapter<InquiryQuestionAnswerLocalObject> {

    private QueryBuilder qb;

    public AbstractAnswersQuestionLazyListAdapter(Context context) {
        super(context);
        InquiryQuestionAnswerLocalObjectDao dao = DaoConfiguration.getInstance().getSession().getInquiryQuestionAnswerLocalObjectDao();
        qb = dao.queryBuilder().orderAsc(InquiryQuestionLocalObjectDao.Properties.Identifier);
        ARL.eventBus.register(this);
        setLazyList(qb.listLazy());
    }

    public AbstractAnswersQuestionLazyListAdapter(Context context, Long inquiryId) {
        super(context);
        InquiryQuestionAnswerLocalObjectDao dao = DaoConfiguration.getInstance().getSession().getInquiryQuestionAnswerLocalObjectDao();
        qb = dao.queryBuilder()
                .where(InquiryQuestionLocalObjectDao.Properties.InquiryId.eq(inquiryId))
                .orderAsc(InquiryQuestionLocalObjectDao.Properties.Identifier);
        ARL.eventBus.register(this);
        setLazyList(qb.listLazy());
    }

    public AbstractAnswersQuestionLazyListAdapter(Context context, Long inquiryId, String questionId) {
        super(context);
        InquiryQuestionAnswerLocalObjectDao dao = DaoConfiguration.getInstance().getSession().getInquiryQuestionAnswerLocalObjectDao();

        qb = dao.queryBuilder();
        qb.orderAsc(InquiryQuestionAnswerLocalObjectDao.Properties.Identifier)
                .where(
                        qb.and(
                                InquiryQuestionAnswerLocalObjectDao.Properties.InquiryId.eq(inquiryId),
                                InquiryQuestionAnswerLocalObjectDao.Properties.QuestionId.eq(questionId)
                        )
                );

        ARL.eventBus.register(this);
        setLazyList(qb.listLazy());
    }

    public void onEventMainThread(QuestionEvent event) {
        if (lazyList != null) lazyList.close();
        setLazyList(qb.listLazy());
        notifyDataSetChanged();
    }

    public void close() {
        if (lazyList != null)lazyList.close();
        ARL.eventBus.unregister(this);
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && lazyList != null) {
            InquiryQuestionAnswerLocalObject item = lazyList.get(position);
            if (item != null) {
                return hash(item.getIdentifier());
            } else {
                return 0;
            }
        }
        return 0;

    }
    public static long hash(String string) {
        long h = 1125899906842597L; // prime
        int len = string.length();

        for (int i = 0; i < len; i++) {
            h = 31*h + string.charAt(i);
        }
        return h;
    }
}

