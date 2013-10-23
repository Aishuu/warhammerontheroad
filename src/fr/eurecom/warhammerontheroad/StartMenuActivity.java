package fr.eurecom.warhammerontheroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class StartMenuActivity extends Activity {
	public final static String EXTRA_MESSAGE = "fr.eurecom.warhammerontheroad.MESSAGE";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_menu, menu);
		return true;
	}
	
	/** Called when the user clicks the createGame button */
	public void createGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(EXTRA_MESSAGE, MainActivity.EXTRA_MESSAGE_CREATEGAME);
	    startActivity(intent);
	}
	
	/** Called when the user clicks the joinGame button */
	public void joinGame(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(EXTRA_MESSAGE, MainActivity.EXTRA_MESSAGE_JOINGAME);
	    startActivity(intent);
	}
	
	/** Called when the user clicks the quit button */
	public void quit(View view) {
		this.finish();
	}

}
