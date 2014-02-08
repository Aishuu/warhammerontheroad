package fr.eurecom.warhammerontheroad.application;

import android.os.Bundle;
import fr.eurecom.warhammerontheroad.R;

public class CombatActivity extends WotrActivity {
	private CombatView combatView;
	private CombatView.CombatThread combatThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_combat);
		this.combatView = (CombatView) findViewById(R.id.combat);
		this.combatThread = this.combatView.getThread();
		this.mService.getGame().registerCombatThread(this.combatThread);
		this.combatThread.setGame(this.mService.getGame());
	}

	@Override
	public void onDestroy() {
		this.mService.getGame().unRegisterCombatThread();
		super.onDestroy();
	}
}
