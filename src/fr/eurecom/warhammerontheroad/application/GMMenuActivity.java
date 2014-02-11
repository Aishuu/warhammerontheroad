package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class GMMenuActivity extends WotrActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gm_menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		int state = this.mService.getGame().getState();
		if(state != Game.STATE_GAME_CREATED) {
			WotrButton gmAccessPlay = ((WotrButton) findViewById(R.id.btnGMAccessPlay));
			gmAccessPlay.setText(R.string.access_fight);
			gmAccessPlay.setEnabled(this.mService.getGame().getMap() != null);

		}
		if(state == Game.STATE_GAME_WAIT_ACTION || state == Game.STATE_GAME_CONFIRM_ACTION)
			((WotrButton) findViewById(R.id.btnCrtMap)).setEnabled(false);

	}

	public void accessChat(View view) {
		Intent intent = new Intent(this, ChatRoomActivity.class);
		startActivity(intent);
	}

	public void createMap(View view) {
		Intent intent = new Intent(this, CreateMapActivity.class);
		startActivity(intent);
	}

	public void writeStory(View view) {
		Intent intent = new Intent(this, WriteAndReadStoryActivity.class);
		startActivity(intent);
	}

	public void createSupportChara(View view) {
		Intent intent = new Intent(this, CreateSupportCharaActivity.class);
		startActivity(intent);
	}

	public void seePlayersData(View view) {
		Intent intent = new Intent(this, SeeCharaDataActivity.class);
		startActivity(intent);
	}

	public void accessPlayOrFight(View view) {
		if(this.mService.getGame().getState() == Game.STATE_GAME_CREATED) {
			this.mService.getGame().gameStarted();
			this.mService.getNetworkParser().startGame();
			WotrButton gmAccessPlay = ((WotrButton) findViewById(R.id.btnGMAccessPlay));
			gmAccessPlay.setText(R.string.access_fight);
			gmAccessPlay.setEnabled(this.mService.getGame().getMap() != null);
		}
		else {
			this.mService.getGame().sendNotifPrepareFight();
			Toast toast = Toast.makeText(this, "Prepare to fight !", Toast.LENGTH_SHORT);
			toast.show();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				public void run() {
					GMMenuActivity.this.startFight();
				}

			}, 5000);
		}
	}

	private void startFight() {
		this.mService.getGame().sendNotifStartFight();
		Intent intent = new Intent(this, CombatActivity.class);
		startActivity(intent);
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
				GMMenuActivity.this.mService.reinit();
				GMMenuActivity.this.finish();    
			}

		})
		.setNegativeButton(R.string.no, null)
		.show();
	}
}
