package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Race;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CharaCreationDetailsActivity extends WotrActivity implements OnItemSelectedListener {
	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_creation_details);
		Spinner spinnerRace = (Spinner) findViewById(R.id.spinRace);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.race_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRace.setAdapter(adapter);
		spinnerRace.setOnItemSelectedListener(this);
		player=this.mService.getGame().getMe();
		TextView birthPlace, siblings,name;
		birthPlace=(TextView) findViewById(R.id.txtCharaCreaDetailsBirthPlace);
		siblings=(TextView) findViewById(R.id.txtCharaCreaDetailsSiblingsNumber);
		name=(TextView) findViewById(R.id.chosedName);
		name.setText(player.getName());
		birthPlace.setText(player.getBirthPlace().getName());
		siblings.setText(Integer.toString(player.getSiblings())+" brothers and sisters");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.chara_creation_profil, menu);
		return true;
	}
	
	public void next(View view) {
		// TODO save info model
		EditText eye, hair;
		eye=(EditText) findViewById(R.id.editCharaCreaDetailsEyeColor);
		hair=(EditText) findViewById(R.id.editCharaCreaDetailsHairColor);
		player.setEyeColor(eye.getText().toString());
		player.setHairColor(hair.getText().toString());
		player.init(player.getRace());
		Intent intent = new Intent(this, PlayerMenuActivity.class);
	    startActivity(intent);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        player.setRace(Race.fromIndex(pos));
        TextView age, size, weight;
        age=(TextView) findViewById(R.id.txtCharaCreaDetailsAge);
        size=(TextView) findViewById(R.id.txtCharaCreaDetailsSize);
        weight=(TextView) findViewById(R.id.txtCharaCreaDetailsWeight);
        age.setText(Integer.toString(player.getAge()));
        weight.setText(Integer.toString(player.getWeight())+" kg");
        int s=player.getSize();
        if(s==100){
        	size.setText("2 m");
        }else{
        	size.setText("1 m "+Integer.toString(player.getSize())+" cm");
        }
    }

	@Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
