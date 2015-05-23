package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class Client {
	private static CacheServiceInterface cache1 = null;
	private static CacheServiceInterface cache2 = null;
	private static CacheServiceInterface cache3 = null;
	
    public static void main(String[] args) {
    	try {
    		System.out.println("Starting Cache Client...");
    		
            cache1 = new DistributedCacheService("http://localhost:3000");
            cache2 = new DistributedCacheService("http://localhost:3001");
            cache3 = new DistributedCacheService("http://localhost:3002");
            
	    	if (args.length > 0) {
	    		if (args[0].equals("write")) {
	    			write();
	    		} else if (args[0].equals("read")) {
	    			CRDTClient.readOnRepair(cache1, cache2, cache3);
	    		}
	    	}
	    	
	    	System.out.println("Exiting Cache Client...");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}        
    }
    
    public static void write() throws Exception {       
        long key = 1;
        String value = "a";
        
        Future<HttpResponse<JsonNode>> server3000 = cache1.put(key, value);
        Future<HttpResponse<JsonNode>> server3001 = cache2.put(key, value);
        Future<HttpResponse<JsonNode>> server3002 = cache3.put(key, value);
        
        final CountDownLatch countDown = new CountDownLatch(3);
        
        try {
        	server3000.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server3001.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server3002.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }

        countDown.await();
        
        if (DistributedCacheService.successCount.intValue() < 2) {	        	
        	cache1.delete(key);
        	cache2.delete(key);
        	cache3.delete(key);
        } else {
        	cache1.get(key);
        	cache2.get(key);
        	cache3.get(key);
        	Thread.sleep(1000);
        	System.out.println("Server A says: " + cache1.getValue());
    	    System.out.println("Server B says: " + cache2.getValue());
    	    System.out.println("Server C says: " + cache3.getValue());
        }
        DistributedCacheService.successCount = new AtomicInteger();
    }
}
