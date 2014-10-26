package com.pbgis.server.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class KVPUtil {

	public static Map<String, Object> parseQueryString(String queryString) {
        String[] st = queryString.split("&");
        Map<String, Object> result = new HashMap<String, Object>();
        for(int i=0;i<st.length;i++){
        	 String token = st[i];
             String[] keyValuePair;
             int idx = token.indexOf('=');
             if(idx > 0) {
                 keyValuePair = new String[2];
                 keyValuePair[0] = token.substring(0, idx);
                 keyValuePair[1] = token.substring(idx + 1);
             } else {
                 keyValuePair = new String[1];
                 keyValuePair[0] = token;
             }
             
             if ( keyValuePair.length > 1 ) {
                 try {
                     keyValuePair[1] = URLDecoder.decode(keyValuePair[1], "ISO-8859-1");
                 } catch(UnsupportedEncodingException e) {
                     throw new RuntimeException("Totally unexpected... is your JVM busted?", e);
                 }
             }
             String key = keyValuePair[0];
             String value = keyValuePair.length > 1 ?  keyValuePair[1] : "";
             if(result.get(key) == null) {
                 result.put(key, value);
             } else {
                 String[] array;
                 Object oldValue = result.get(key);
                 if(oldValue instanceof String) {
                     array = new String[2];
                     array[0] = (String) oldValue;
                     array[1] = value;
                 } else {
                     String[] oldArray = (String[]) oldValue;
                     array = new String[oldArray.length + 1];
                     System.arraycopy(oldArray, 0, array, 0, oldArray.length);
                     array[oldArray.length] = value;
                 }
                 result.put(key, array);
             }
        }
        return result;
    }
}
