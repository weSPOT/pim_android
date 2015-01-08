package net.wespot.pim.utils;

import org.celstec.arlearn2.android.delegators.ARL;

import java.util.TimerTask;

/**
 * Created by titogelo on 26/06/14.
 */
public class RetrieveMessageTask extends TimerTask{


    private static final String TAG = "Timer";

    public RetrieveMessageTask() {
        ARL.eventBus.register(this);
    }

    @Override
    public void run() {
//        Log.e(TAG, "Start");
        doSomeWork();
//        Log.e(TAG, "Stop");

    }

    public void doSomeWork() {
        ARL.eventBus.post(new doSomeWorkClass());
    }


    public void onEventAsync(doSomeWorkClass sge) {

        ARL.eventBus.post(new TimeMessageEvent());
    }

    private class doSomeWorkClass {}

}
