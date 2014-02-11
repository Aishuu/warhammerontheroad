package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import fr.eurecom.warhammerontheroad.model.Case;
import fr.eurecom.warhammerontheroad.model.CombatAction;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Hero;
import fr.eurecom.warhammerontheroad.model.Map;
import fr.eurecom.warhammerontheroad.model.Player;
import fr.eurecom.warhammerontheroad.model.Vide;

public class CombatMenu {
	private Hero h;
	private int cell_size;
	private int width;
	private ArrayList<CombatMenuItem> items;
	private CombatMenuItem itemSelected, itemHovered;
	private Game game;
	private long start_time;
	private Case chargeCase;
	private ArrayList<Case> where;

	public static final long LENGTH_ANIM		= 500;
	public static final long LENGTH_HIGHLIGHT_ANIM = 300;
	private static final int GREY_TRANSPARENCY	= 150;

	public CombatMenu(Game game, Hero h, int width, int height, Context context) {
		this.width = width;
		this.game = game;
		this.cell_size = game.getMap().getCellSize();
		this.h = h;
		this.itemSelected = null;
		this.itemHovered = null;
		this.chargeCase = null;
		this.start_time = SystemClock.elapsedRealtime();
		this.items = new ArrayList<CombatMenuItem>();
		this.where = null;

		boolean canDo[] = new boolean[8];
		canDo[0] = h.peutViser(game);
		canDo[1] = h.whereMovement(game) != null;
		canDo[2] = h.whereAttaqueStandard(game) != null;
		canDo[3] = h.whereCharge(game) != null;
		canDo[4] = h.peutDegainer(game);
		canDo[5] = h.peutRecharger(game);
		canDo[6] = h.whereAttaqueRapide(game) != null;
		canDo[7] = h.whereDesengager(game) != null;

		int l = 0;
		for(int i=0; i<8; i++) {
			if(canDo[i]) l++;
		}
		Log.d("WOTR", "nb of items : "+l);
		if(l == 0)
			this.game.endTurn();
		else {
			l++;
			int j=0;
			for(int i=0; i<8; i++) {
				if(canDo[i]) {
					this.items.add(new CombatMenuItem(i, width, height, j, l, context));
					j++;
				}
			}
			this.items.add(new CombatMenuItem(-1, width, height, j, l, context));
		}
		this.shrink();
	}

	public void updateSize(int width, int height) {
		this.cell_size = game.getMap().getCellSize();
		this.width = width;

		for(CombatMenuItem item: this.items)
			item.updateSize(width, height);
		this.shrink();
	}

	public void saveState(Bundle outState) {
		outState.putString("menuHero", this.h.representInString());
		if(this.itemSelected == null) {
			if(this.itemHovered != null) {
				for(Case c: where)
					c.setHighlighted(false);
				this.where = null;
				this.itemHovered = null;
			}
		}
		else {
			outState.putInt("menuItemSelected", this.itemSelected.getAct());
			int[] xtab = new int[this.where.size()];
			int[] ytab = new int[this.where.size()];
			int i=0;
			for(Case c: this.where) {
				xtab[i] = c.getX();
				ytab[i] = c.getY();
				i++;
			}
			outState.putIntArray("menuWhereX", xtab);
			outState.putIntArray("menuWhereY", ytab);
		}
	}

	public void restoreState(Bundle savedState) {
		int act = savedState.getInt("menuItemSelected");
		if(act < 0)
			this.reDisplayItems();
		else {
			for(CombatMenuItem cmi: this.items)
				if(cmi.getAct() == act) {
					this.itemSelected = cmi;
					int[] xtab = savedState.getIntArray("menuWhereX");
					int[] ytab = savedState.getIntArray("menuWhereY");
					this.where = new ArrayList<Case>();
					for(int i=0; i<xtab.length; i++) {
						Case c = this.game.getMap().getCase(xtab[i], ytab[i]);
						this.where.add(c);
						c.setHighlighted(true);
					}
					break;
				}
		}
	}

	public void doDraw(Canvas c) {
		// compute anim start menu
		long t = SystemClock.elapsedRealtime();
		float ratio;
		if(t-this.start_time > LENGTH_ANIM)
			ratio = 1;
		else
			ratio = ((float) t-this.start_time)/LENGTH_ANIM;
		Paint grey = new Paint();
		grey.setColor(Color.argb((int)(GREY_TRANSPARENCY*ratio), 0, 0, 0));
		grey.setStyle(Style.FILL);

		Map m = this.game.getMap();
		int maxX = m.getMaxX();
		int maxY = m.getMaxY();
		for(int x=0; x<maxX; x++)
			for(int y=0; y<maxY; y++) {
				Case cas = m.getCase(x, y);
				if(cas == this.h)
					continue;
				if(cas.isHighlighted()) {
					float ratio_highlight;
					long s = cas.getStartHighlightTime();
					if(t-s > LENGTH_HIGHLIGHT_ANIM)
						ratio_highlight = 1;
					else
						ratio_highlight = ((float) t-s)/LENGTH_HIGHLIGHT_ANIM;
					Paint yellow = new Paint();
					yellow.setColor(Color.argb(GREY_TRANSPARENCY, (int)(255*ratio_highlight), (int)(255*ratio_highlight), 0));
					yellow.setStyle(Style.FILL);

					c.drawRect(x*cell_size, y*cell_size, (x+1)*cell_size, (y+1)*cell_size, yellow);
					continue;
				}
				if(cas.isFadingAway()) {
					float ratio_highlight;
					long s = cas.getStartHighlightTime();
					if(t-s > LENGTH_HIGHLIGHT_ANIM)
						ratio_highlight = 1;
					else
						ratio_highlight = ((float) t-s)/LENGTH_HIGHLIGHT_ANIM;
					Paint yellow = new Paint();
					yellow.setColor(Color.argb(GREY_TRANSPARENCY, (int)(255*(1-ratio_highlight)), (int)(255*(1-ratio_highlight)), 0));
					yellow.setStyle(Style.FILL);

					c.drawRect(x*cell_size, y*cell_size, (x+1)*cell_size, (y+1)*cell_size, yellow);
					continue;
				}
				c.drawRect(x*cell_size, y*cell_size, (x+1)*cell_size, (y+1)*cell_size, grey);
			}
		for(CombatMenuItem item: this.items)
			item.doDraw(c);
	}


	public void doOnTouchEvent(MotionEvent event) {
		int x,y;
		int actions = event.getActionMasked();
		switch (actions) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			x = (int)event.getX();
			y = (int)event.getY();

			if(this.itemSelected == null)
				this.hoverItem(x, y);
			else
				this.hoverCase(x, y);
			break;
		case MotionEvent.ACTION_UP:
			x = (int)event.getX();
			y = (int)event.getY();

			if(this.itemSelected == null)
				this.selectItem(x, y);
			else
				this.selectCase(x, y);
			break;

		}
	}

	private void hoverItem(int x, int y) {
		for(CombatMenuItem cmi: this.items)
			if((this.itemHovered == cmi && !cmi.isInBound(x, y)) || ((this.itemHovered == null || this.itemHovered != cmi) && cmi.isInBound(x, y))) {

				Log.d("WOTR", "itemHovered : "+this.itemHovered+"; cmi : "+cmi);
				int act = cmi.getAct();
				if(act >= 0) {
					switch(CombatAction.fromIndex(act)) {
					case VISER:
					case DEGAINER:
					case RECHARGER:
						break;
					case MOVE:
						where = this.h.whereMovement(game);
						for(Case c: where)
							c.setHighlighted(this.itemHovered != cmi);
						break;
					case DESENGAGER:
						where = this.h.whereDesengager(game);
						for(Case c: where)
							c.setHighlighted(this.itemHovered != cmi);
						break;
					case STD_ATTACK:
						where = this.h.whereAttaqueStandard(game);
						for(Case c: where)
							c.setHighlighted(this.itemHovered != cmi);
						break;
					case CHARGE:
						where = this.h.whereCharge(game);
						for(Case c: where)
							if(c instanceof Vide)
								c.setHighlighted(this.itemHovered != cmi);
						break;
					case ATTAQUE_RAPIDE:
						where = this.h.whereAttaqueRapide(game);
						for(Case c: where)
							c.setHighlighted(this.itemHovered != cmi);
						break;
					}
				}
				if(this.itemHovered != cmi) {
					this.itemHovered = cmi;
					this.itemHovered.setHovered(true);
				}
				else {
					this.itemHovered.setHovered(false);
					this.itemHovered = null;
				}
			}
	}

	private void hoverCase(int x, int y) {
		if(this.itemSelected == null || this.itemSelected.getAct() != CombatAction.CHARGE.getIndex())
			return;
		if(this.chargeCase == null) {
			Case cc = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			for(Case c: this.where)
				if(c == cc) {
					this.chargeCase = cc; 
					for(Case ccc: where)
						ccc.setHighlighted(false);
					for(Case ccc: this.game.getMap().getInRangeCases(cc, 1, 1, true, false))
						if(ccc instanceof Hero && ((this.h instanceof Player && !(ccc instanceof Player)) || (!(this.h instanceof Player) && ccc instanceof Player)))
							ccc.setHighlighted(true);
					break;
				}
		}
		// TODO: draw line
	}

	private void selectItem(int x, int y) {
		for(CombatMenuItem cmi: this.items)
			if(cmi.isInBound(x, y)) {
				int act = cmi.getAct();
				if(act < 0) {
					this.game.endTurn();
					return;
				}
				switch(CombatAction.fromIndex(act)) {
				case VISER:
					this.game.performAndSendViser(this.h);
					break;
				case DEGAINER:
					this.game.performAndSendDegainer(this.h);
					break;
				case RECHARGER:
					this.game.performAndSendRecharger(this.h);
					break;
				case CHARGE:
					this.chargeCase = null;
				case MOVE:
				case DESENGAGER:
				case STD_ATTACK:
				case ATTAQUE_RAPIDE:
					if(this.itemHovered != cmi)
						this.hoverItem(x, y);
					this.itemSelected = cmi;
					for(CombatMenuItem cmi2: this.items)
						cmi2.fadeAway();
					break;
				}
				break;
			}
	}

	private void selectCase(int x, int y) {
		if(this.itemSelected == null || this.itemSelected.getAct() < 0)
			return;
		Case c;
		switch(CombatAction.fromIndex(this.itemSelected.getAct())) {
		case VISER:
		case DEGAINER:
		case RECHARGER:
			break;
		case MOVE:
			c = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			for(Case cc: this.where)
				if(c == cc) {
					this.game.performAndSendMove(h, c);
					break;
				}
			this.reDisplayItems();
			break;
		case DESENGAGER:
			c = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			for(Case cc: this.where)
				if(c == cc) {
					this.game.performAndSendDesengager(h, c);
					break;
				}
			this.reDisplayItems();
			break;
		case STD_ATTACK:
			c = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			for(Case cc: this.where)
				if(c == cc) {
					this.game.performAndSendAttaqueStandard(h, (Hero) c);
					break;
				}
			this.reDisplayItems();
			break;
		case CHARGE:
			c = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			if(c instanceof Hero) {
				for(Case cc: this.where)
					if(c == cc) {
						this.game.performAndSendCharge(h, (Hero) c, this.chargeCase);
						break;
					}
			}
			this.reDisplayItems();
			break;
		case ATTAQUE_RAPIDE:
			c = this.game.getMap().getCase(x/this.cell_size, y/this.cell_size);
			for(Case cc: this.where)
				if(c == cc) {
					this.game.performAndSendAttaqueRapide(h, (Hero) c);
					break;
				}
			this.reDisplayItems();
			break;
		}
	}

	private void reDisplayItems() {
		for(Case cc: this.where)
			cc.setHighlighted(false);
		this.where = null;
		this.chargeCase = null;
		this.itemSelected = null;
		this.itemHovered = null;
		for(CombatMenuItem cmi: this.items) {
			cmi.setHovered(false);
			cmi.reAppear();
		}
	}
	
	private void shrink() {
		int maxOverflow = 0;
		int max = this.items.get(0).getXDest()-this.width/2;
		for(CombatMenuItem item: this.items) {
			int of = item.getOverflow();
			if(of > maxOverflow) {
				if(of > max) {
					maxOverflow = max;
					break;
				}
				maxOverflow = of;
			}
		}
		for(CombatMenuItem item: this.items) 
			item.shrink(maxOverflow);
	}
}
