package com.lendasoft.clubercompanion.Ordenes;


import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lendasoft.clubercompanion.HelperClasses.JsonParser;
import com.lendasoft.clubercompanion.HelperClasses.SwipeDismissListViewTouchListener;
import com.lendasoft.clubercompanion.R;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Mesas extends ListActivity {

    private ArrayAdapter pendingAdapter;
    private ArrayAdapter<String> nAdapter;
    private ArrayList<OBJ_ORDEN> penOrderlist;
    private MaterialRefreshLayout refresh;
    private MaterialRefreshLayout refresh2;
    private ArrayAdapter atendedAdapter;
    private ArrayAdapter<String> mAdapter;

    Long TimerInterval = 10000L;

    String waiterid;
    JSONObject jsonObject = new JSONObject();
    String urlpending;
    String urlatended;
    String[] objetos = new String[3];

    Button btn_perfil;

    ListView listView;
    ListView listView2;

    OBJ_ORDEN[] ordenes = new OBJ_ORDEN[250];//Asumimos que tendra 250 ordenes pendientes
    OBJ_ORDEN[] completedOrders = new OBJ_ORDEN[250];//Ordenes completadas
    ArrayList<OBJ_ITEM> obj_items;

    Integer RefreshType = 0;//0 para pendientes,1 para atendidas

    private Handler handler = new Handler();
    private static Button btn_test;
    private ListView list;
    private ListView list2;

    private ArrayList<String> arrayList;

    private ArrayList<String> arrayList2;
    private ArrayList<OBJ_ORDEN> atOrderList;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public Mesas() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Traemos datos de la otra pantalla y generamos URLs
        waiterid = getIntent().getExtras().getString("Waiterid");
        if (waiterid !=null) {
            saveValue();
        }
        else waiterid = valueSaved();
        urlpending =  "http://api.cluberservice.com/api/Companion/QueryPendingOrders?waitpersonId=" /*"http://apisbx.cluberapp.com/api/Companion/QueryPendingOrders?waitpersonId="*/ + waiterid;
        urlatended = "http://api.cluberservice.com/api/Companion/QueryCompletedOrders?waitpersonId=" /*"http://apisbx.cluberapp.com/api/Companion/QueryCompletedOrders?waitpersonId="*/ + waiterid;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);



        arrayList = new ArrayList<String>();

        penOrderlist = new ArrayList<OBJ_ORDEN>();
        pendingAdapter = new OrdenArrayAdapter(this, penOrderlist);

        atOrderList = new ArrayList<OBJ_ORDEN>();
        atendedAdapter = new OrdenArrayAdapter(this, atOrderList);
        arrayList2 = new ArrayList<String>();

        //Nos traemos la informacion del WS
        new AsyncTaskExample().execute(urlpending);

        //Inicializamos el boton de perfil
        perfilClicked();

        //
        //Creamos las TABS
        //
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Pendientes");
        tabSpec.setContent(R.id.layout1);//(R.id.tab1);
        tabSpec.setIndicator("Pendientes");
        tabHost.addTab(tabSpec);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("Atendidas");
        tabSpec2.setContent(R.id.layout2);//(R.id.tab2);
        tabSpec2.setIndicator("Atendidas");
        tabHost.addTab(tabSpec2);

        //
        //Que pasa cuando cambiamos de TAB
        //
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if ("Pendientes".equals(tabId)) {
                    System.out.println("Pendientes");
                    pendingAdapter.clear();
                    new AsyncTaskExample().execute(urlpending);
                    RefreshType = 0;
                }
                if ("Atendidas".equals(tabId)) {
                    System.out.println("Atendidas");
                    atendedAdapter.clear();
                    new AsyncTaskAtendidas().execute(urlatended);
                    RefreshType = 1;
                }
            }
        });

    //Definicion de los adaptadores de datos
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, arrayList);
        setListAdapter(pendingAdapter);

        nAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text2,arrayList2);


        listView = getListView();


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
                                   String Orderid = ordenes[position].OrderId;
                                    String status = "0";
                                    String url = "http://api.cluberservice.com/api/Companion/ChangeOrderStatus?orderId=" + Orderid + "&status=" + status;
                                   new AsyncTaskPost().execute(url);

                                    //Eliminamos de los arreglos y lo pasamos al otro tab
                                    atendedAdapter.add(pendingAdapter.getItem(position));
                                    pendingAdapter.remove(pendingAdapter.getItem(position));
                                    ordenes = ArrayUtils.remove(ordenes, position);
                                }
                                pendingAdapter.notifyDataSetChanged();
                                atendedAdapter.notifyDataSetChanged();
                            }
                        });




        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());

        //Hacemos lo mismo para el otro listview
        listView2 = getListView();//(ListView) findViewById(R.id.list2);

        SwipeDismissListViewTouchListener touchListener2 =
                new SwipeDismissListViewTouchListener(
                        listView2,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String Orderid = /*completedOrders*/ordenes[position].OrderId;
                                    String status = "8";
                                    String url = "http://api.cluberservice.com/api/Companion/ChangeOrderStatus?orderId=" + Orderid + "&status=" + status;
                                    new AsyncTaskPost().execute(url);

                                    //Eliminamos de los arreglos y lo pasamos al otro tab
                                    //atendedAdapter.add(pendingAdapter.getItem(position));
                                    //pendingAdapter.remove(pendingAdapter.getItem(position));
                                    //ordenes = ArrayUtils.remove(ordenes, position);
                                }
                                //pendingAdapter.notifyDataSetChanged();
                                //atendedAdapter.notifyDataSetChanged();
                            }
                        });

        listView2.setOnTouchListener(touchListener2);
        listView2.setOnScrollListener(touchListener2.makeScrollListener());

     //
     //PULL TO REFRESH
     //
     // Manejamos el pull to refresh de pendientes

    refresh = (MaterialRefreshLayout) findViewById(R.id.refresh);
    refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //Codigo que se hara en el refresh
            pendingAdapter.clear();
            new AsyncTaskExample().execute(urlpending);
            refresh.finishRefresh();
        }
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            //load more refreshing...
        }
    });

    //Manejamos el pull to refresh de atendidas

       refresh2 = (MaterialRefreshLayout) findViewById(R.id.refresh2);
        refresh2.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //Codigo que se hara en el refresh
                setListView2();
                atendedAdapter.clear();
                new AsyncTaskAtendidas().execute(urlatended);
                //Activity_Atendidas atendidas = new Activity_Atendidas();
                //atendidas.refreshaction(waiterid);
                refresh2.finishRefresh();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //load more refreshing...
            }
        });

    //
    //Termina pull to refresh
    //


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    //Refresh se ejecutara despues de 5 segundos
        handler.postDelayed(runnable, 5000);
    }

    public void perfilClicked(){
        btn_perfil = (Button) findViewById(R.id.btn_perfil);
        btn_perfil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.lendasoft.clubercompanion.Perfil.perfil");
                        intent.putExtra("Waiterid",waiterid);
                        startActivity(intent);
                    }
                }
        );
    }


    ///LISTVIEW2
    //
    //
    public void setListView2(){

        View atended = findViewById(R.id.layout2);
        listView2 = (ListView) atended.findViewById(R.id.list2);
        //listView2 = getListView();//(ListView) findViewById(R.id.list2);

        nAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text2,arrayList2);
        //setListAdapter(nAdapter);
    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("TouchListView 2");
            OBJ_ORDEN orden = completedOrders[position];

            //Serializamos el objeto orden para que pueda ser pasado al detalle
            Intent intent = new Intent(getApplicationContext(), Detalle.class);
            intent.putExtra("Waiterid",waiterid);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("OrdenTag", (Serializable) orden);
            intent.putExtras(mBundle);
            if (intent != null) {
                //Aqui pasamos el objeto mesa para mostrar su detalle
                startActivity(intent);
            }
        }
    });

        SwipeDismissListViewTouchListener touchListener2 =
                new SwipeDismissListViewTouchListener(
                        listView2,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String Orderid = completedOrders[position].OrderId;
                                    String status = "8";
                                    String url = "http://api.cluberservice.com/api/Companion/ChangeOrderStatus?orderId=" + Orderid + "&status=" + status;
                                    new AsyncTaskPost().execute(url);
                                }

                            }
                        });

        listView2.setOnTouchListener(touchListener2);
        listView2.setOnScrollListener(touchListener2.makeScrollListener());
    }

    ///
    //
    // LISTVIEW2

    public String valueSaved(){
        SharedPreferences mySharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String value = mySharedPrefs.getString("Waiterid", null);
        return value;
    }

    public void saveValue(){
        SharedPreferences mySharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplication());
        mySharedPrefs.edit().putString("Waiterid", waiterid).commit();
    }

    //Cuando seleccionamos una orden esta nos envia al detalle,pasamos la info para que cargue el detalle
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        OBJ_ORDEN orden = ordenes[position];

        //Serializamos el objeto orden para que pueda ser pasado al detalle
        Intent intent = new Intent(getApplicationContext(), Detalle.class);
        intent.putExtra("Waiterid",waiterid);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("OrdenTag", (Serializable) orden);
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
    //
    //Parseamos la fecha para desplegarla en el campo
    //
    public String parsedate(String date) {//Asumimos a date como: YYYY-MM-DDTHH:mm:ss:zzz
        String hour;
        hour = date.substring(date.indexOf('T') + 1, date.indexOf(':') + 3);
        return hour;
    }

    //
    //Le damos formato al string para que mantenga su forma
    //
    public String formatString(String tablenumber, String Orderid, String timestamp) {
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       /* client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Mesas Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.lendasoft.clubercompanion/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
/*        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Mesas Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.lendasoft.clubercompanion/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    public void changeorderstate(String url) throws IOException, JSONException {
        JsonParser.PostRequest(url);
    }

    //
    //TRAEMOS LAS PENDIENTES
    //

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
                System.out.println("jsonarray:" + jsonarray);
                System.out.println("Array elements:" + jsonarray.length());

                for (int i = 0; i < jsonarray.length(); i++) {

                    obj_items = new ArrayList<OBJ_ITEM>();
                    JSONObject obj = jsonarray.getJSONObject(i);
                    System.out.println("OBJ:(" + i + ")" + obj);
                    String OrderId = obj.getString("OrderId");
                    String dOrdertotal = obj.getString("Total");
                    String sOrdertablenumber = obj.getString("PlaceTableNumber");
                    String Ordertimestamp = formatdaystamp(obj.getString("TimeStamp"));
                    String sOrdertip = obj.getString("Tip");
                    String dOrderstatus = obj.getString("Status");
                    String sUserfullname = obj.getString("UserFullName");
                    Integer dOrderstate = obj.getInt("State");


                    JSONArray itemjson = jsonarray.getJSONObject(i).getJSONArray("Items");//Obtenemos la lista de items de esa orden
                    //Obtenemos los items dentro de la orden
                    for (int x = 0; x < itemjson.length(); x++) {
                        JSONObject item = itemjson.getJSONObject(x);
                        String sOrderitemid = item.getString("OrderItemId");
                        String sItemprice = item.getString("Price");
                        String sPlaceitemid = item.getString("PlaceItemId");
                        String sPlaceitemname = item.getString("PlaceItemName");
                        obj_items.add(new OBJ_ITEM(OrderId,sOrderitemid,sItemprice,sPlaceitemid,sPlaceitemname));
                    }


                    //Creamos el objeto orden

                    penOrderlist.add(new OBJ_ORDEN(OrderId, sOrdertablenumber, dOrdertotal, obj_items, this.getState(dOrderstate), sUserfullname, sOrdertip,Ordertimestamp));
                    OBJ_ORDEN orden = new OBJ_ORDEN();
                    orden.setOrderId(OrderId);
                    orden.setTableNumber(sOrdertablenumber);
                    orden.setTotalPayment(dOrdertotal);
                    orden.setItems(obj_items);
                    orden.setTip(sOrdertip);
                    orden.setUserfullname(sUserfullname);
                    orden.setTimeStamp(Ordertimestamp);
                    ordenes[i] = orden;
                }

            } catch (IOException | JSONException e) {
               // e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return objetos;
        }


        public Integer getState(Integer state){
            if (state == 1){
                return R.drawable.greencircle;
            }
            else if (state == 2){
                return R.drawable.redcircle;
            }
            else return R.drawable.graycircle;
        }

        public String formatdaystamp(String timestamp) throws ParseException {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date date = dateFormat.parse(timestamp);
            SimpleDateFormat dateFormatFinal = new SimpleDateFormat("HH:mm-dd/MM/yyyy");
            return dateFormatFinal.format(date);
        }

        public void Queryorders(){

        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            mAdapter.notifyDataSetChanged();
            pendingAdapter.notifyDataSetChanged();
        }

    }

    //
    //HACEMOS POST A LA ORDEN DE ATENDIDA
    //

    public class AsyncTaskPost extends AsyncTask<String, String, String[]> {
        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        protected String[] doInBackground(String... url) {
            try {

                JsonParser.PostRequest(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
               // e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {

        }
    }

    //
    //METODO GET PARA ATENDIDAS
    //

    public class AsyncTaskAtendidas extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            //Mostrar progressbar de ser necesario
        }

        @Override
        protected String[] doInBackground(String... url) {
            try {

                jsonObject = JsonParser.readJsonFromUrl(url[0]);
                JSONArray jsonarray = JsonParser.readJsonArrayFromURL(url[0]);
                System.out.println("jsonarray:" + jsonarray);
                System.out.println("Array elements:" + jsonarray.length());

                for (int i = 0; i < jsonarray.length(); i++) {

                    obj_items = new ArrayList<OBJ_ITEM>();
                    JSONObject obj = jsonarray.getJSONObject(i);
                    System.out.println("OBJ:(" + i + ")" + obj);
                    String OrderId = obj.getString("OrderId");
                    String dOrdertotal = obj.getString("Total");
                    String sOrdertablenumber = obj.getString("TableNumber");
                    String Ordertimestamp = formatdaystamp(obj.getString("TimeStamp"));
                    String sOrdertip = obj.getString("Tip");
                    String dOrderstatus = obj.getString("Status");
                    String sUserfullname = obj.getString("UserFullName");
                    Integer dOrderstate = obj.getInt("State");


                    JSONArray itemjson = jsonarray.getJSONObject(i).getJSONArray("Items");//Obtenemos la lista de items de esa orden
                    //Obtenemos los items dentro de la orden
                    for (int x = 0; x < itemjson.length(); x++) {
                        JSONObject item = itemjson.getJSONObject(x);
                        String sOrderitemid = item.getString("OrderItemId");
                        String sItemprice = item.getString("Price");
                        String sPlaceitemid = item.getString("PlaceItemId");
                        String sPlaceitemname = item.getString("PlaceItemName");
                        obj_items.add(new OBJ_ITEM(OrderId,sOrderitemid,sItemprice,sPlaceitemid,sPlaceitemname));
                    }


                    //Creamos el objeto orden
                    /*penOrderlist.add(new OBJ_ORDEN(OrderId, sOrdertablenumber, dOrdertotal, obj_items, this.getState(dOrderstate), sUserfullname, sOrdertip));*/
                    atOrderList.add(new OBJ_ORDEN(OrderId, sOrdertablenumber, dOrdertotal, obj_items, this.getState(dOrderstate), sUserfullname, sOrdertip,Ordertimestamp));
                    OBJ_ORDEN orden = new OBJ_ORDEN();
                    orden.setOrderId(OrderId);
                    orden.setTableNumber(sOrdertablenumber);
                    orden.setTotalPayment(dOrdertotal);
                    orden.setItems(obj_items);
                    orden.setTip(sOrdertip);
                    orden.setUserfullname(sUserfullname);
                    orden.setTimeStamp(formatdaystamp(Ordertimestamp));

                    //ordenes[i] = orden;
                    completedOrders[i] = orden;
                }

            } catch (IOException | JSONException e) {
                //e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return objetos;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            //mAdapter.notifyDataSetChanged();
            //pendingAdapter.notifyDataSetChanged();
            nAdapter.notifyDataSetChanged();
            System.out.println("Completed elements:" + atendedAdapter.getCount());
            atendedAdapter.notifyDataSetChanged();

            View atended = findViewById(R.id.layout2);
            listView2 = (ListView) atended.findViewById(R.id.list2);
            listView2.setAdapter(atendedAdapter);
            setListView2();
        }

        //
        //Elegir el estado de la orden
        //
        public Integer getState(Integer state){
            if (state == 1){
                return R.drawable.greencircle;
            }
            else if (state == 2){
                return R.drawable.redcircle;
            }
            else return R.drawable.graycircle;
        }

        public String formatdaystamp(String timestamp) throws ParseException {
            /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            System.out.println("TIMESTAMP:" + timestamp);
            Date date = dateFormat.parse(timestamp);
            SimpleDateFormat dateFormatFinal = new SimpleDateFormat("HH:mm-dd/MM/yyyy");
            return dateFormatFinal.format(date);*/
            return "";
        }
    }

//Refresh cada N tiempo
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(RefreshType == 0) {
      /* Metodo a Ejecutar */
                pendingAdapter.clear();
                new AsyncTaskExample().execute(urlpending);
      /* Volvemos a llamar cada N */
                handler.postDelayed(this, TimerInterval);
            }
            else{
                atendedAdapter.clear();
                new AsyncTaskAtendidas().execute(urlatended);
                handler.postDelayed(this,TimerInterval);
            }
        }
    };

}



