package com.lendasoft.clubercompanion;


import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;


import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


public class Mesas extends ListActivity {
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> mAdapter;
    JSONObject jsonObject = new JSONObject();
    String url = "http://cluberapidev.azurewebsites.net/api/order/queryuserid/?userid=1038";//2053 TODO:Debemos agregarle el id del mesero
    String [] objetos = new String[3];
    OBJ_ORDEN [] ordenes = new OBJ_ORDEN[250];//Asumimos que tendra 250 ordenes pendientes

    private static Button   btn_test;
    private ListView list;
    private ArrayList<String> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);
        arrayList = new ArrayList<String>();
        new AsyncTaskExample().execute(url);

        // Set up ListView example
        String[] items = new String[20];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Item " + (i + 1);
        }


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


        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,arrayList);
        setListAdapter(mAdapter);

        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mAdapter.remove(mAdapter.getItem(position));
                                    ordenes = ArrayUtils.remove(ordenes,position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());


        //Agregamos elementos a la lista

        //Para cuando halla cambios en los datos
        //adapter.notifyDataSetChanged();



    }




    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Toast.makeText(this,
                "Clicked " + getListAdapter().getItem(position).toString(),
                Toast.LENGTH_SHORT).show();
        OBJ_ORDEN orden = ordenes[position];
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
                JSONArray jsonarray = JsonParser.readJsonArrayFromURL(url[0]);
                for(int i=0; i<jsonarray.length(); i++){
                    JSONObject obj = jsonarray.getJSONObject(i);

                    String OrderId = obj.getString("OrderId");
                    String totald = obj.getString("Total");
                    String tablenumber = obj.getString("TableNumber");

                    arrayList.add("Orden:" + OrderId);

                    //Creamos el objeto orden
                    OBJ_ORDEN orden = new OBJ_ORDEN();
                    orden.setOrderId(OrderId);
                    orden.setTableNumber(tablenumber);
                    orden.setTotalPayment(totald);
                    orden.setItems(new String[0]);
                    ordenes[i] = orden;
                }



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
            mAdapter.notifyDataSetChanged();
        }
    }

}


