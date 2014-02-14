package fr.eurecom.warhammerontheroad.application;

import android.os.Bundle;
import android.view.Menu;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Map;
import fr.eurecom.warhammerontheroad.model.Obstacle;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Vide;

public class CreateMapActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_map);
		
		// TODO: move that
		this.mService.getNetworkParser().sendFile("grass.png");
		Map m = new Map(this.mService.getContext(), 10, 5, "grass.png");
		this.mService.getGame().setMap(m);
		for(int j=0; j<10; j++)
			m.setCase(new Obstacle(j, 4), j, 4);
		m.setCase(new Obstacle(7, 1), 7, 1);
		for(Hero h: this.mService.getGame().getHeros()) {
			if(h instanceof Player) {
				int x, y;
				do {
					x = (int)(Math.random()*3);
					y = (int)(Math.random()*4);
				} while(!(m.getCase(x, y) instanceof Vide));
				m.setCase(h, x, y);
			}
			else {
				int x, y;
				do {
					x = 7+(int)(Math.random()*3);
					y = (int)(Math.random()*4);
				} while(!(m.getCase(x, y) instanceof Vide));
				m.setCase(h, x, y);
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
