package it.unina.android.ripper_service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Activity that can be used to manually start AndroidRipperService
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				startService(new Intent("it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE"));

			}
		});
        
        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				stopService(new Intent("it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE"));
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
