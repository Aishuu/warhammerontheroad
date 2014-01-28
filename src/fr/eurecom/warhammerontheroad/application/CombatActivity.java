package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.R;
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
	

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combat);
		this.table = (LinearLayout) findViewById(R.id.tableLayout);
		this.contextualMenu = (RelativeLayout) findViewById(R.id.contextualMenu);
		Display display = getWindowManager().getDefaultDisplay();
		int total_width, total_height;
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			total_width = size.x;
			total_height = size.y;
		}
		else {
			total_width = display.getWidth();
			total_height = display.getHeight();
		}
		this.mService.getGame().getMap().draw(this.table, total_width, total_height, this);
	}
	
	public void drawMenu(float x, float y) {
		this.contextualMenu.setVisibility(View.VISIBLE);
	}

}
