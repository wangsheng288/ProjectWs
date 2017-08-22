package com.example.tom.projectws;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Tom on 2017/8/2.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG="MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        //get update token
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"New Token:"+refreshedToken);
        //we can save token to third party to do anything we want
    }
}

