package cens.ucla.edu.budburst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	final private int MENU_ADD_PLANT = 0;
	final private int MENU_ADD_SITE = 1;
	final private int MENU_LOGOUT = 2;
	final private int MENU_SYNC = 3;
	
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
	}
	
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
			intent = new Intent(Helloscr.this, Sync.class);
			startActivity(intent);
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
}
