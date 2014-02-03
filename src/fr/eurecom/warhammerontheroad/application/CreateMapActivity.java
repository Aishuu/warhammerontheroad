package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Map;
import fr.eurecom.warhammerontheroad.model.Player;
import android.os.Bundle;
import android.view.Menu;

public class CreateMapActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_map);
		
		// TODO: move that
		this.mService.getNetworkParser().sendFile("grass.png");
		Map m = new Map(this.mService.getContext(), 10, 5, "grass.png");
		this.mService.getGame().setMap(m);
		int y=1;
		for(Hero h: this.mService.getGame().getHeros()) {
			if(h instanceof Player)
				m.setCase(h, 1, 1);
			else {
				m.setCase(h, 6, y);
				y++;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_map, menu);
		return true;
	}
}
