package com.lendasoft.clubercompanion;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Login extends ActionBarActivity {
    String log_username;
    String log_password;
    String log_name;

    private static EditText username;
    private static EditText password;
    private static Button   login_btn;

    JSONObject jsonObject = new JSONObject();
    String url = "http://cluberapidev.azurewebsites.net/api/user/querymobilephonenumber/?mobilephonenumber=";
    String [] login_object = new String[3];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButtonClicked();
    }

    public void  LoginButtonClicked(){
        username = (EditText)findViewById(R.id.txtuser_editText);
        password = (EditText)findViewById(R.id.txtpass_editText);
        login_btn = (Button)findViewById(R.id.button_login);


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    url = url + username.getText();
                    new AsyncTaskExample().execute(url);
                }
            }
        });



        login_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(/*username.getText().toString().equals("testuser") &&*/
                             password.getText().toString().equals(login_object[1])){
                            Toast.makeText(Login.this,"Welcome " + log_name,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent("com.lendasoft.clubercompanion.Mesas");
                            startActivity(intent);
                        } else{
                            Toast.makeText(Login.this,"User and Password incorrect",Toast.LENGTH_SHORT).show();
                        }
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
                login_object[0] = jsonObject.getString("MobilePhoneNumber");
                login_object[1] = jsonObject.getString("PIN");
                login_object[2] = jsonObject.getString("Name");
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
        }
    }

}
