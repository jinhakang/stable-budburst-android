package cens.ucla.edu.budburst.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SyncDBHelper extends SQLiteOpenHelper{
	
	public SyncDBHelper(Context context){
		super(context, "syncBudburst.db", null, 7);
	}
	
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE species_in_mystation (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"species_id NUMERIC," +
				"site_id NUMERIC," +
				"site_name TEXT," +
				"protocol_id NUMERIC," +
				"synced NUMERIC);");
		
		db.execSQL("CREATE TABLE downloaded_observation (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"species_id NUMERIC," +
				"site_id NUMERIC," +
				"phenophase_id NUMERIC," +
				"image_id NUMERIC," +
				"time TEXT," +
				"note TEXT," +
				"synced NUMERIC);");

	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS species_in_mystation;");
		db.execSQL("DROP TABLE IF EXISTS downloaded_observation;");
		
		onCreate(db);
	}
	
	public void clearAllTable(Context cont){
		SyncDBHelper dbhelper = new SyncDBHelper(cont);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		db.execSQL("DELETE FROM species_in_mystation;");
		db.execSQL("DELETE FROM downloaded_observation;");
		
		dbhelper.close();
	}
}
