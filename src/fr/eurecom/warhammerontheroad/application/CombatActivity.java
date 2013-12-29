package fr.eurecom.warhammerontheroad.application;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

public class CombatActivity extends Activity {
	private LinearLayout table;
	private RelativeLayout contextualMenu;
	private int cell_size;
	private int maxx, maxy;
	

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.maxx = 10;
		this.maxy = 5;
		setContentView(R.layout.activity_combat);
		this.table = (LinearLayout) findViewById(R.id.tableLayout);
		this.contextualMenu = (RelativeLayout) findViewById(R.id.contextualMenu);
		Combat c = new Combat(maxx, maxy);
		c.setResourceINCell(3, 2, R.drawable.mage);
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
		this.cell_size = total_width/maxx < total_height/maxy ? (int) (total_width/maxx) : (int) (total_height/maxy);
		Log.d("test", "Size of the layout : "+this.maxx*this.cell_size+"x"+this.maxy*this.cell_size);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(this.maxx*this.cell_size,this.maxy*this.cell_size);
		//lp.setMargins(0, 0, 0, 0);
		//this.table.setLayoutParams(lp);
		for(int i=0; i<maxy; i++) {
			LinearLayout line = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(this.maxx*this.cell_size, this.cell_size);
			params.setMargins(0, 0, 0, 0);
			line.setLayoutParams(params);
			line.setOrientation(LinearLayout.HORIZONTAL);

			for(int j=0; j<maxx; j++) {
				ImageView im = new ImageView(this);
				params = new LinearLayout.LayoutParams(this.cell_size, this.cell_size);
				params.setMargins(0, 0, 0, 0);
				im.setLayoutParams(params);
				if(c.isSomeoneHere(j, i)) {
					im.setImageResource(c.getResourceInCell(j, i));

					im.setOnLongClickListener(new View.OnLongClickListener() {
						public boolean onLongClick(View v) {
							// Starts the drag

							drawMenu(v.getX(), v.getY());
							return true;

						}});
				}
				line.addView(im);
			}
			this.table.addView(line);
		}
	}
	
	private void drawMenu(float x, float y) {
		this.contextualMenu.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.combat, menu);
		return true;
	}

}
