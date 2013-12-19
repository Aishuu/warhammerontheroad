package fr.eurecom.warhammerontheroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NewGameIntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game_intro);
	}
	
	public void accessGMMenu(View view) {
		Intent intent = new Intent(this, GMMenuActivity.class);
	    startActivity(intent);
	}

	public void cancel_game_creation(View view) {
		finish();
	}
}
