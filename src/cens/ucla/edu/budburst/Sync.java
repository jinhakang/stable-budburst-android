package cens.ucla.edu.budburst;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//This class is to download and to upload data with server


public class Sync{
	
	private String dataJson;
	
	public Sync(){
		dataJson = null;
	}
	
	public String getResult(){
		return dataJson;
	}
	
	//Upload data
	public boolean download_json(String url_addr){
		StringBuilder result = new StringBuilder();
		try{
			URL url = new URL(url_addr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if(conn != null){
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result.append(line+'\n');
					}
					br.close();
				}
				else{
					conn.disconnect();
					return false;
				}
				conn.disconnect();
			}
		}catch(Exception e){
			return false;
		}
		dataJson = result.toString();
		return true;
	}
	
	//Downlaod data
	public boolean upload_json(String url_addr){
		try{
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean insert_downloaded_json_into_database(String downloaded_json){
		try{
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
	
	
		

