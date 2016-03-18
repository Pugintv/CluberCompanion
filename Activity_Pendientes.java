package com.lendasoft.clubercompanion;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by victorrosas on 3/16/16.
 */
public class Activity_Pendientes extends ListActivity {
    private ArrayAdapter pendingAdapter;
    private ArrayAdapter<String> nAdapter;
    private ArrayList<OBJ_ORDEN> penOrderlist;
    private MaterialRefreshLayout refresh;
    private MaterialRefreshLayout refresh2;
    private ArrayAdapter atendedAdapter;
    private ArrayAdapter<String> mAdapter;

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

    private static Button btn_test;
    private ListView list;
    private ListView list2;

    private ArrayList<String> arrayList;

    private ArrayList<String> arrayList2;
    private ArrayList<OBJ_ORDEN> atOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Traemos datos de la otra pantalla y generamos URLs
        waiterid = getIntent().getExtras().getString("Waiterid");
        if (waiterid !=null) {
            saveValue();
        }
        else waiterid = valueSaved();
        urlpending =  "http://apisbx.cluberapp.com/api/Companion/QueryPendingOrders?waitpersonId=" + waiterid;
        urlatended = "http://apisbx.cluberapp.com/api/Companion/QueryCompletedOrders?waitpersonId=" + waiterid;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_pendientes);
        //setContentView(R.layout.activity_mesas);


        arrayList = new ArrayList<String>();
        penOrderlist = new ArrayList<OBJ_ORDEN>();
        pendingAdapter = new OrdenArrayAdapter(this, penOrderlist);

        atOrderList = new ArrayList<OBJ_ORDEN>();
        atendedAdapter = new OrdenArrayAdapter(this, atOrderList);
        arrayList2 = new ArrayList<String>();

        //Nos traemos la informacion del WS
        new AsyncTaskExample().execute(urlpending);



        //Definicion de los adaptadores de datos
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, arrayList);
        setListAdapter(pendingAdapter);


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
                                    String url = "http://apisbx.cluberapp.com/api/Companion/ChangeOrderStatus?orderId=" + Orderid + "&status=" + status;
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
        //listView2.setOnTouchListener(touchListener);
        //listView2.setOnScrollListener(touchListener.makeScrollListener());

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


        //
        //Termina pull to refresh
        //

    }

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
                    String Ordertimestamp = obj.getString("TimeStamp");
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

                    penOrderlist.add(new OBJ_ORDEN(OrderId, sOrdertablenumber, dOrdertotal, obj_items, this.getState(dOrderstate), sUserfullname, sOrdertip));
                    OBJ_ORDEN orden = new OBJ_ORDEN();
                    orden.setOrderId(OrderId);
                    orden.setTableNumber(sOrdertablenumber);
                    orden.setTotalPayment(dOrdertotal);
                    orden.setItems(obj_items);
                    orden.setTip(sOrdertip);
                    orden.setUserfullname(sUserfullname);
                    ordenes[i] = orden;
                }

            } catch (IOException | JSONException e) {
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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {

        }
    }



    //Cuando seleccionamos una orden esta nos envia al detalle,pasamos la info para que cargue el detalle
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        OBJ_ORDEN orden = ordenes[position];
        //getListAdapter().getItem(position);

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

}
