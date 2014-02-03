package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import android.os.Bundle;
import android.view.Menu;

public class SeeCharaDataActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_chara_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_chara_data, menu);
		return true;
	}

}
