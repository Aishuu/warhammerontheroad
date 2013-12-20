package fr.eurecom.warhammerontheroad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JoinGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);
	}
	
	public void enterGame(View view) {
		Intent intent = new Intent(this, PlayerMenuActivity.class);
	    startActivity(intent);
	}
}
