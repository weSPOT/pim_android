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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import net.wespot.pim.R;
import net.wespot.pim.controller.Adapters.FriendsLazyListAdapter;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.utils.layout.BaseFragmentActivity;
import net.wespot.pim.utils.layout.ButtonManager;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.dao.gen.FriendsLocalObject;

/**
 * A fragment that launches other parts of the demo application.
 */
public class PimFriendsFragment extends BaseFragmentActivity implements ListItemClickInterface<FriendsLocalObject> {

    private static final String TAG = "PimFriendsFragment";
    private FriendsLazyListAdapter adapterFriends;
    private Boolean sentRequesFriendLoad =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_friends);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.friends_add_friend);
        ListView friendsListView = (ListView) findViewById(R.id.list_friends);

        adapterFriends =  new FriendsLazyListAdapter(this);
        friendsListView.setAdapter(adapterFriends);
        adapterFriends.setOnListItemClickCallback(this);


        // Instantiation of the buttonManager
        ButtonManager buttonManager = new ButtonManager(this);

        // Creation of layout params
        LinearLayout.LayoutParams secondLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_zero, 0);

        // New inquiry button
        buttonManager.generateButton(linearLayout, secondLayoutParams, Constants.ID_ADD_FRIEND,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_ADD_FRIEND),
                Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_ADD_FRIEND), "").setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        INQ.friendsDelegator.siteUsersRequests();

                        // Only is possible to load the users if we already have the request sent
//                        if (sentRequesFriendLoad){
                            Intent intent = new Intent(getApplicationContext(), InqUsersiteFragment.class);
                            startActivity(intent);
//                        }
                    }
                });

        setTitle(R.string.common_title);

    }
    @Override
    public void onDestroy() {
        adapterFriends.close();
        super.onDestroy();
//        ARL.eventBus.unregister(this);
    }

    @Override
    public void onListItemClick(View v, int position, FriendsLocalObject object) {

    }

    @Override
    public boolean setOnLongClickListener(View v, int position, FriendsLocalObject object) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inquiry, menu);

        menu.setGroupVisible(R.id.actions_general, false);
        menu.setGroupVisible(R.id.actions_wonder_moment, false);
        menu.setGroupVisible(R.id.actions_data_collection, false);
        menu.setGroupVisible(R.id.actions_friends, true);
        menu.setGroupVisible(R.id.actions_usersite, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh_list_friends:
                Toast.makeText(this, getResources().getString(R.string.menu_refreshing_list_friends), Toast.LENGTH_SHORT).show();
                INQ.friendsDelegator.syncFriends();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}