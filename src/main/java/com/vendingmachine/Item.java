package com.vendingmachine;
import java.math.BigDecimal;

class Item{
    String name;
    BigDecimal unitPrice;
    
    Item(){
        unitPrice = new BigDecimal(0);
        name = "";
    }

    Item(String n){
        name = n;
        unitPrice = new BigDecimal(0);
    }
   
    public void setPrice(BigDecimal p){
        unitPrice = p;
    }
}
