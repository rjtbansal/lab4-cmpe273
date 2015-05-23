package edu.sjsu.cmpe.cache.client;
import java.util.HashMap;
import java.util.Map;

public class CRDTClient {
    public static void readOnRepair(CacheServiceInterface arg1, CacheServiceInterface arg2,
    		CacheServiceInterface arg3) throws Exception {
    	CacheServiceInterface cache1  = arg1;
    	CacheServiceInterface cache2  = arg2;
    	CacheServiceInterface cache3  = arg3;
    	
        long key = 1;
        String value = "a";
        
        cache1.put(key, value);
        cache2.put(key, value);
        cache3.put(key, value);
        
        System.out.println("Setting value: a");
        Thread.sleep(30000);
        
        cache1.get(1);
	    cache2.get(1);
	    cache3.get(1);
	        
	    System.out.println("Getting value: a");
	    Thread.sleep(1000);
	    
	    System.out.println("Server A says: " + cache1.getValue());
	    System.out.println("Server B says: " + cache2.getValue());
	    System.out.println("Server C says: " + cache3.getValue());
        
        value = "b";
        cache1.put(key, value);
        cache2.put(key, value);
        cache3.put(key, value);
        
        System.out.println("Setting value: b");
        Thread.sleep(30000);
	        
	    cache1.get(1);
	    cache2.get(1);
	    cache3.get(1);
	        
	    System.out.println("Getting value: b");
	    Thread.sleep(1000);
	    
	    System.out.println("Server A says: " + cache1.getValue());
	    System.out.println("Server B says: " + cache2.getValue());
	    System.out.println("Server C says: " + cache3.getValue());
	        
	    String[] values = {cache1.getValue(), cache2.getValue(), cache3.getValue()};
	    
	    Map<String, Integer> map = new HashMap<String, Integer>();
	    String majority = null;
	    for (String eachValue : values) {
	        Integer countValue = map.get(eachValue);
	        map.put(eachValue, countValue != null ? countValue+1 : 1);
	        if (map.get(eachValue) > values.length / 2) {
	        	majority = eachValue;
	        	break;
	        }	
	    }
	    
	    cache1.put(key, majority);
        cache2.put(key, majority);
        cache3.put(key, majority);
        
        System.out.println("Repairing value: b");
	    Thread.sleep(1000);
	    
	    cache1.get(key);
        cache2.get(key);
        cache3.get(key);
        
        System.out.println("Getting value b after repair: ");
	    Thread.sleep(1000);
	    
	    System.out.println("Server A says: " + cache1.getValue());
	    System.out.println("Server B says: " + cache2.getValue());
	    System.out.println("Server C says: " + cache3.getValue());
    }
}
