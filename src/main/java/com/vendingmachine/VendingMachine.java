package com.vendingmachine;
import java.util.*;
import java.math.BigDecimal;

class VendingMachine{
    public static final int MAX_SLOT_COUNT = 20;
    //Key: coin denomination, Value: coin quantity 
    private Hashtable<BigDecimal, Integer > cashInventory = new Hashtable<BigDecimal, Integer>();
    //a hashtable for locating the slot for an item, each item can be in different slot at the same time
    //so use an ArrayList to store the slots
    private Hashtable<String, ArrayList<Integer>> itemSlot = new Hashtable<String, ArrayList<Integer>>(); 
    //a hashtable for the item and its price
    private Hashtable<String, BigDecimal> itemPrice = new Hashtable<String, BigDecimal>(); 
    //ArrayList of Slot, this is the item invetory
    private ArrayList<Slot> inventory;
    
    //ctor
    VendingMachine(){
        inventory = new ArrayList<Slot>(MAX_SLOT_COUNT);
        for(int i = 0; i < MAX_SLOT_COUNT; ++i)
            inventory.add(new Slot());
    }
    public boolean loadItem(String name, String qtystr, String slotstr) throws Exception{
        Integer quantity = Integer.valueOf(qtystr);
        if(quantity < 0){
            System.out.println("Item quantity should be non-negative number");
            return false;
        }
        Integer slotnum = Slot.getSlotNum(slotstr);
        if(slotnum.equals(-1))
            return false;
        inventory.get(slotnum).loadItem(name, quantity.intValue());
        ArrayList<Integer> list;
        if(itemSlot.containsKey(name.trim().toUpperCase())){
            list = itemSlot.get(name.trim().toUpperCase());
        }else{
            list = new ArrayList<Integer>();
            itemSlot.put(name.trim().toUpperCase(), list);
        }
        if(list.indexOf(slotnum) == -1)
            list.add(slotnum); 
        return true;
    }
    public boolean configItem(String name, String priceStr){
        BigDecimal p = new BigDecimal(priceStr);
        itemPrice.put(name.trim().toUpperCase(), p);
        return true;
    } 
    public boolean print(){
        printItemInventory();
        printCoinInventory();
        return true;
    }
    public BigDecimal purchase(String slotstr, String qtystr, String amountstr) throws Exception{
        Integer slotnum = Slot.getSlotNum(slotstr);
        if(slotnum.equals(-1))
            throw new Exception(); 
        Integer quantity = Integer.valueOf(qtystr);
        if(quantity.compareTo(0)< 0)
            throw new Exception("Item quantity should be non-negative number");
        if(!inventory.get(slotnum.intValue()).isEnough(quantity.intValue()))
            throw new Exception();
        BigDecimal amount = new BigDecimal(amountstr); 
        BigDecimal amountdue = calcAmount(inventory.get(slotnum.intValue()).item, quantity);
        if(amountdue.compareTo(amount) > 1)
            throw new Exception("Not enough payment, " + amountdue.toString() + " needed.");
        BigDecimal change = amount.subtract(amountdue).setScale(2, BigDecimal.ROUND_HALF_UP); 
        Hashtable<BigDecimal, Integer> changes;
        if(change.compareTo(new BigDecimal(0)) > 0 ){
            changes = Coin.findChange(change, cashInventory);
            if(changes.size() == 0)
                throw new Exception("Not enough changes, can't sell.");
            System.out.println("Changes: " + change.toString()); 
            System.out.println("Change details: "); 
            printCoin(changes);
            updateCashInventory(changes);
        }else
            System.out.println("No need changes");
        inventory.get(slotnum.intValue()).updateQty(quantity.intValue());
        //Should update the coin inventory too after this purchase, but since 
        //there's only a total amount in the purchaseevent, can't figure out 
        //what coins should be updated, so this step is ignored here
        //updateCoinInventory(); 
        return change;
    }
    private BigDecimal calcAmount(String itemname, Integer quantity) throws Exception{
        if(!itemPrice.containsKey(itemname.trim().toUpperCase())){
            throw new Exception("No price found for " + itemname + ", cannot sell.");
        }
        BigDecimal unitPrice = itemPrice.get(itemname.trim().toUpperCase());
        return unitPrice.multiply(new BigDecimal(quantity));
    }
     
    //Load Coins
    public void loadCoin(String denom, String quantitystr) throws Exception{
        Integer quantity = Integer.valueOf(quantitystr);
	if(quantity <0)
	    throw new Exception("Change quantity should be non-negative number");
        Coin coin = new Coin();
        BigDecimal c = coin.CreateCoin(denom);
	if(cashInventory.containsKey(c))
	    cashInventory.put(c, new Integer(cashInventory.get(c)) + quantity);
	else
	    cashInventory.put(c, new Integer(quantity));
    }
    private void printCoin(Hashtable<BigDecimal, Integer> coins){
        System.out.printf("\t\t%-10s %-10s %-10s%n", "Coin", "Amount", "Value");
        BigDecimal total = new BigDecimal(0);
        BigDecimal amount = new BigDecimal(0);
	for(BigDecimal d: coins.keySet()){
            amount = d.multiply(new BigDecimal(coins.get(d)));
            total = total.add(amount);
            System.out.printf("\t\t%-10s %-10s %-10s%n", d.toString(), coins.get(d).toString(),amount.toString());
        }
        System.out.println("\t\t------------------------");
        System.out.printf("\t\t%-10s %-10s %-10s%n", "Total", "", total.toString());
    }
    private void updateCashInventory(Hashtable<BigDecimal, Integer> changes) throws Exception{
        for(BigDecimal d: changes.keySet()){
           if(cashInventory.containsKey(d))
               cashInventory.put(d, cashInventory.get(d) - changes.get(d));
           else
               throw new Exception("Cash Invetory Wrong! key " + d.toString() + " not found in cash Invetory");
        }
    } 
    //Print Coin Inventory
    private void printCoinInventory(){
        System.out.println("\t\t**** Cash Inventory ****");
        printCoin(cashInventory); 
    }
    //Print Item Inventory
    private void printItemInventory(){
        System.out.println("\t\t**** Item Inventory ****");
        System.out.printf("\t\t%-10s %-10s %-10s%n", "Item", "Amount", "Slot");
        for(String item: itemSlot.keySet()){
            for(int slot: itemSlot.get(item)){
                if(!inventory.get(slot).empty())
                    inventory.get(slot).print();
            }
        }
            
        System.out.println("\t\t------------------------"); 
    }
}
