package com.lendasoft.clubercompanion;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends ActionBarActivity {
    private static EditText username;
    private static EditText password;
    private static Button   login_btn;

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

        login_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(username.getText().toString().equals("testuser") &&
                             password.getText().toString().equals("password")   ){
                            Toast.makeText(Login.this,"User and Password correct",Toast.LENGTH_SHORT).show();
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
}
