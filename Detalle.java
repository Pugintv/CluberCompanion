package com.lendasoft.clubercompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;


public class Detalle extends ActionBarActivity implements Serializable {

    String txtinfo;

    TextView lblinfo;
    TextView lbltotal;
    TextView lbltablenum;
    TextView lbldate;

    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<OBJ_ITEM> itemList;
    private static Button btn_volver;
    String waiterid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        this.ToordersButtonClicked();

        lbldate = (TextView)findViewById(R.id.lblfecha);
        lbltablenum = (TextView)findViewById(R.id.lbltablenumber);
        lbltotal = (TextView)findViewById(R.id.lbltotal);
        lblinfo = (TextView)findViewById(R.id.Lblinfo);

        waiterid = getIntent().getExtras().getString("Waiterid");

        OBJ_ORDEN orden = (OBJ_ORDEN) getIntent().getSerializableExtra("OrdenTag");
        System.out.println(orden.TableNumber);
        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);
        list.setAdapter(adapter);//Le ponemos a nuestra lista el adaptador de datos

        /*arrayList.add("Orden: " + orden.OrderId);
        arrayList.add("Total: " + orden.TotalPayment);
        arrayList.add("Mesa: " + orden.TableNumber);*/
        lbldate.setText(orden.TimeStamp);
        lbltablenum.setText("Mesa:  " + orden.TableNumber);
        lblinfo.setText("Orden: " + orden.OrderId);
        lbltotal.setText("Total: $" + orden.TotalPayment);

        itemList = formatOrder(orden.Items);
        for (Integer i =0;i<itemList.size();i++) {
        OBJ_ITEM item = itemList.get(i);
            arrayList.add(item.sPlaceitemname + "($ "+ item.sItemprice + " )" + "    X     " + item.dItemQuantity.toString() + "           = $" + (item.dItemQuantity * Double.parseDouble(item.sItemprice)));
        }
        arrayList.add("");
        arrayList.add("Cliente: " + orden.Userfullname);
        arrayList.add("Propina: $" + orden.Tip);
    }

    public void ToordersButtonClicked(){
        btn_volver = (Button)findViewById(R.id.btn_volverdetalle);
        btn_volver.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.lendasoft.clubercompanion.Mesas");
                        intent.putExtra("Waiterid",waiterid);
                        startActivity(intent);
                    }
                }
        );
    }

    public ArrayList<OBJ_ITEM> formatOrder(ArrayList<OBJ_ITEM> order){
        ArrayList<OBJ_ITEM> formattedOrder = new ArrayList<OBJ_ITEM>();
        for (Integer i =0;i<order.size();i++) {
            OBJ_ITEM item = order.get(i); //Obtenemos el item de la orden
            String itemId = item.sPlaceitemid; //Obtenemos su id

            if (containsitem(formattedOrder,itemId)){

            }

            else {
                for (Integer x = 0; x < order.size(); x++) { //Comparamos su id para ver si coincide con el id de otros items de la orden
                    if (itemId.equals(order.get(x).sPlaceitemid)) {
                        item.setdItemQuantity(item.getdItemQuantity() + 1);
                    }
                }
                formattedOrder.add(item);
            }
        }
        return formattedOrder;
    }

    public boolean containsitem(ArrayList<OBJ_ITEM> order,String orderid){
        for (Integer i = 0; i<order.size();i++) {
            if (order.get(i).sPlaceitemid.equals(orderid)) { return true;}
        }
        return  false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
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
