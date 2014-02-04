package fr.eurecom.warhammerontheroad.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;

public class PlayerMenuActivity extends WotrActivity implements GameServiceListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_menu);

		this.mService.getGame().addGameServiceListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		int state = this.mService.getGame().getState();
		if(state == Game.STATE_GAME_PERSO_CREATED)
			((WotrButton) findViewById(R.id.btnCreateChara)).setText(R.string.modify_chara);
		else if(state == Game.STATE_GAME_LAUNCHED)
			((WotrButton) findViewById(R.id.btnCreateChara)).setText(R.string.see_chara_stats);
	}

	public void accessChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
		startActivity(intent);
	}

	public void accessStory(View view) {
		Intent intent = new Intent(this, WriteAndReadStoryActivity.class);
		startActivity(intent);
	}

	public void createCharaDetails(View view) {
		Intent intent;
		if(this.mService.getGame().getState() == Game.STATE_GAME_LAUNCHED){
			intent = new Intent(this, SeeStatsActivity.class);
			intent.putExtra("chara id", this.mService.getGame().getMe().getName());
		}
		else
			intent = new Intent(this, CharaCreationDetailsActivity.class);
		startActivity(intent);
	}

	@Override
	public void onStateChanged(Game game, int exState) {
		int newState = game.getState();
		if(exState == Game.STATE_GAME_LAUNCHED && (newState == Game.STATE_GAME_TURN || newState == Game.STATE_GAME_WAIT_TURN)) {
			Intent intent = new Intent(this, CombatActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void prepareFight() {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(PlayerMenuActivity.this.mService.getContext(), R.string.prepare_fight, Toast.LENGTH_SHORT);
				toast.show();
			}});
	}

	@Override
	protected void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.quit)
		.setMessage(R.string.disconnected)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				PlayerMenuActivity.this.mService.reinit();
				PlayerMenuActivity.this.finish();    
			}

		})
		.setNegativeButton(R.string.no, null)
		.show();
	}
}
