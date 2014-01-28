package fr.eurecom.warhammerontheroad.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Player;

public class JoinGameActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);
	}
	
	public void enterGame(View view) {
		EditText edit = (EditText) findViewById(R.id.join_game_number);
		EditText edit_name = (EditText) findViewById(R.id.join_game_name);
		if(edit.getText().length() == 0 || edit_name.getText().length() == 0)
			return;
		try {
			int id = Integer.parseInt(edit.getText().toString());
			this.mService.getGame().setMe(new Player(this.mService.getContext(), edit_name.getText().toString()));
			this.mService.getGame().bound(id);
			this.mService.getNetworkParser().bind(id);
			Intent intent = new Intent(this, PlayerMenuActivity.class);
		    startActivity(intent);
		} catch (NumberFormatException e) {
			Log.e("JoinGameActivity", "This is not a number !");
			edit.setText("");
		}
	}
}
