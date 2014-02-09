package fr.eurecom.warhammerontheroad.application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.model.CombatAction;

public class CombatMenuItem {
	private int width, height;
	private int x_dest, y_dest;
	private int item_height, item_width, dist_items;
	private int act;
	private int pos, outOf;
	private String text;
	private int size;
	private long start_time;
	private long fade_away_time;
	private Typeface tf;
	private boolean hovered;
	private int overflow;

	private static final float RADIUS		= 0.5f;
	private static final int TOLERANCE_Y	= 15;

	public CombatMenuItem(int act, int width, int height, int pos, int outOf, Context context) {
		this.hovered = false;
		this.width = width;
		this.height = height;
		this.act = act;
		if(act < 0)
			this.text = "End Turn";
		else
			this.text = CombatAction.fromIndex(act).getLabel();
		this.pos = pos;
		this.outOf = outOf;
		this.fade_away_time = 0;
		this.tf = Typeface.createFromAsset(context.getAssets(), "WashingtonText.ttf");

		this.size = context.getResources().getDimensionPixelSize(R.dimen.fontSize);

		int nb_item = outOf/2;
		if(outOf % 2 == 1 && pos <= nb_item)
			nb_item++;
		Paint p = new Paint();
		p.setTypeface(tf);
		p.setColor(Color.argb(255, 0, 0, 0));
		p.setStyle(Style.FILL);
		p.setTextSize(this.size);
		Rect textBounds = new Rect();
		p.getTextBounds(text, 0, text.length(), textBounds);
		this.item_height = textBounds.bottom-textBounds.top;
		this.item_width = textBounds.right-textBounds.left;
		this.dist_items = (this.height - nb_item*item_height)/(nb_item+1);
		this.y_dest = dist_items + (pos%nb_item)*(dist_items+item_height);
		if(pos < nb_item)
			this.x_dest = (int)(this.width/2 + Math.sqrt((this.height*this.height*RADIUS*RADIUS)-(this.height/2-this.y_dest)*(this.height/2-this.y_dest)));
		else
			this.x_dest = (int)(this.width/2 - Math.sqrt((this.height*this.height*RADIUS*RADIUS)-(this.height/2-this.y_dest)*(this.height/2-this.y_dest))-item_width);
		this.start_time = SystemClock.elapsedRealtime();
		this.overflow = pos < nb_item ? this.x_dest+this.item_width-this.width : -this.x_dest;
	}

	public void updateSize(int width, int height) {
		int nb_item = outOf/2;
		if(outOf % 2 == 1 && pos <= nb_item)
			nb_item++;
		Paint p = new Paint();
		p.setTypeface(tf);
		p.setColor(Color.argb(255, 0, 0, 0));
		p.setStyle(Style.FILL);
		p.setTextSize(this.size);
		this.dist_items = (this.height - nb_item*item_height)/(nb_item+1);
		this.y_dest = dist_items + (pos%nb_item)*(dist_items+item_height);
		if(pos < nb_item)
			this.x_dest = (int)(this.width/2 + Math.sqrt((this.height*this.height*RADIUS*RADIUS)-(this.height/2-this.y_dest)*(this.height/2-this.y_dest)));
		else
			this.x_dest = (int)(this.width/2 - Math.sqrt((this.height*this.height*RADIUS*RADIUS)-(this.height/2-this.y_dest)*(this.height/2-this.y_dest))-item_width);
		this.overflow = pos < nb_item ? this.x_dest+this.item_width-this.width : -this.x_dest;
	}

	public int getOverflow() {
		return this.overflow;
	}

	public void shrink(int value) {
		int nb_item = outOf/2;
		if(outOf % 2 == 1 && pos <= nb_item)
			nb_item++;
		this.x_dest += pos < nb_item ? -value : value;
	}
	
	public int getXDest() {
		return this.x_dest;
	}

	public void doDraw(Canvas c) {
		long t = SystemClock.elapsedRealtime();
		if(this.fade_away_time != 0 && t-this.fade_away_time > CombatMenu.LENGTH_ANIM)
			return;
		if(t-this.start_time > CombatMenu.LENGTH_ANIM) {
			float ratio;
			if(this.fade_away_time != 0)
				ratio = ((float) t-this.fade_away_time)/CombatMenu.LENGTH_ANIM;
			else
				ratio = 0;
			Paint p = new Paint();
			p.setTypeface(tf);
			if(this.hovered)
				p.setColor(Color.argb((int)(255*(1-ratio)), 139, 0, 0));
			else
				p.setColor(Color.argb((int)(255*(1-ratio)), 255, 255, 255));
			p.setStyle(Style.FILL);
			p.setTextSize(this.size);
			c.drawText(text, x_dest, y_dest, p);
		} else {
			float ratio = ((float) t-this.start_time)/CombatMenu.LENGTH_ANIM;
			Paint p = new Paint();
			p.setTypeface(tf);
			if(this.hovered)
				p.setColor(Color.argb((int)(ratio*255), 139, 0, 0));
			else
				p.setColor(Color.argb((int)(ratio*255), 255, 255, 255));
			p.setStyle(Style.FILL);
			p.setTextSize(this.size*ratio);
			c.drawText(text, this.width/2+(this.x_dest-this.width/2)*ratio, this.height/2+(this.y_dest-this.height/2)*ratio, p);
		}
	}

	public int getAct() {
		return this.act;
	}

	public boolean isInBound(int x, int y) {
		int toler = this.dist_items/4 < TOLERANCE_Y ? TOLERANCE_Y : this.dist_items/4;
		return (x > this.x_dest && x < this.x_dest+this.item_width && y > this.y_dest-toler && y < this.y_dest+this.item_height+toler);
	}

	public void fadeAway() {
		long t = SystemClock.elapsedRealtime();
		if(t-this.start_time > CombatMenu.LENGTH_ANIM)
			this.fade_away_time = t;
		else
			this.fade_away_time = 2*t-CombatMenu.LENGTH_ANIM-this.start_time;
	}

	public void reAppear() {
		this.fade_away_time = 0;
		this.start_time = SystemClock.elapsedRealtime();
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}
}
