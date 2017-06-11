package com.vendingmachine;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.math.BigDecimal;
/**
 * Dummy class to test that project compiles
 */
public class Test {
	public static void main( String args[] ) {
	System.out.println( "Test" );
        Test t = new Test();	
        VendingMachine machine = new VendingMachine();
	System.out.println("Machine is ready!");
        try{
            t.acceptEvents(machine);
	}catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void acceptEvents(VendingMachine vm) throws Exception {
        String fileName = "vendingmachineevents.dat";
	InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
	try{
            Scanner scanner = new Scanner(is);  
	    while(scanner.hasNextLine()){
		String line = scanner.nextLine();
		if (line.startsWith("#")) continue;
		String[] fields = line.split(",");
                //System.out.println("Event: " + line);
                fields[0] = fields[0].trim().toUpperCase();   
                if (fields[0].startsWith("LOADCHANGE"))
	            processLoadChangeEvent(fields, vm); 
                else if (fields[0].startsWith("CONFIGUREITEMEVENT"))
                    processConfigureItemEvent(fields, vm);
                else if (fields[0].startsWith("LOADITEM"))
                    processLoadItemEvent(fields, vm);
                else if (fields[0].startsWith("PRINTINVENTORY"))
	            processPrintEvent(fields, vm);
		else if (fields[0].startsWith("PURCHASEEVENT"))
	            processPurchaseEvent(fields, vm);
                else 
		    throw new Exception("Unknown event:" + fields[0]);
            }
	    scanner.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void processPrintEvent(String[] fields, VendingMachine vm){
        try{
            vm.print();
            System.out.printf("[Done] %s %n", fields[0]);
        }catch(Exception ex){
            System.out.printf("[Fail] %s %n", fields[0]);
            ex.printStackTrace();
        }
    }
    private void processLoadChangeEvent(String[] fields, VendingMachine vm){
        if(fields.length != 3){
            System.out.println("Invalid LOADCHANGE event, wrong number of fields. Current event ignored");
            return;
        }
        try{ 
            vm.loadCoin(fields[1], fields[2]);
            System.out.printf("[Done] %s, %s, %s %n", fields[0], fields[1], fields[2]);
        }catch(Exception ex){
            System.out.printf("[Fail] %s, %s, %s %n", fields[0], fields[1], fields[2]);
            ex.printStackTrace();
        } 
    }

    private void processConfigureItemEvent(String[] fields, VendingMachine vm){
        if(fields.length != 3){
            System.out.println("Invalid CONFIGUREITEMEVENT event, wrong number of fields. Current event ignored.");
            return;
        }
        try{
            vm.configItem(fields[1], fields[2]);
            System.out.printf("[Done] %s, %s, %s %n", fields[0], fields[1], fields[2]);
        }catch(Exception ex){
            System.out.printf("[Fail] %s, %s, %s %n", fields[0], fields[1], fields[2]);
            ex.printStackTrace();
        }
    }
    
    private void processLoadItemEvent(String[] fields, VendingMachine vm){
        if(fields.length != 4){
            System.out.println("Invalid LOADITEM event, wrong number of fields. Current event ignored");
            return;
        }
        try{
            if (!vm.loadItem(fields[1], fields[2], fields[3]))
                throw new Exception(); 
            else
                System.out.printf("[Done] %s, %s, %s, %s %n", fields[0], fields[1], fields[2], fields[3]);
        }catch(Exception ex){
            System.out.printf("[Fail] %s, %s, %s, %s %n", fields[0], fields[1], fields[2], fields[3]);
            ex.printStackTrace();
        }
    }
    
    private void processPurchaseEvent(String[] fields, VendingMachine vm){
        if (fields.length != 4){
            System.out.println("Invalid PURCHASEEVENT event, wrong number of fields. Current event ignore.");
            return;
        }
        try{
            BigDecimal change = vm.purchase(fields[1], fields[2], fields[3]);
            System.out.printf("[Done] %s, %s, %s, %s %n", fields[0], fields[1], fields[2], fields[3]);
        }catch(Exception ex){
            System.out.printf("[Fail] %s, %s, %s, %s %n", fields[0], fields[1], fields[2], fields[3]);
            ex.printStackTrace();
        }
    }
}
