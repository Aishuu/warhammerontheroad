package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayerMenuActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_menu);
	}
	
	public void accessChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
	    startActivity(intent);
	}
	
	public void accessPlay(View view) {
		
	}
	
	public void accessStory(View view) {
		
	}
	
	public void createCharaDetails(View view) {
		Intent intent = new Intent(this, CharaCreationDetailsActivity.class);
	    startActivity(intent);
	}
}
