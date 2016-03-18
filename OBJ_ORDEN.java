package com.lendasoft.clubercompanion;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by victorrosas on 11/10/15.
 */
public class OBJ_ORDEN implements Serializable {
    private static final long serialVersionUID = 1L;
    public String OrderId;
    public String TotalPayment;
    public String Tip;
    public Integer Status;
    public Integer State;
    public String Userid;
    public String Userfullname;
    public String TableNumber;
    public Integer Priority;
    public ArrayList<OBJ_ITEM> Items;

    public String getTip() {
        return Tip;
    }

    public void setTip(String tip) {
        Tip = tip;
    }

    public String getUserfullname() {
        return Userfullname;
    }

    public void setUserfullname(String userfullname) {
        Userfullname = userfullname;
    }

    public OBJ_ORDEN(String orderid,String tablenumber,String totalpayment,ArrayList<OBJ_ITEM> items,Integer state,String clientname,String tip){
    this.OrderId = orderid;
    this.TableNumber = tablenumber;
    this.TotalPayment = totalpayment;
    this.Items = items;
    this.State = state;
    this.Tip = tip;
    this.Userfullname = clientname;
}



    public OBJ_ORDEN() {

    }

    public void setOrderId(String orderid){
        this.OrderId = orderid;
    }

    public void setTotalPayment(String totalPayment){
        this.TotalPayment = totalPayment;
    }

    public void setTableNumber(String tableNumber){
        this.TableNumber = tableNumber;
    }

    public void setItems(ArrayList<OBJ_ITEM> items){
        this.Items = items;
    }

    public void setPriority(Integer priority){
        this.State = priority;
    }

    public String getOrderid(){return OrderId;}

    public String getTableNumber(){return TableNumber;}

    public String getTotalPayment() {return TotalPayment;}

    public ArrayList<OBJ_ITEM> getItems() {return Items;}

    public Integer getPriority() { return State;}

}
