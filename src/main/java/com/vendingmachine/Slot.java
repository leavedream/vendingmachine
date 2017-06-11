package com.vendingmachine;

class Slot{
    String item;
    int qty;
    int ID;
    private static int instCnt = 0;
    Slot(){
        item = new String();
        ID = instCnt++;
    }

    public int getQty(){
        return qty;
    }

    public boolean empty(){
        return qty == 0 ? true: false;
    }

    public int updateQty(int newq) throws Exception{
        qty += newq;
        return qty;
    }
   
    public void loadItem(String itemtoload, int quantity) throws Exception{
        if(quantity <=0)
            throw new Exception("Wrong load quantity");
        if(item.equalsIgnoreCase(itemtoload))
            updateQty(quantity);
        else if(empty()){
            item = itemtoload;
            updateQty(quantity);
        }else
            throw new Exception("Can't load different type of items into same slot");
    }       
 
    public void print(){
        if(empty()) return;
        System.out.printf("\t\t%-10s %-10s %-10s%n", item, qty, ID);
    }   
    
    public boolean isEnough(int quantity){
        if((qty + quantity)<0){
            System.out.println("Not enought " + item + " to sell. Current qty=" + String.valueOf(qty));
            return false;
        }
        return true;
    }
        
    public static Integer getSlotNum(String slotstr){
        try{
            Integer n = Integer.valueOf(slotstr);
            if(n.intValue() < 0 || n.intValue() >= VendingMachine.MAX_SLOT_COUNT){
                System.out.println("Slot number should be between 0 and " + String.valueOf(VendingMachine.MAX_SLOT_COUNT));
                return new Integer(-1);
            }
            return n;
        }catch(Exception ex){
            ex.printStackTrace();
            return new Integer(-1);
        }
    }
}
