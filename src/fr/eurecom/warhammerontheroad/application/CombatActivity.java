package fr.eurecom.warhammerontheroad.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
	
	public void displayChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
		startActivity(intent);
	}
	
	public void displayChara(View view) {
		Intent intent;
		if(this.mService.getGame().isGM())
			intent = new Intent(this, SeeCharaDataActivity.class);
		else {
			intent = new Intent(this, SeeStatsActivity.class);
			intent.putExtra("chara id", this.mService.getGame().getMe().getName());
		}
		startActivity(intent);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		this.combatThread.saveState(outState);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		this.combatView.initThread(this);
		this.combatThread = this.combatView.getThread();
		this.mService.getGame().registerCombatThread(this.combatThread);
		this.combatThread.setGame(this.mService.getGame());
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
	

	@Override
	public void onBackPressed() {
		if(this.mService.getGame().isGM()) {
			this.combatView.stop();
		} else {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.quit)
		.setMessage(R.string.disconnected)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				CombatActivity.this.combatView.stop();
				CombatActivity.this.mService.reinit();
				CombatActivity.this.finish();    
			}

		})
		.setNegativeButton(R.string.no, null)
		.show();
	}
	}
}
