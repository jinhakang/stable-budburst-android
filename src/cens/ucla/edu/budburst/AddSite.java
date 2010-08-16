package cens.ucla.edu.budburst;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

public class AddSite extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsite);
		
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		LocationManager lmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		String bestprovider = lmanager.getBestProvider(criteria, true);
		Location cur_location = lmanager.getLastKnownLocation(bestprovider);
		
		EditText latitude = (EditText)this.findViewById(R.id.latitude);
		latitude.setText(String.valueOf(cur_location.getLatitude()));

		EditText longitude = (EditText)this.findViewById(R.id.longitude);
		longitude.setText(String.valueOf(cur_location.getLongitude()));
	}
}
