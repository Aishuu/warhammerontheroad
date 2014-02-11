package fr.eurecom.warhammerontheroad.model;

import android.os.SystemClock;
import fr.eurecom.warhammerontheroad.application.CombatMenu;
import fr.eurecom.warhammerontheroad.network.Describable;

public abstract class Case implements Describable {
	protected int x,y;
	protected int flag;
	private boolean highlighted, fadingAway;
	private long start_highlight_time;

	protected Case() {
		this.x = -1;
		this.y = -1;
		this.flag = 0;
		this.highlighted = false;
		this.fadingAway = false;
		this.start_highlight_time = 0;
	}

	protected Case(int x, int y) {
		this.x = x;
		this.y = y;
		this.highlighted = false;
		this.fadingAway = false;
		this.start_highlight_time = 0;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
	
	public int getFlag(){
		return this.flag;
	}
	
	public void setFlag(int flag){
		this.flag = flag;
	}
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isHighlighted() {
		return this.highlighted;
	}

	public boolean isFadingAway() {
		return this.fadingAway;
	}

	public void setHighlighted(boolean highlighted) {
		if(highlighted == this.highlighted)
			return;
		this.highlighted = highlighted;
		long t = SystemClock.elapsedRealtime();
		if(t-this.start_highlight_time < CombatMenu.LENGTH_HIGHLIGHT_ANIM)
			this.start_highlight_time = 2*t - CombatMenu.LENGTH_HIGHLIGHT_ANIM - this.start_highlight_time;
		else
			this.start_highlight_time = t;
		this.fadingAway = !highlighted;
	}

	public long getStartHighlightTime() {
		if(this.fadingAway) {
			long t = SystemClock.elapsedRealtime();
			if(t-this.start_highlight_time > CombatMenu.LENGTH_HIGHLIGHT_ANIM)
				this.fadingAway = false;
		}
		return this.start_highlight_time;
	}
}
