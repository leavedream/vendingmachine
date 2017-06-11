package com.vendingmachine;
import java.util.*;
import java.math.BigDecimal;

class Coin{
    public static final BigDecimal PENNY = new BigDecimal("0.01");
    public static final BigDecimal NICKEL = new BigDecimal("0.05");
    public static final BigDecimal DIME = new BigDecimal("0.10");
    public static final BigDecimal QUARTER = new BigDecimal("0.25");
    public static final BigDecimal HALF = new BigDecimal("0.5");
    public static final BigDecimal DOLLAR = new BigDecimal("1.0");
    
    public static final BigDecimal CreateCoin(String str) throws Exception {
        BigDecimal d;
        try{
            d = new BigDecimal(str);
        }catch(Exception ex){
            throw new Exception("Wrong coin denomination " + str, ex );
	}   
        if(d.compareTo(PENNY) == 0){
            return PENNY;
        }else if(d.compareTo(NICKEL) == 0){
            return NICKEL;
        }else if(d.compareTo(DIME) == 0){
            return DIME;
        }else if(d.compareTo(QUARTER) == 0){
            return QUARTER;
        }else if(d.compareTo(HALF) == 0){
            return HALF;
        }else if(d.compareTo(DOLLAR) == 0){
            return DOLLAR;
        }else{
            throw new Exception("Wrong coin denomination " + str);
        }
    }
  
    public static final Hashtable<BigDecimal, Integer> findChange(BigDecimal amount, Hashtable<BigDecimal, Integer> cashInventory) throws Exception {
        int V = amount.multiply(new BigDecimal(100)).intValue();
        int [] coins = new int[cashInventory.size()];
        int [] count = new int[cashInventory.size()]; 
        int num = 0;
        for(BigDecimal d: cashInventory.keySet()){
            coins[num] = d.multiply(new BigDecimal(100)).intValue();
            count[num] = cashInventory.get(d).intValue();
            num++;
        }
        int [][] dp = new int[num+1][V+1];
        ArrayList<ArrayList<Hashtable<Integer, Integer>>> change = new ArrayList<ArrayList<Hashtable<Integer, Integer>>>();
        //Initialize dp array
        //System.out.format("Init dp V. V=%d, amount=%s, num=%d %n", V, amount.toString(), num); 
        for(int i = 0; i <= V; ++i){
            dp[0][i] = Integer.MAX_VALUE; 
        }
        for(int i = 0; i <= num; ++i){
            dp[i][0] = 0;
        }
        for(int i = 0; i <= num; ++i){
            ArrayList<Hashtable<Integer, Integer>> tmp = new ArrayList<Hashtable<Integer, Integer>>();
            for(int j = 0; j <= V; ++j){
                tmp.add(new Hashtable<Integer, Integer>());
            }
            change.add(tmp);
        }
        for(int i = 1; i <= num; ++i){
            for(int j = 1; j <= V; ++j){
                int k = Math.min(count[i-1], j / coins[i-1]);
                dp[i][j] = Integer.MAX_VALUE;
                while(k>=0){
                    if(j >= k * coins[i-1] && dp[i-1][j - k * coins[i-1]] != Integer.MAX_VALUE && (dp[i][j] > dp[i-1][j - k * coins[i-1]] + k)){
                        //System.out.printf("i=%d, j=%d, k=%d, ",i,j, k);
                        //System.out.printf("j - k * coins[i-1] = %d%n", j - k * coins[i-1]); 
                        dp[i][j] = dp[i-1][j - k * coins[i-1]] + k;
                        Hashtable<Integer, Integer> tmp = change.get(i-1).get(j - k * coins[i-1]);
                        //System.out.printf("change.get(i-1).get(j-k*coins[i-1]): %s", tmp.toString());
                        Integer key = new Integer(coins[i-1]);
                        change.get(i).set(j, new Hashtable<Integer, Integer>());
                        for(Integer key2: tmp.keySet())
                            change.get(i).get(j).put(key2, tmp.get(key2));
                        if(k > 0){
                            if(change.get(i).get(j).containsKey(key))
                                change.get(i).get(j).put(key, change.get(i).get(j).get(key) + k);
                            else
                                change.get(i).get(j).put(key, new Integer(k));
                        }
                        //System.out.printf("Change for %d: %s ", j, change.get(i).get(j).toString());
                        //System.out.printf("dp[%d][%d] = %d%n", i, j, dp[i][j]);
                    }
                    k--;
                }
            }
        }
        Hashtable<BigDecimal, Integer> ret = new Hashtable<BigDecimal, Integer>();
        for(Integer i: change.get(num).get(V).keySet())
            ret.put(CreateCoin(Double.toString(i / 100.0)), change.get(num).get(V).get(i)); 
        return ret;
    }
}
