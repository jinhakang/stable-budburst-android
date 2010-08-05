package cens.ucla.edu.budburst.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



//This class is to download and to upload data with server



public class SyncNetworkHelper extends Activity{
	
	final private String TAG = "SyncNetworkHelper"; 
	
	private SyncDBHelper syncDBHelper;	
	private String dataJson;
	

	
	//private static BudburstDBHelper budburstDBHelper;
	
	public SyncNetworkHelper(){
		dataJson = null;
	}
	
	public String getResult(){
		return dataJson;
	}
	
	//Download image
	public boolean download_image(String url_addr, Context cont){
		try{
			syncDBHelper = new SyncDBHelper(cont);
			SQLiteDatabase db = syncDBHelper.getReadableDatabase();

			ArrayList<Integer> imageId_arr = new ArrayList<Integer>(); 

			Cursor cursor = db.rawQuery("SELECT image_id FROM downloaded_observation;",null);
			while(cursor.moveToNext()){
				if(cursor.getInt(0) != 0)
					imageId_arr.add(cursor.getInt(0));
			}
			cursor.close();
			syncDBHelper.close();
			String BASE_PATH = "/sdcard/pbudburst/";
			
			if(!new File(BASE_PATH).exists()){
				try{new File(BASE_PATH).mkdirs();}
				catch(Exception e){
					Log.e(TAG, e.toString());
					return false;
				}
			}
				
			
			for(int i=0; i<imageId_arr.size() ;i++){
				String image_URL = url_addr + "?image_id=" + imageId_arr.get(i);
				String path  = BASE_PATH + imageId_arr.get(i) + ".jpg";
				ContentDownloader downloader = new ContentDownloader(image_URL);
				if(!downloader.downloadContentsTo(path))
					return false;
			}
			return true;
		}catch(Exception e){
			Log.e(TAG,e.toString());
			return false;
		}
	}

	public String upload_new_plant(String username, String password, Context cont){
		try{
			
			//Open database
	    	SyncDBHelper syncDBHelper = new SyncDBHelper(cont);
	    	SQLiteDatabase syncDB = syncDBHelper.getReadableDatabase();
	    	
	    	//Open database cursor
			Cursor cursor = syncDB.rawQuery("SELECT species_id, site_id FROM species_in_mystation " +
					"WHERE synced=9", null);
		    
			if(cursor.getCount() == 0){
		        cursor.close();
		        syncDBHelper.close();
				return "No new plant";
			}
			
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        
	        String result = null;

	        while(cursor.moveToNext()){
				HttpClient httpclient = new DefaultHttpClient();  
			    		    
			    String url = new String("http://cens.solidnetdns.com/~jinha/PBB/PBsite_CENS/phone/phone_service.php?add_plant&username="
			    	+username+"&password="+password);
			    HttpPost httppost = new HttpPost(url);

	        	nameValuePairs.add(new BasicNameValuePair("species_id", cursor.getString(0)));  
	        	nameValuePairs.add(new BasicNameValuePair("site_id", cursor.getString(1)));  
	        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		  
		        // Execute HTTP Post Request  
	        	HttpResponse response = httpclient.execute(httppost);
	        	result = response.toString();
		        Log.d(TAG, response.toString());
	        }
	        
	        cursor.close();
	        syncDBHelper.close();
			return result;
		}
		catch(Exception e){
			Log.e(TAG, e.toString());
			return null;
		}
	}
	
	public String upload_new_obs(String username, String password, Context cont){
		try{
			
			//Open database
	    	SyncDBHelper syncDBHelper = new SyncDBHelper(cont);
	    	SQLiteDatabase syncDB = syncDBHelper.getReadableDatabase();
	    	
	    	//Open database cursor
	    	String query = "SELECT species_id, site_id, phenophase_id, time, note, image_id FROM downloaded_observation WHERE synced=9";
			Cursor cursor = syncDB.rawQuery(query, null);
		    
			if(cursor.getCount() == 0){
		        cursor.close();
		        syncDBHelper.close();
				return "No new obs";
			}
			

			
	        // Add your data  
	        //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        
	        String result = null;

	        while(cursor.moveToNext()){
				HttpClient httpclient = new DefaultHttpClient();  
			    		    
			    String url = new String("http://cens.solidnetdns.com/~jinha/PBB/PBsite_CENS/phone/phone_service.php?submit_obs&username="
			    	+username+"&password="+password);
			    HttpPost httppost = new HttpPost(url);
			    
				String species_id = cursor.getString(0);
				String site_id = cursor.getString(1);
				String phenophase_id = cursor.getString(2);
				String time = cursor.getString(3);
				String note = cursor.getString(4);
				String image_id = cursor.getString(5);
	        	
	        	MultipartEntity entity = new MultipartEntity();
	        	entity.addPart("species_id", new StringBody(cursor.getString(0)));
	        	entity.addPart("site_id", new StringBody(cursor.getString(1)));
	        	entity.addPart("phenophase_id", new StringBody(cursor.getString(2)));
	        	entity.addPart("time", new StringBody(cursor.getString(3)));
	        	entity.addPart("note", new StringBody(cursor.getString(4)));


			    if(cursor.getInt(5) != 0){
				    File file = new File("/sdcard/pbudburst/" + cursor.getString(5) + ".jpg");
		        	entity.addPart("image", new FileBody(file));
			    }
			    	
	        	httppost.setEntity(entity);
	        	
	        	//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
		  
		        // Execute HTTP Post Request  
	        	HttpResponse response = httpclient.execute(httppost);
	        	result = response.toString();
		        Log.d(TAG, response.toString());
	        }

	        syncDBHelper.close();
	        cursor.close();
			return result;
		}
		catch(Exception e){
			Log.e(TAG, e.toString());
			return null;
		}
	}
	
	
	//Download data
	public String download_json(String url_addr){
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
					return null;
				}
				conn.disconnect();
			}
		}catch(Exception e){
			Log.e(TAG, e.toString());
			return null;
		}
		return result.toString();
	}
	
	//Upload data
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

class ContentDownloader {

	final private String TAG = "ContentDownloader"; 
	
    private String url;
    private static final int SIZE = 1024; 
    private String destinationFile;

    public ContentDownloader(String url){
        this.url = url;
    }

    public boolean downloadContentsTo(String destinationFile){
        this.destinationFile = destinationFile;
        
        if(new File(destinationFile).exists())
        	return true;

        URL urlObject = null;
        try {
            urlObject = new URL(url);
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        URLConnection urlConnection = null;
        InputStream inputStream = null;
        BufferedInputStream bufferedInput = null; 

        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutput = null;

        try{
            urlConnection = urlObject.openConnection();
            inputStream = urlConnection.getInputStream();
            bufferedInput = new BufferedInputStream(inputStream);

            outputStream = new FileOutputStream(this.destinationFile);
            bufferedOutput = new BufferedOutputStream(outputStream);

            byte[] buffer = new byte[SIZE];
            while (true){
                int noOfBytesRead = bufferedInput.read(buffer, 0, buffer.length);
                if (noOfBytesRead == -1){
                    break;
                }
                bufferedOutput.write(buffer, 0, noOfBytesRead);
            }	
        }catch (IOException e) {
        	Log.e(TAG, e.toString());
            e.printStackTrace();
            return false;
        }finally{
            closeStreams(new InputStream[]{bufferedInput, inputStream}, 
                new OutputStream[]{bufferedOutput, outputStream});
        }
        System.out.println("Downloading completed");
        return true;
    }

    private void closeStreams(
        InputStream[] inputStreams, OutputStream[] outputStreams){

        try{
            for (InputStream inputStream : inputStreams){
                if (inputStream != null){
                    inputStream.close();
                }
            }
            for (OutputStream outputStream : outputStreams){
                if (outputStream != null){
                    outputStream.close();
                }
            }
        }catch(IOException exception){
        	Log.e(TAG, exception.toString());
            exception.printStackTrace();
        }
    }
}

