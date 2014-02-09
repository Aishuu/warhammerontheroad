package fr.eurecom.warhammerontheroad.application;

import android.os.Bundle;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;

public class CombatActivity extends WotrActivity implements GameServiceListener {
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
		this.mService.getGame().addGameServiceListener(this);
		
		if(savedInstanceState != null)
			this.combatThread.restoreState(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		this.combatThread.saveState(outState);
	}
	
	@Override
	public void onPause() {
		this.combatThread.pause();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		this.combatThread.resumePause();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		this.mService.getGame().unRegisterCombatThread();
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	@Override
	public void onStateChanged(Game game, int exState) {
		if(game.getState() == Game.STATE_GAME_LAUNCHED)
			this.finish();
	}

	@Override
	public void prepareFight() {
	}
}
