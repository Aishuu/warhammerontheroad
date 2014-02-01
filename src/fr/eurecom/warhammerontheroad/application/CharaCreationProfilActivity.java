package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Player;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class CharaCreationProfilActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_creation_profil);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.chara_creation_profil, menu);
		return true;
	}

	public void terminate(View view) {
		Player p = this.mService.getGame().getMe();

		
		//TODO: for testing purpose
		p.AddJob(1);
		
		this.mService.getNetworkParser().createPlayer(p);
		this.mService.getGame().validateCharaCreation();
		this.finish();
	}

    @Override
    public void onBackPressed() {
    	
    }
}
