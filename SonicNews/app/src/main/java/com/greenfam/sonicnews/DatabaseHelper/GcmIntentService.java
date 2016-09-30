package com.greenfam.sonicnews.DatabaseHelper;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by jason on 9/29/16.
 */

public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService(String name) {
        super(name);
    }

    public GcmIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    // Todo: Learn why they created this class
}
