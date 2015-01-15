package net.wespot.pim.utils;


import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import net.wespot.pim.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.gcm.NotificationListenerInterface;

import java.util.HashMap;

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
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class NotificationListenerTest implements NotificationListenerInterface {
    private static final java.lang.String TAG = "12312";

//    @Override
//    public boolean acceptNotificationType(String notificationType) {
//        return true;
//    }
//
//    @Override
//    public void handleNotification(HashMap<String, String> map) {
//        Log.e(TAG, "testtttt");
//        ARL.getContext();
//    }

//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public NotificationListenerTest(String name) {
//        super(name);
//    }
//
    @Override
    public boolean acceptNotificationType(String notificationType) {
         return false;
    }

    @Override
    public void handleNotification(HashMap map) {
        System.out.println("map = " + map);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ARL.getContext())
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        NotificationManager mNotificationManager =
                (NotificationManager) ARL.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(TAG), mBuilder.build());        mNotificationManager.notify(Integer.parseInt(TAG), mBuilder.build());
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.e(TAG, "test")
//    }
}
