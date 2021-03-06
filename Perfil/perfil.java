package com.lendasoft.clubercompanion.Perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lendasoft.clubercompanion.HelperClasses.CircleTransform;
import com.lendasoft.clubercompanion.HelperClasses.JsonParser;
import com.lendasoft.clubercompanion.HelperClasses.UserPicture;
import com.lendasoft.clubercompanion.Login;
import com.lendasoft.clubercompanion.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class perfil extends AppCompatActivity {

    private static final int SELECT_SINGLE_PICTURE = 101;
    public static final String IMAGE_TYPE = "image/*";
    ImageView selectedImagePreview;
    TextView txt_nombre;
    TextView txt_mesas;
    TextView txt_tips;
    private static Button btn_amesas;
    Button btn_logout;

    JSONObject jsonObject;
    JSONObject jsonwaiter;

    String waiterid;
    String username;
    String totaltips;
    String assignedtables;
    String urlimage;
    JSONArray JSONarraymesas;
    ArrayList<String> tablename;
    Uri myuri;


    //Imagen
    String base64;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String waiterId = getIntent().getStringExtra("Waiterid");
        String url = "http://api.url/" + waiterId;
        new AsyncTaskExample().execute(url);

        //Obtenemos el valor de waiterId
        waiterid = getIntent().getExtras().getString("Waiterid");

        tablename = new ArrayList<String>();
        setContentView(R.layout.activity_perfil);
        TomesasButtonClicked();

        //Poner el nombre del mesero
        txt_nombre = new TextView(this);
        txt_nombre =(TextView)findViewById(R.id.txt_perfilnombre);
        //txt_nombre.setText(username);

//Poner las mesas asignadas al mesero
        txt_mesas = new TextView(this);
        txt_mesas =(TextView)findViewById(R.id.txt_perfilmesas);
        txt_mesas.setText("Mesas:1,2,3,4,5,6 y Barra");

        //Poner las propinas
        txt_tips = new TextView(this);
        txt_tips = (TextView) findViewById(R.id.txt_perfilpropinas);
        //txt_tips.setText(totaltips);

        //Que pasa cuando le damos click al boton de imagen
        findViewById(R.id.btn_pick_single_image).setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
            }
        });


        btn_logout = (Button)findViewById(R.id.btnLogout);
        btn_logout.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String DeviceId = sharedpreferences.getString("prefs_deviceId","");
                String url = "http://api.url/" + DeviceId;
                new AsyncTaskPostDelete().execute(url);
            }
        });

    }


    public void TomesasButtonClicked(){
        btn_amesas = (Button)findViewById(R.id.btnpmesas);
        btn_amesas.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent("com.lendasoft.clubercompanion.Ordenes.Mesas");
                            intent.putExtra("Waiterid",waiterid);
                            startActivity(intent);
                    }
                }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    selectedImagePreview.setImageBitmap(getRoundedShape(new UserPicture(selectedImageUri, getContentResolver()).getBitmap()));
                    //Convertimos a Array de Bytes
                    byte[] bytes = toByteArray(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());
                    //Convertimos a Base64
                    base64 = toBase64(bytes);
                    //Posteamos
                    String url = "http://api.url/";
                    new AsyncTaskPostImage().execute(url);
                } catch (IOException e) {
                    //Log.e(perfil.class.getSimpleName(), "Failed to load image", e);
                }
                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
           // Log.d(perfil.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

    public byte[] toByteArray(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return b;
    }

    public String toBase64(byte[] image){
        String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        Canvas canvas;
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    public String buildTablestring(String[] tables){
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < tables.length; i++) {
            strBuilder.append(tables[i] + " | ");
        }
        String newString = strBuilder.toString();
        return newString;
    }

    public class AsyncTaskExample extends AsyncTask<String, String, String[]> {
        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        protected String[] doInBackground(String... url) {
            try {
                jsonObject = JsonParser.readJsonFromUrl(url[0]);
                System.out.println("Perfil:" + jsonObject);
                username = jsonObject.getString("UserName");
                totaltips = jsonObject.getString("ShiftTips");
                urlimage = jsonObject.getString("ImageUrl");
                myuri = Uri.parse(urlimage);
                JSONarraymesas = jsonObject.getJSONArray("CurrentTables");
                for (int i = 0;i<JSONarraymesas.length();i++){
                JSONObject tableinfo = JSONarraymesas.getJSONObject(i);
                    String tablenumber = tableinfo.getString("PlaceTableNumber");
                    tablename.add(tablenumber);
                }
                assignedtables = buildTablestring(tablename.toArray(new String[tablename.size()]));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {

            txt_tips.setText(totaltips);
            txt_nombre.setText(username);
            txt_mesas.setText(assignedtables);

            selectedImagePreview = (ImageView)findViewById(R.id.image_preview);

            Picasso.with(getBaseContext())
                    .load(myuri)
                    .placeholder(R.drawable.waiter_placeholder)
                    .transform(new CircleTransform())
                    .into(selectedImagePreview);
        }
    }

    public class AsyncTaskPostImage extends AsyncTask<String, String, String[]> {
        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        protected String[] doInBackground(String... url) {
            try {
               jsonwaiter = JsonParser.PostImage(url[0], waiterid, base64);
                urlimage = jsonwaiter.getString("ImageUrl");
                myuri = Uri.parse(urlimage);
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit= preferences.edit();
                edit.putString("Imageurl",urlimage);
                edit.commit();
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

    //
    //LOGOUT,Borramos el device del registro
    //
    public class AsyncTaskPostDelete extends AsyncTask<String,String,String[]> {
        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        protected String[] doInBackground(String... url) {

            try {
                JSONObject jsonObject = JsonParser.PostdeleteDevice(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            sharedpreferences.edit().clear().commit();
            sharedpreferences.edit().putString("ParseInit","YES");
            sharedpreferences.edit().commit();
            Intent intent = new Intent(getApplicationContext(),Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
