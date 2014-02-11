package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Race;

public class CreateSupportCharaActivity extends WotrActivity implements OnItemSelectedListener{
	private Hero h;
	ArrayList<Hero> enemies=new ArrayList<Hero>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_support_chara);
		Spinner spinRace=(Spinner) findViewById(R.id.spinCreateSupportRace);
		ArrayAdapter<CharSequence> adapterRace = ArrayAdapter.createFromResource(this, R.array.ennemy_array, android.R.layout.simple_spinner_item);
		adapterRace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinRace.setAdapter(adapterRace);
		spinRace.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_support_chara, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id){
		Race r=Race.fromIndex(pos+4);
		this.createHero(r);
	}

	private void createHero(Race r) {
		h=new Hero(this.mService.getContext(),r);
		WebView s, p, w, a;
		s=(WebView) findViewById(R.id.createSupportStats);
		p=(WebView) findViewById(R.id.createSupportProfil);
		w=(WebView) findViewById(R.id.createSupportWeapons);
		a=(WebView) findViewById(R.id.createSupportArmor);
		String header, hs,hp,hw,ha;
		header="<html><header><style>@font-face{font-family:'WashingtonText'; src:url('file:///android_asset/WashingtonText.ttf')} " +
				"body{color:#8B0000; background-color:rgba(#,#,#,0); font-family:WashingtonText } " +
				"td {padding:5px;} table{width:100%;text-align:center;} </style></header>";

		//Stats
		int m=h.getStats().getStats(12);
		hs=header;
		hs +="<body><table><tr><td>Mouvement</td><td>Charge</td><td>Running</td><td>Injuries</td></tr>";
		hs+="<tr><td>"+Integer.toString(m)+"</td><td>"+Integer.toString(m*2)+"</td><td>"+Integer.toString(m*3)+"</td><td>"+Integer.toString(h.getB())+"/"+Integer.toString(h.getStats().getStats(9))+"</td></tr>";
		hs+="</table></body></html>";
		s.loadDataWithBaseURL(null, hs, "text/html","utf-8", null);
		s.setBackgroundColor(0x00000000);
		s.getSettings().setDefaultFontSize(25);

		//Profil
		ArrayList<String> profil=h.getStats().getFullStats();

		hp=header;
		hp +="<body><table><tr><td>CC</td><td>CT</td><td>F</td><td>E</td><td>Ag</td><td>Int</td><td>FM</td><td>Soc</td></tr><tr>";
		int i;
		for(i=0; i<profil.size();i++){
			hp+="<td>"+profil.get(i)+"</td>";
			if(i==7)
				hp +="</tr><tr><td>A</td><td>B</td><td>BF</td><td>BE</td><td>M</td><td>Mag</td><td>PF</td><td>PD</td></tr><tr>";
		}
		hp+="</tr></table></body></html>";
		p.loadDataWithBaseURL(null, hp, "text/html","utf-8", null);
		p.setBackgroundColor(0x00000000);
		p.getSettings().setDefaultFontSize(25);

		//Weapon
		ArrayList<String[]> weapons=h.getWeapon();

		hw=header;
		hw +="<body><table><tr><td>Name</td><td>Damage</td><td>Range</td><td>Reload</td><td>Attributes</td></tr>";
		for(String [] weapon: weapons)
			hw+="<tr><td>"+weapon[0]+"</td><td>"+weapon[1]+"</td><td>"+weapon[2]+"</td><td>"+weapon[3]+"</td><td>"+weapon[4]+"</td></tr>";
		hw+="</table></body></html>";
		w.loadDataWithBaseURL(null, hw, "text/html","utf-8", null);
		w.setBackgroundColor(0x00000000);
		w.getSettings().setDefaultFontSize(25);

		//Armor
		ArrayList<String[]> armor=h.getArmor();

		ha=header;
		ha+="<body><table><tr><td>Type</td><td>Protected parts</td><td>Armor points</td></tr>";
		for(String [] ar: armor)
			ha+="<tr><td>"+ar[0]+"</td><td>"+ar[1]+"</td><td>"+ar[2]+"</td></tr>";
		ha+="</table></body></html>";
		a.loadDataWithBaseURL(null, ha, "text/html","utf-8", null);
		a.setBackgroundColor(0x00000000);
		a.getSettings().setDefaultFontSize(25);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void saveChara(View view){
		this.mService.getGame().addHero(h);
		this.mService.getNetworkParser().createHero(h);
		showEnemyImage();
		this.createHero(h.getRace());
	}

	private void showEnemyImage(){
		ArrayList<Hero> heroes=this.mService.getGame().getHeros();
		GridView grid=(GridView) findViewById(R.id.gridCreateSupport);
		enemies.clear();
		for(Hero hero:heroes){
			if(!(hero instanceof Player))
				enemies.add(hero);
		}
		grid.setAdapter(new ImageAdapter(this, enemies));

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Hero e=enemies.get(position);
				Intent intent=new Intent(CreateSupportCharaActivity.this,SeeEnemyStatsActivity.class);
				intent.putExtra("chara id", e.getId());
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume(){
		super.onResume();
		showEnemyImage();
	}

}
