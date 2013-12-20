package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CharaCreationDetailsActivity extends WotrActivity implements OnItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_creation_details);
		Spinner spinner = (Spinner) findViewById(R.id.spinRace);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.race_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.chara_creation_profil, menu);
		return true;
	}
	
	public void next(View view) {
		Intent intent = new Intent(this, CharaCreationProfilActivity.class);
	    startActivity(intent);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        this.mService.getPlayer().createHero(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
