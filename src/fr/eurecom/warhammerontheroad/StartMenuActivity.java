package fr.eurecom.warhammerontheroad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class StartMenuActivity extends WotrActivity {
	@SuppressWarnings("unused")
	private final static String TAG = 			"StartMenuActivity";


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
		Intent intent = new Intent(this, CreateGameActivity.class);
		startActivity(intent);
	}

	/** Called when the user clicks the joinGame button */
	public void joinGame(View view) {
		Intent intent = new Intent(this, JoinGameActivity.class);
		startActivity(intent);
	}

	/** Called when the user clicks the quit button */
	public void quit(View view) {
		this.finish();
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

}
