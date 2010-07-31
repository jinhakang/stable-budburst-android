package cens.ucla.edu.budburst;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Helloscr extends Activity{

	private String username;
	private String password;
	private SharedPreferences pref;
	private int progressValue;
	private String result;
	private Sync sync;

	//MENU contants
	final private int MENU_ADD_PLANT = 0;
	final private int MENU_ADD_SITE = 1;
	final private int MENU_LOGOUT = 2;
	final private int MENU_SYNC = 3;
	
	ProgressDialog mProgress;
	boolean mQuit;
	UpdateThread mThread;
			
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helloscr);
		
		//Retrieve username and password
		pref = getSharedPreferences("userinfo",0);
		username = pref.getString("Username","");
		password = pref.getString("Password","");
		
		//Display instruction message
		TextView textViewHello = (TextView)findViewById(R.id.hello_textview);
		textViewHello.setText("Hello " + username + ",\n\n" + getString(R.string.instruction));
		
		//My plant button
		Button buttonMyplant = (Button)findViewById(R.id.myplant);
		buttonMyplant.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
			}
		}
		);
		
		//Shared plant button
		Button buttonSharedplant = (Button)findViewById(R.id.sharedplant);
		buttonSharedplant.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(Helloscr.this,"Coming soon..!",Toast.LENGTH_SHORT).show();
			}
			}
		);
		
		//Sync button
		Button buttonSync = (Button)findViewById(R.id.sync);
		buttonSync.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				//Show progress
				showDialog(0);
				mProgress.setProgress(0);
				
				//Call a thread to transfer data
				mQuit = false;
				mThread = new UpdateThread();
				mThread.start();
				
			}
			}
		);
		
		buttonMyplant.setSelected(true);
	}
	
	/////////////////////////////////////////////////////////////
	//Menu option
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		SubMenu addButton = menu.addSubMenu("Add")
			.setIcon(android.R.drawable.ic_menu_add);
		addButton.add(0,MENU_ADD_PLANT,0,"Add Plant");
		addButton.add(0,MENU_ADD_SITE,0,"Add Site");		
		
		menu.add(0,MENU_SYNC,0,"Sync").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0,MENU_LOGOUT,0,"Log out").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				
		return true;
	}
	
	//Menu option selection handling
	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent;
		switch(item.getItemId()){
		case MENU_SYNC:
			//Display pregress dialog
			showDialog(0);
			mProgress.setProgress(0);
			
			//Run thread
			mQuit = false;
			mThread = new UpdateThread();
			mThread.start();
			return true;
		case MENU_ADD_PLANT:
			intent = new Intent(Helloscr.this, AddPlant.class);
			startActivity(intent);
			return true;
		case MENU_ADD_SITE:
			Toast.makeText(Helloscr.this,"Sorry, it's coming soon..",Toast.LENGTH_SHORT).show();
			return true;
		case MENU_LOGOUT:
			new AlertDialog.Builder(Helloscr.this)
				.setTitle("Question")
				.setMessage("You might lose your unsynced data if you log out. Do you want to log out?")
				.setPositiveButton("Yes",mClick)
				.setNegativeButton("no",mClick)
				.show();
			return true;
		}
		return false;
	}
	
	//Dialog confirm message if user clicks logout button
	DialogInterface.OnClickListener mClick =
		new DialogInterface.OnClickListener(){
		public void onClick(DialogInterface dialog, int whichButton){
			if(whichButton == DialogInterface.BUTTON1){
				
				SharedPreferences.Editor edit = pref.edit();				
				edit.putString("Username","");
				edit.putString("Password","");
				edit.commit();
				
				Intent intent = new Intent(Helloscr.this, Login.class);
				startActivity(intent);
			}else{
			}
		}
	};
	//Menu option
	/////////////////////////////////////////////////////////////
	
	//Thread
	class UpdateThread extends Thread{
		public void run(){
			sync = new Sync();

			
		//0.Upload first
			if(sync.upload_json(getString(R.string.upload_observation_URL))){
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 50;
				mHandler.sendMessage(msg);
			}
			
		//1.Download user site
			
			if(sync.download_json(getString(R.string.get_observation_URL))){
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 50;
				mHandler.sendMessage(msg);

			}else{
				Message msg = mHandler.obtainMessage();
				msg.arg1 = -1;
				mHandler.sendMessage(msg);
			}
			
		//2.Download user species
			if(sync.download_json(getString(R.string.get_user_spcies_URL))){
				Message msg = mHandler.obtainMessage();
				msg.arg1 = 90;
				mHandler.sendMessage(msg);				
			}else{
				Message msg = mHandler.obtainMessage();
				msg.arg1 = -1;
				mHandler.sendMessage(msg);
			}
			
		//3.Download user observation
			Message msg = mHandler.obtainMessage();
			msg.arg1 = 100;
			mHandler.sendMessage(msg);				
		}
	}
	
	//Message handler for download thread
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			progressValue = msg.arg1;
			
			
			if(progressValue == -1){//Download fails
				mQuit = true;
				dismissDialog(0);
				Toast.makeText(Helloscr.this,"Please check your network.",Toast.LENGTH_SHORT).show();
				progressValue = 0;
			}
			else if(progressValue < 100){
				mProgress.setProgress(progressValue);
				
				result = sync.getResult();
				if(result != null)
				{
					TextView textViewHello = (TextView)findViewById(R.id.hello_textview);
					textViewHello.setText(result);
				}
			}else{ //Download done.
				mQuit = true;
				dismissDialog(0);
			}
		}
	};
	
	//Dialog for download thread
	protected Dialog onCreateDialog(int id){
		switch(id){
		case 0:
			mProgress = new ProgressDialog(this);
			mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgress.setTitle("Syncing");
			mProgress.setMessage("Wait...");
			mProgress.setCancelable(false);
			mProgress.setButton("Cancel",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int wchihButton){
					mQuit = true;
					dismissDialog(0);
				}
			});
			return mProgress;
		}
		return null;
	}
}
