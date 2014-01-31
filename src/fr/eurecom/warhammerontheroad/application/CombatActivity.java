package fr.eurecom.warhammerontheroad.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.Game;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CombatActivity extends WotrActivity {
	private LinearLayout table;
	private RelativeLayout contextualMenu;


	@SuppressWarnings({"deprecation", "rawtypes"})
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(this.mService.getGame().getState() != Game.STATE_GAME_WAIT_TURN && this.mService.getGame().getState() != Game.STATE_GAME_TURN)
			this.finish();
		setContentView(R.layout.activity_combat);
		this.table = (LinearLayout) findViewById(R.id.tableLayout);
		this.contextualMenu = (RelativeLayout) findViewById(R.id.contextualMenu);
		Display display = getWindowManager().getDefaultDisplay();
		int total_width, total_height;
		try {
			Point size = new Point();
			Class pointClass = Class.forName("android.graphics.Point");
			Method newGetSize = Display.class.getMethod("getSize", new Class[]{ pointClass });

			newGetSize.invoke(display, size);
			total_width = size.x;
			total_height = size.y;
		} catch(NoSuchMethodException ex) {
			total_width = display.getWidth();
			total_height = display.getHeight();
		} catch(ClassNotFoundException ex) {
			total_width = display.getWidth();
			total_height = display.getHeight();
		} catch(InvocationTargetException ex) {
			total_width = display.getWidth();
			total_height = display.getHeight();
		} catch(IllegalAccessException ex) {
			total_width = display.getWidth();
			total_height = display.getHeight();
		}
		this.mService.getGame().getMap().draw(this.table, total_width, total_height, this);
	}

	public void drawMenu(float x, float y) {
		this.contextualMenu.setVisibility(View.VISIBLE);
	}

}
