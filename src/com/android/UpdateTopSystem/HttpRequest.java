package com.android.UpdateTopSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class HttpRequest {
	public static JSONObject httpRequest(String url,Map<String, String> params) throws Exception{
		if((params != null) && (params.size() > 0)){
			url += "?";
			
			Set<String> keys = params.keySet(); 
			for(String key:keys){
				url += key;
				url += "=";
				url += params.get(key);
				url += "&";
			}
			url = url.substring(0, url.length()-1);
		}
		
		System.out.println("sendPost:"+url);
		String result = sendPost(url);
		
		JSONObject jsonObject = new JSONObject(result);
		if(jsonObject.getInt("errorCode") != 200){
			throw new RuntimeException(jsonObject.getString("errorMsg"));
		}
		jsonObject = jsonObject.getJSONObject("ret");
		
		return jsonObject;
	}
	
	private static String sendPost(String url) throws Exception{
		ByteArrayOutputStream out = null;
        InputStream in = null;  
        String result = "";  
       
        try {  
            URL realUrl = new URL(url);  
            //鎵撳紑鍜孶RL涔嬮棿鐨勮繛鎺�  
            URLConnection conn = realUrl.openConnection();  
            //璁剧疆閫氱敤鐨勮姹傚睘鎬�  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  "KuoxuePad");  
            //鍙戦�丳OST璇锋眰蹇呴』璁剧疆濡備笅涓よ  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            
            //瀹氫箟BufferedReader杈撳叆娴佹潵璇诲彇URL鐨勫搷搴�  
            in = conn.getInputStream();  
            out = new ByteArrayOutputStream(10*1024);
            byte[] buffer = new byte[2048];
            int count;
            while ((count = in.read(buffer)) != -1) {											
				out.write(buffer, 0, count);
			} 
            result = new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {  
            System.out.println("鍙戦�丳OST璇锋眰鍑虹幇寮傚父锛�" + e);  
            e.printStackTrace();  
            throw e;
        }  
        
        //浣跨敤finally鍧楁潵鍏抽棴杈撳嚭娴併�佽緭鍏ユ祦  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if ( in != null) { 
                	in .close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        
        return result;
	}
}
