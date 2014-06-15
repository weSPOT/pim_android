package net.wespot.pim;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import net.wespot.pim.utils.Constants;
import net.wespot.pim.utils.layout.ButtonManager;
import net.wespot.pim.utils.layout.MainActionBarFragmentActivity;
import net.wespot.pim.utils.layout.ViewItemClickInterface;
import net.wespot.pim.view.InqMyMediaFragment;
import net.wespot.pim.view.PimBadgesFragment;
import net.wespot.pim.view.PimInquiriesFragment;
import net.wespot.pim.view.PimProfileFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MyAccount;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.events.InquiryEvent;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends MainActionBarFragmentActivity implements ListItemClickInterface<View> {
    private static final String TAG = "MainActivity";
//    private static final int MY_INQUIRIES = 12345;
//    private static final int MY_MEDIA = 12346;
//    private static final int PROFILE = 12347;
//    private static final int BADGES = 12348;
//    private static final int FRIENDS = 12349;
    private static int number_inquiries;

    private ViewItemClickInterface callback;



//    private ButtonDelegator man;


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "Recover in MainActivity > onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "Recover in MainActivity > onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "Recover in MainActivity > onSaveInstanceState");
    }

    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ARL.eventBus.register(this);

        number_inquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();

        setContentView(R.layout.main_main);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.content_main_screen);

        // Instantiation of the buttonManager
        ButtonManager buttonManager = new ButtonManager(this);
        buttonManager.setOnListItemClickCallback(this);

        // Creation of layout params
        LinearLayout.LayoutParams thirdLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_second);
        LinearLayout.LayoutParams secondLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_zero);
        LinearLayout.LayoutParams firstLayoutParams = buttonManager.generateLayoutParams(R.dimen.mainscreen_margintop_first);

        // My Inquiries button
        buttonManager.generateButton(linearLayout, firstLayoutParams, Constants.ID_MYINQUIRIES,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MYINQUIRIES), Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MYINQUIRIES), "");

        // My media button
        buttonManager.generateButton(linearLayout, thirdLayoutParams, Constants.ID_MYMEDIA,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MYMEDIA), Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MYMEDIA), "");

        // Profile button
        buttonManager.generateButton(linearLayout, secondLayoutParams, Constants.ID_PROFILE,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_PROFILE), Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_PROFILE), "");

        // Badges button
        buttonManager.generateButton(linearLayout, secondLayoutParams, Constants.ID_BADGES,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_BADGES), Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_BADGES), "");

        // Friends button
        buttonManager.generateButton(linearLayout, secondLayoutParams, Constants.ID_MAIN_FRIENDS,
                Constants.INQUIRY_MAIN_LIST.get(Constants.ID_MAIN_FRIENDS), Constants.INQUIRY_ICON_MAIN_LIST.get(Constants.ID_MAIN_FRIENDS), "");
    }

    @Override
    public void onListItemClick(View v, int id, View object) {
        switch (id){
            case Constants.ID_MYINQUIRIES:
                Intent intent_inquiries = new Intent(getApplicationContext(), PimInquiriesFragment.class);
                startActivity(intent_inquiries);
                break;
            case Constants.ID_MYMEDIA:
                Intent intent_media = new Intent(getApplicationContext(), InqMyMediaFragment.class);
                startActivity(intent_media);
                break;
            case Constants.ID_PROFILE:
                Intent intent_profile = new Intent(getApplicationContext(), PimProfileFragment.class);
                startActivity(intent_profile);
                break;
            case Constants.ID_BADGES:
                Intent intent_badges = new Intent(getApplicationContext(), PimBadgesFragment.class);
                startActivity(intent_badges);
                break;
            case Constants.ID_MAIN_FRIENDS:
                Toast.makeText(getApplicationContext(), "Not implemented yet.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // Avoid re-login
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "on resume Main activity. Number of inquiries: "+number_inquiries);

        number_inquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();
    }

    private void onEventBackgroundThread(InquiryEvent inquiryObject){
        number_inquiries = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll().size();
    }

    private void onEventBackgroundThread(MyAccount myAccount){
        INQ.inquiry.syncInquiries();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ARL.eventBus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_default, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:

                Log.d(TAG, "Before logout: " + INQ.accounts.isAuthenticated());
                INQ.accounts.disAuthenticate();
                INQ.properties.setAccount(0l);

                Intent myIntent = new Intent(this, SplashActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                startActivity(myIntent);
                finish();

                Log.d(TAG, "After logout: "+INQ.accounts.isAuthenticated());

                Toast.makeText(this,R.string.menu_logout,Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, View object) {
        return false;
    }
}
