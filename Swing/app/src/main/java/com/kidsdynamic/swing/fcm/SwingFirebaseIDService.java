package com.kidsdynamic.swing.fcm;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;

import retrofit2.Call;
import retrofit2.Response;

public class SwingFirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "SwingFirebaseIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);
        userApiNeedToken.updateAndroidRegistrationId(token).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }
}