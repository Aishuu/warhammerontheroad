package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Color;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Gender;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Race;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CharaCreationDetailsActivity extends WotrActivity implements OnItemSelectedListener {
	private Player player;
	private Spinner spinnerCarrier;
	private Spinner spinnerGender;
	private EditText eye, hair;
	private TextView age, size, weight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_creation_details);
		Spinner spinnerRace = (Spinner) findViewById(R.id.spinRace);
		spinnerCarrier= (Spinner) findViewById(R.id.spinChooseCarrier);
		spinnerGender= (Spinner) findViewById(R.id.spinSexe);
		ArrayAdapter<CharSequence> adapterRace = ArrayAdapter.createFromResource(this, R.array.race_array, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterCarrier = ArrayAdapter.createFromResource(this, R.array.carrier_array, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this, R.array.sexe_array, android.R.layout.simple_spinner_item);
		adapterRace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterCarrier.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRace.setAdapter(adapterRace);
		spinnerCarrier.setAdapter(adapterCarrier);
		spinnerGender.setAdapter(adapterGender);
		spinnerRace.setOnItemSelectedListener(this);

		player=this.mService.getGame().getMe();

		TextView birthPlace, siblings,name;
		birthPlace=(TextView) findViewById(R.id.txtCharaCreaDetailsBirthPlace);
		siblings=(TextView) findViewById(R.id.txtCharaCreaDetailsSiblingsNumber);
		name=(TextView) findViewById(R.id.chosedName);
		eye=(EditText) findViewById(R.id.editCharaCreaDetailsEyeColor);
		hair=(EditText) findViewById(R.id.editCharaCreaDetailsHairColor);
		age=(TextView) findViewById(R.id.txtCharaCreaDetailsAge);
		size=(TextView) findViewById(R.id.txtCharaCreaDetailsSize);
		weight=(TextView) findViewById(R.id.txtCharaCreaDetailsWeight);

		name.setText(player.getName());
		birthPlace.setText(player.getBirthPlace().getName());
		siblings.setText(Integer.toString(player.getSiblings()));

		if(savedInstanceState!=null) {
			spinnerRace.setSelection(player.getRace().getIndex());
			spinnerCarrier.setSelection(savedInstanceState.getInt("Carrier"));
			spinnerGender.setSelection(savedInstanceState.getInt("Gender"));
			age.setText(Integer.toString(player.getAge()));
			weight.setText(Integer.toString(player.getWeight())+" kg");
			size.setText("1,"+Integer.toString(player.getSize())+" m");
			eye.setText(savedInstanceState.getString("eye"));
			hair.setText(savedInstanceState.getString("hair"));

		}else if(this.mService.getGame().getState()==Game.STATE_GAME_PERSO_CREATED){
			spinnerRace.setSelection(player.getRace().getIndex());
			spinnerCarrier.setSelection(player.getJob().getIndex());
			spinnerGender.setSelection(player.getGender().getIndex());
			age.setText(Integer.toString(player.getAge()));
			weight.setText(Integer.toString(player.getWeight())+" kg");
			size.setText("1, "+Integer.toString(player.getSize())+" m");
			eye.setText(player.getEyeColor());
			hair.setText(player.getHairColor());
		} 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.chara_creation_profil, menu);
		return true;
	}

	public void next(View view) {
		// TODO save info model
		if(eye.getText().length()==0){
			player.setEyeColor("Blue");
			Toast.makeText(getApplicationContext(), "Eye color set to blue", Toast.LENGTH_SHORT).show();
		}else
			player.setEyeColor(eye.getText().toString());
		if(hair.getText().length()==0){
			player.setHairColor("Black");
			Toast.makeText(getApplicationContext(), "Hair color set to black", Toast.LENGTH_SHORT).show();
		}else
			player.setHairColor(hair.getText().toString());
		player.init();

		//TODO: for testing purpose
		player.AddJob(spinnerCarrier.getSelectedItemPosition());
		player.setColor(Color.BLUE);
		player.setGender(Gender.fromIndex(spinnerGender.getSelectedItemPosition()));

		this.mService.getNetworkParser().createPlayer(player);
		this.mService.getGame().validateCharaCreation();

		this.finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		player.setRace(Race.fromIndex(pos));
		age.setText(Integer.toString(player.getAge()));
		weight.setText(Integer.toString(player.getWeight())+" kg");
		int s=player.getSize();
		if(s==100){
			size.setText("2 m");
		}else{
			size.setText("1,"+Integer.toString(player.getSize())+" m");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Carrier", spinnerCarrier.getSelectedItemPosition());
		savedInstanceState.putInt("Gender", spinnerGender.getSelectedItemPosition());
		savedInstanceState.putString("eye",eye.getText().toString());
		savedInstanceState.putString("hair",hair.getText().toString());
	}
}
