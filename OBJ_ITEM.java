package com.lendasoft.clubercompanion;

import java.io.Serializable;

/**
 * Created by victorrosas on 3/1/16.
 */
public class OBJ_ITEM implements Serializable{
    public String sOrderid;
    public String sOrderitemid;
    public String sItemprice;
    public String sPlaceitemid;
    public String sPlaceitemname;
    public Integer dItemQuantity = 0;



    public OBJ_ITEM(String orderid,String orderitemid,String itemprice,String placeitemid,String placeitemname){
        this.sOrderid = orderid;
        this.sOrderitemid = orderitemid;
        this.sItemprice = itemprice;
        this.sPlaceitemid = placeitemid;
        this.sPlaceitemname = placeitemname;
    }

    public String getsOrderid() {return sOrderid;}

    public void setsOrderid(String sOrderid) { this.sOrderid = sOrderid; }

    public String getsOrderitemid() {
        return sOrderitemid;
    }

    public void setsOrderitemid(String sOrderitemid) {
        this.sOrderitemid = sOrderitemid;
    }

    public String getsItemprice() {
        return sItemprice;
    }

    public void setsItemprice(String sItemprice) {this.sItemprice = sItemprice;}

    public String getsPlaceitemid() {
        return sPlaceitemid;
    }

    public void setsPlaceitemid(String sPlaceitemid) {
        this.sPlaceitemid = sPlaceitemid;
    }

    public String getsPlaceitemname() {
        return sPlaceitemname;
    }

    public void setsPlaceitemname(String sPlaceitemname) {
        this.sPlaceitemname = sPlaceitemname;
    }


    public Integer getdItemQuantity() {return dItemQuantity;}

    public void setdItemQuantity(Integer dItemQuantity) {this.dItemQuantity = dItemQuantity;}
}
