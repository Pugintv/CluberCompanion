package com.lendasoft.clubercompanion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.parse.Parse;
import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Login extends ActionBarActivity {

///PUSH NOTIFICATIONS

    public static Login mainActivity;
    public static Boolean isVisible = false;
    private GoogleCloudMessaging gcm;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

///


    String isParseInit;
    String isLogged;
    String WaiterId;
    String Login;
    String log_username;
    String log_password;
    String log_name;
    JSONObject jsontest;
    private static EditText username;
    private static EditText password;
    private static Button   login_btn;
    HTTP_Request req = new HTTP_Request();

    JSONObject jsonObject = new JSONObject();
    JSONObject jsonwaiter = new JSONObject();
    JSONObject waiter = new JSONObject();
    String [] login_object = new String[3];
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();

        LoginButtonClicked();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isParseInit = sharedPreferences.getString("ParseInit","NO");
        if(isParseInit.equalsIgnoreCase("NO")){
            parseInit();
        }

        checkIsLogged();
    }

    public void registerWithNotificationHubs()
    {
        //Log.i(TAG, " Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void parseInit(){
        Parse.initialize(this, "wptHWdzQOWT8LYaOmTVCZOD3PhU7WjlpQW2keSyi", "4jsVgExUQIiQoVOs1tWIcT32VO6uMGVHHGoR0QOr");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public void checkIsLogged(){
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        isLogged = sharedPreferences.getString("Login","NO");
        if(isLogged.equalsIgnoreCase("YES")){
            Intent intent = new Intent("com.lendasoft.clubercompanion.Mesas");
            intent.putExtra("Waiterid",sharedPreferences.getString("WaiterId",null));
            startActivity(intent);
        }

    }

    public void  LoginButtonClicked(){
        username = (EditText)findViewById(R.id.txtuser_editText);
        password = (EditText)findViewById(R.id.txtpass_editText);
        login_btn = (Button)findViewById(R.id.button_login);


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //url = url + username.getText();
                    //new AsyncTaskExample().execute(url);

                }
            }
        });



        login_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://apisbx.cluberapp.com/api/Companion/SignIn?userName=" + username.getText() + "&password=" + password.getText().toString();
                        new AsyncTaskExample().execute(url);

                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Clase para Webservices

    public class AsyncTaskExample extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        @Override
        protected String[] doInBackground(String... url) {
            try {
                jsonObject = JsonParser.readJsonFromUrl(url[0]);
                login_object[0] = jsonObject.getString("SuccessfulSignIn");
                jsonwaiter = jsonObject.getJSONObject("Waitperson");
                login_object[1] = jsonwaiter.getString("WaitpersonId");
                login_object[2] = jsonwaiter.getString("UserName");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return login_object;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            log_username = login_object[0];
            log_password = login_object[1];
            log_name = login_object[2];

            if(login_object[0] == "true") {


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Login","YES");
                editor.putString("WaiterId",login_object[1]);
                editor.commit();

                Toast.makeText(Login.this,"Welcome " + login_object[2],Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.lendasoft.clubercompanion.Mesas");
                intent.putExtra("Waiterid",login_object[1]);
                startActivity(intent);
            } else{
                Toast.makeText(Login.this, "User and Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                //Log.i(TAG, "This device is not supported by Google Play Services.");
                //ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

}
