package com.lendasoft.clubercompanion;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class Mesas extends ActionBarActivity {

    JSONObject jsonObject = new JSONObject();
    String url = "http://cluberapidev.azurewebsites.net/api/order/queryuserid/?userid=2053";//TODO:Debemos agregarle el id del mesero
    String [] objetos = new String[3];

    private static Button   btn_test;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);
             Test();


//Creamos las TABS
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Pendientes");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Pendientes");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Atendidas");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Atendidas");
        tabHost.addTab(tabSpec);

//Creamos la LISTA
        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
        list.setAdapter(adapter);//Le ponemos a nuestra lista el adaptador de datos
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {//Cuando seleccionemos una fila iremos a la pantalla de detalle
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //El parametro id nos dice que row del listview le hicimos click
                System.out.println(id);
                //Aqui creamos una orden correspondiente al row para pasar al siguiente view
                OBJ_ORDEN orden = new OBJ_ORDEN();
                orden.setOrderId(objetos[0]);
                orden.setTableNumber(objetos[1]);
                orden.setTotalPayment(objetos[2]);
                orden.setItems(new String[0]);
                //Serializamos el objeto orden para que pueda ser pasado
                Intent intent = new Intent(getApplicationContext(),Detalle.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("OrdenTag",(Serializable)orden);
                intent.putExtras(mBundle);
                if (intent != null) {
                    //Aqui pasamos el objeto mesa para mostrar su detalle
                    startActivity(intent);
                }
            }
        });

        new AsyncTaskExample().execute(url);

        //Agregamos elementos a la lista

        //Para cuando halla cambios en los datos
        //adapter.notifyDataSetChanged();



    }


    public void Test() {
        btn_test = (Button) findViewById(R.id.mesasTestbtn);
        btn_test.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Iniciamos la tarea asincrona
                        new AsyncTaskExample().execute(url);
                    }
            }
        );
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mesas, menu);
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

    public class AsyncTaskExample extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        @Override
        protected String[] doInBackground(String... url) {
            try {
                jsonObject = JsonParser.readJsonFromUrl(url[0]);
                //JSONArray jarr = jsonObject.getJSONArray("");
                objetos[0] = jsonObject.getString("OrderId");
                objetos[1] = jsonObject.getString("Total");
                objetos[2] = jsonObject.getString("TableNumber");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return objetos;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            arrayList.add("Orden:" + objetos[0]);
            arrayList.add("Orden de prueba #2");
            arrayList.add("Orden de prueba #3");
            adapter.notifyDataSetChanged();
        }
    }

}


