package com.lendasoft.clubercompanion;

import java.lang.reflect.Array;
import java.io.Serializable;
/**
 * Created by victorrosas on 11/10/15.
 */
public class OBJ_ORDEN implements Serializable {
    private static final long serialVersionUID = 1L;
    public String OrderId;
    public String TotalPayment;
    public String TableNumber;
    public String[]  Items;

public OBJ_ORDEN(String orderid,String tablenumber,String totalpayment,String[] items){
    OrderId = orderid;
    TableNumber = tablenumber;
    TotalPayment = totalpayment;
    Items = items;
}

    public OBJ_ORDEN() {

    }

    public void setOrderId(String orderid){
        OrderId = orderid;
    }

    public void setTotalPayment(String totalPayment){
        TotalPayment = totalPayment;
    }

    public void setTableNumber(String tableNumber){
        TableNumber = tableNumber;
    }

    public void setItems(String[] items){
        Items = items;
    }

}
