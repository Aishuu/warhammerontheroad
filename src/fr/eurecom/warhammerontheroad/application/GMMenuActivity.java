package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Map;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Race;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GMMenuActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gm_menu);
		this.mService.getNetworkParser().sendFile("grass.png");
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
		Hero h = new Hero(this.mService.getContext(), Race.ELF);
		this.mService.getGame().addHero(h);
		this.mService.getNetworkParser().createHero(h);
	}
	
	public void seePlayersData(View view) {
		Map m = new Map(this.mService.getContext(), 10, 5, "grass.png");
		this.mService.getGame().setMap(m);
		Player p = this.mService.getGame().getPlayer("test");
		Hero h = this.mService.getGame().getHero(1);
		m.setCase(p, 1, 1);
		m.setCase(h, 1, 2);
		this.mService.getGame().waitForAction();
		this.mService.getNetworkParser().beginFight(m);
	}
	
	public void accessPlay(View view) {

	}
}
