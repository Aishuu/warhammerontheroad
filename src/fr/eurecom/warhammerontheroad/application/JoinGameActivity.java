package fr.eurecom.warhammerontheroad.application;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Player;

public class JoinGameActivity extends WotrActivity implements GameServiceListener {
	private WotrEditText nameEdit;
	private WotrEditText idEdit;
	private WotrButton enterGameButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);
		this.mService.getGame().addGameServiceListener(this);
		this.nameEdit = (WotrEditText) findViewById(R.id.join_game_name);
		this.idEdit = (WotrEditText) findViewById(R.id.join_game_number);
		this.enterGameButton = (WotrButton) findViewById(R.id.btnEnterGame);
		this.nameEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s){
				textChanged();
				}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		this.idEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s){
				textChanged();
				}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		this.enterGameButton.setEnabled(false);
	}

	private void textChanged() {
		String s1 = this.nameEdit.getText().toString();
		String s2 = this.idEdit.getText().toString();
		if(s1.length() == 0 || s2.length() == 0)
			this.enterGameButton.setEnabled(false);
		else  {
			try {
				Integer.parseInt(s2);
				this.enterGameButton.setEnabled(true);
			} catch(NumberFormatException e) {
				this.enterGameButton.setEnabled(false);
			}
		}
	}

	@Override
	public void onBackPressed() {
		this.mService.reinit();
		this.finish();
	}

	@Override
	public void onDestroy() {
		this.mService.getGame().removeGameServiceListener(this);
		super.onDestroy();
	}

	public void enterGame(View view) {
		if(this.idEdit.getText().length() == 0 || this.nameEdit.getText().length() == 0)
			return;
		try {
			int id = Integer.parseInt(this.idEdit.getText().toString());
			this.mService.getGame().setMe(new Player(this.mService.getContext(), this.nameEdit.getText().toString(), this.mService.getGame().getHeros()));
			this.mService.getNetworkParser().bind(id);
		} catch (NumberFormatException e) {
			Log.e("JoinGameActivity", "This is not a number !");
			this.idEdit.setText("");
		}
	}

	@Override
	public void onStateChanged(Game game, int exState) {
		int newState = game.getState();
		if(newState == Game.STATE_GAME_CREATED) {
			Intent intent = new Intent(this, PlayerMenuActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	@Override
	public void prepareFight() {
	}
}
