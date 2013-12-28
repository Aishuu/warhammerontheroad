package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Player;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class JoinGameActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);
	}
	
	public void enterGame(View view) {
		EditText edit = (EditText) findViewById(R.id.join_game_number);
		if(edit.getText().length() == 0)
			return;
		try {
			int id = Integer.parseInt(edit.getText().toString());
			this.mService.getGame().setPlayer(new Player(this, "George"));
			this.mService.getNetworkParser().bind(id);
			Intent intent = new Intent(this, PlayerMenuActivity.class);
		    startActivity(intent);
		} catch (NumberFormatException e) {
			Log.e("JoinGameActivity", "This is not a number !");
			edit.setText("");
		}
	}
}
