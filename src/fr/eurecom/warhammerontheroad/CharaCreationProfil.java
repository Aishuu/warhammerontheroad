package fr.eurecom.warhammerontheroad;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CharaCreationProfil extends Activity {

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

}
