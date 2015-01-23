package com.gi.server.core.service.tokenservice;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class TokensManager {
	
	private static Hashtable<String, Date> tokens = new Hashtable<String, Date>();

	public static Hashtable<String, Date> getTokens() {
		return tokens;
	}
	
	public synchronized static void clearExpiredTokens(){
		Date dateNow = new Date();
		Set<String> keys = tokens.keySet();
		Iterator<String> itKey = keys.iterator();
		while(itKey.hasNext()){
			String key = itKey.next();
			try{
				Date dateExpire = tokens.get(key);
				if(dateNow.after(dateExpire)){
					tokens.remove(key);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	
}
