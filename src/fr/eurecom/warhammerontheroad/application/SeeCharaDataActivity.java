package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Player;

public class SeeCharaDataActivity extends WotrActivity {
	private GridView grid;
	private ArrayList<Hero> heroes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_chara_data);
		grid=(GridView) findViewById(R.id.gridCharaData);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_chara_data, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		setGrid();
	}
	
	public void setGrid(){
		heroes=this.mService.getGame().getHeros();
		
		grid.setAdapter(new ImageAdapter(this, heroes));
		
		grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Hero h=heroes.get(position);
	            Intent intent;
	            if(h instanceof Player){
	            	intent=new Intent(SeeCharaDataActivity.this,SeeStatsActivity.class);
	            	intent.putExtra("chara id", ((Player) h).getName());
	            }else{
	            	intent=new Intent(SeeCharaDataActivity.this,SeeEnemyStatsActivity.class);
	            	intent.putExtra("chara id", h.getId());
	            }
	            startActivity(intent);
	        }
	    });

	}

}
