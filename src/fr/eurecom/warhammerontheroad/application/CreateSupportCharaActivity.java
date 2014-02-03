package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Race;
import android.os.Bundle;
import android.view.Menu;

public class CreateSupportCharaActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_support_chara);
		Hero h = new Hero(this.mService.getContext(), Race.GOBLIN);
		this.mService.getGame().addHero(h);
		this.mService.getNetworkParser().createHero(h);
		Hero h2 = new Hero(this.mService.getContext(), Race.ORC);
		this.mService.getGame().addHero(h2);
		this.mService.getNetworkParser().createHero(h2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_support_chara, menu);
		return true;
	}

}
