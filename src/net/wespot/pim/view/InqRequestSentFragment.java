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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.UserSiteAdapter;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.events.SentFriendRequestsEvent;
import org.celstec.events.objects.User;

import java.util.ArrayList;
import java.util.Iterator;

@SuppressLint("NewApi")
public class InqRequestSentFragment extends BaseFragmentActivity {

    private ListView sent_request_listView = null;
    private ArrayList<User> resquestedFriends = new ArrayList<User>();
    private View mLoadingView;
    private int mShortAnimationDuration;

    private Boolean sentRequesFriendLoad =false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARL.eventBus.register(this);

        setContentView(R.layout.fragment_request_sent);
        sent_request_listView = (ListView) findViewById(R.id.list_sent_request_usersite);
        mLoadingView = findViewById(R.id.loading_spinner);

        sent_request_listView.setVisibility(View.GONE);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        setTitle(R.string.friends_title_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inquiry, menu);

        menu.setGroupVisible(R.id.actions_general, false);
        menu.setGroupVisible(R.id.actions_wonder_moment, false);
        menu.setGroupVisible(R.id.actions_data_collection, false);
        menu.setGroupVisible(R.id.actions_friends, false);
        menu.setGroupVisible(R.id.actions_usersite, false);
        menu.setGroupVisible(R.id.actions_questions, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_usersite_search:
                Toast.makeText(this, getResources().getString(R.string.usersite_menu_search), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void onEventMainThread(SentFriendRequestsEvent elggUsersEvent){
        sent_request_listView.setAlpha(0f);
        sent_request_listView.setVisibility(View.VISIBLE);

        sent_request_listView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);


        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });

        Iterator a = elggUsersEvent.getIterator();

        for (Iterator iterator = a; iterator.hasNext();)
        {
            resquestedFriends.add((User) iterator.next());
        }

        UserSiteAdapter contactAdapter = new UserSiteAdapter(getApplicationContext(), 0, resquestedFriends, true);
        sent_request_listView.setAdapter(contactAdapter);
    }



    public void onDestroy(){
        super.onDestroy();
        ARL.eventBus.unregister(this);
    }
}


