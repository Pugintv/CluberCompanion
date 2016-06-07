package com.lendasoft.clubercompanion;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RegistrationIntentService extends IntentService {
    String url;
    String DeviceID;
    String token;
    SharedPreferences sharedPreferences; //= PreferenceManager.getDefaultSharedPreferences(this);;
    private static final String TAG = "RegIntentService";

   private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    public String getToken() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(NotificationSettings.SenderId,GoogleCloudMessaging.INSTANCE_ID_SCOPE);
        DeviceID = token;
        return token;
    }

    public String getDeviceID(){return DeviceID;}

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String resultString = null;
        String regID = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(NotificationSettings.SenderId,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("prefs_regId",token);
            editor.commit();

            Log.i(TAG, "Got GCM Registration Token: " + token);
            DeviceID = token;


            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if ((regID=sharedPreferences.getString("prefs_regId", null)) == null) {
                /*NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this);
                Log.i(TAG, "Attempting to register with NH using token : " + token);

                regID = hub.register(token).getRegistrationId();*/
                DeviceID = token;
                editor.putString("prefs_regId",token);
                editor.commit();


               /* resultString = "Registered Successfully - RegId : " + regID;
                Log.i(TAG, resultString);
                sharedPreferences.edit().putString("registrationID", regID ).apply();*/

            } else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
                url = "http://apisbx.cluberapp.com/api/Notification/UpdateDeviceRegistration/"  +  regID;
            }
        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (Login.isVisible) {
            System.out.println("Completed");
        }

    }

    public void RegisterDevicePost(){
        System.out.println("Register");
        //new AsyncTaskPostRegister().execute(url);
        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DeviceId",DeviceID);
        editor.commit();*/

    }


    public class AsyncTaskPostRegister extends AsyncTask<String,String,String[]> {
        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        protected String[] doInBackground(String... url) {
            try {

                JSONObject jsonObject = JsonParser.PostRequest(url[0]);
                DeviceID = jsonObject.getString("");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {

        }
    }
}