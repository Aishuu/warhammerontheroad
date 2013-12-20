package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GMMenuActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gm_menu);
	}
	
	public void accessChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
	    startActivity(intent);
	}
	
	public void openBook(View view) {

	}
	
	public void writeStory(View view) {

	}
	
	public void createSupportChara(View view) {

	}
	
	public void seePlayersData(View view) {

	}
	
	public void accessPlay(View view) {

	}
}
