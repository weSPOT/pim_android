package net.wespot.pim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import net.wespot.pim.controller.Adapters.InitialPagerAdapter;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.utils.layout.CirclePageIndicator;
import net.wespot.pim.utils.layout.PageIndicator;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;

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
public class SplashActivity extends FragmentActivity {

    private Button login_wespot;
    private Button login_google;
    private Button login_facebook;

    InitialPagerAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INQ.init(this);

        setContentView(R.layout.activity_splash);

        mAdapter = new InitialPagerAdapter(getSupportFragmentManager());

        login_wespot = (Button) findViewById(R.id.login_wespot);

        login_wespot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra(Constants.URL_LOGIN_NAME, INQ.config.getProperty("wespot_login_url"));
                intent.putExtra(Constants.TYPE_LOGIN, Constants.WESPOT);
                startActivity(intent);
            }
        });

        login_google = (Button) findViewById(R.id.loginGoogle);

        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                String url = getGoogleLoginRedirectURL(INQ.config.getProperty("google_login_url"), INQ.config.getProperty("google_login_client_apikey"));
                intent2.putExtra(Constants.URL_LOGIN_NAME, url);
                intent2.putExtra(Constants.TYPE_LOGIN, Constants.GOOGLE);
                startActivity(intent2);
            }
        });

        login_facebook = (Button) findViewById(R.id.loginFacebook);

        login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                String url = getFbRedirectURL(INQ.config.getProperty("facebook_login_url"), INQ.config.getProperty("facebook_login_client_apikey"));
                intent2.putExtra(Constants.URL_LOGIN_NAME, url);
                intent2.putExtra(Constants.TYPE_LOGIN, Constants.FACEBOOK);
                startActivity(intent2);
            }
        });

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator = indicator;
        indicator.setViewPager(mPager);
        indicator.setSnap(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (INQ.accounts.isAuthenticated()){
//            login_google.setVisibility(View.INVISIBLE);
//            login_facebook.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            ARL.accounts.syncMyAccountDetails();

        }
    }

    public String getFbRedirectURL(String redirect_uri, String client_id)  {
        return "https://graph.facebook.com/oauth/authorize?client_id=" + client_id + "&display=page&redirect_uri=" + redirect_uri + "&scope=publish_stream,email";

    }
    public String getGoogleLoginRedirectURL(String redirect_uri, String client_id_google) {
        return "https://accounts.google.com/o/oauth2/auth?redirect_uri=" + redirect_uri + "&response_type=code&client_id=" + client_id_google + "&approval_prompt=force&scope=https://www.googleapis.com/auth/glass.timeline https://www.googleapis.com/auth/glass.location https://www.googleapis.com/auth/userinfo.profile  https://www.googleapis.com/auth/userinfo.email";
    }
    public String getLinkedInLoginRedirectURL(String redirect_uri, String client_id) {
        return "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&" +
                "client_id="+client_id+
                "&scope=r_fullprofile%20r_emailaddress%20r_network" +
                "&state=BdhOU9fFb6JcK5BmoDeOZbaY58" +
                "&redirect_uri="+redirect_uri;
    }
}
