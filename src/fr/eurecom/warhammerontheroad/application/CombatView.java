package fr.eurecom.warhammerontheroad.application;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import fr.eurecom.warhammerontheroad.model.Game;
import fr.eurecom.warhammerontheroad.model.Hero;

public class CombatView extends SurfaceView implements SurfaceHolder.Callback {
	public class CombatThread extends Thread {
		private Game g;
		private int width, height, cell_size;
		private SurfaceHolder surfaceHolder;
		
		private SmallText[] smallTexts;
		private int startSmallTexts, nbSmallTexts;
		private static final int NB_SMALL_TEXT		= 5;
		
		private boolean running;
		private final Object mRunLock = new Object();
		private Context context;
		private CombatMenu menu;
		private Bundle savedStatePause;

		public CombatThread(SurfaceHolder surfaceHolder, Context context)  {
			this.surfaceHolder = surfaceHolder;
			this.smallTexts = new SmallText[NB_SMALL_TEXT];
			this.startSmallTexts = 0;
			this.nbSmallTexts = 0;
			this.running = true;
			this.context = context;
			this.menu = null;
			this.savedStatePause = null;
		}
		
		public void setGame(Game g) {
			this.g = g;
		}
		
		public Game getGame() {
			return this.g;
		}

		@Override
		public void run() {
			while(this.running) {
				Canvas c = null;
				try {
					c = this.surfaceHolder.lockCanvas(null);
					synchronized (this.surfaceHolder) {
						this.update();
						synchronized(mRunLock) {
							this.doDraw(c);
						}
					}
				} finally {
					if(c != null)
						this.surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
		
		public void pause() {
			this.savedStatePause = new Bundle();
			this.saveState(this.savedStatePause);
		}
		
		public void resumePause() {
			if(this.savedStatePause != null) {
				this.restoreState(this.savedStatePause);
				this.savedStatePause = null;
			}
		}
		
		public void saveState(Bundle outState) {
			if(this.menu != null) {
				outState.putBoolean("isCombatMenu", true);
				this.menu.saveState(outState);
			}
		}
		
		public void restoreState(Bundle savedState) {
			if(savedState.getBoolean("isCombatMenu")) {
				Hero h = this.g.getHero(savedState.getString("menuHero"));
				if(h != null) {
					this.menu = new CombatMenu(this.g, h, this.width, this.height, this.context);
					this.menu.restoreState(savedState);
				}
			}
		}
		
		public void displayEnd(boolean victory) {
			// TODO: display victory / defeat
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}

		private void update() {
			for(int i=0; i<this.nbSmallTexts; i++)
				if(this.smallTexts[(this.startSmallTexts+i)%NB_SMALL_TEXT].hasEnded()) {
					this.startSmallTexts ++;
					this.startSmallTexts %= NB_SMALL_TEXT;
					this.nbSmallTexts --;
				} else
					break;
		}
		
		public void displayMenu(Hero h) {
			this.menu = new CombatMenu(this.g, h, this.width, this.height, this.context);
		}
		
		public void hideMenu() {
			this.g.getMap().setHighlighted(false);
			this.menu = null;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
		private void addSmallText(SmallText st) {
			this.smallTexts[(this.startSmallTexts+this.nbSmallTexts)%NB_SMALL_TEXT] = st;
			this.nbSmallTexts++;
			if(this.nbSmallTexts > NB_SMALL_TEXT) {
				this.startSmallTexts = (this.startSmallTexts+1)%NB_SMALL_TEXT;
				this.nbSmallTexts = NB_SMALL_TEXT;
			}
		}

		public void printDamage(int x, int y, int dmg) {
			synchronized(this.surfaceHolder) {
				this.addSmallText(new SmallText(dmg+" hp", 255, 0, 0, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}
		
		public void printStatus(int x, int y, String status) {
			synchronized(this.surfaceHolder) {
				this.addSmallText(new SmallText(status, 0, 255, 255, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}
		
		public void printStandard(int x, int y, String text) {
			synchronized(this.surfaceHolder) {
				this.addSmallText(new SmallText(text, 25, 25, 25, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}

		public boolean doOnTouchEvent(MotionEvent event) {
			if(this.menu == null)
				return false;
			this.menu.doOnTouchEvent(event);
			return true;
		}

		public void setSurfaceSize(int width, int height) {
			synchronized(this.surfaceHolder) {
				this.width = width;
				this.height = height;
				this.g.getMap().scaleImage(width, height);
				int maxX = g.getMap().getMaxX();
				int maxY = g.getMap().getMaxY();
				this.cell_size = width/maxX < height/maxY ? (width/maxX) : (height/maxY);
				if(this.menu != null)
					this.menu.updateSize(width, height);
			}
		}

		public void setRunning(boolean running) {
			synchronized (mRunLock) {
				this.running = running;
			}
		}
		
		private void doDraw(Canvas c) {
			
			c.drawBitmap(this.g.getMap().getImageFond(), 0, 0, null);

			for(Hero h: g.getHeros())
				h.doDraw(c, cell_size);
		
			for(int i=0; i<this.nbSmallTexts; i++)
				this.smallTexts[(this.startSmallTexts+i)%NB_SMALL_TEXT].doDraw(c, height);
			
			if(this.menu != null)
				this.menu.doDraw(c);
		}
	}
	
	public final static long DELAY_DEATH_ANIM 	= 1000;
	public final static long DELAY_CROSS_X		= 2000;

	private CombatThread combatThread;

	public CombatView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		this.combatThread = new CombatThread(holder, context);

		setFocusable(true);
	}

	public CombatThread getThread() {
		return this.combatThread;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return combatThread.doOnTouchEvent(event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		combatThread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		combatThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		this.stop();
	}
	
	public void stop() {
		boolean retry = true;
		combatThread.setRunning(false);
		while(retry)  {
			try {
				combatThread.join();
				retry = false;
			} catch(InterruptedException e) {
			}
		}
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int maxX = this.combatThread.getGame().getMap().getMaxX();
		int maxY = this.combatThread.getGame().getMap().getMaxY();

		int cell_size = MeasureSpec.getSize(widthMeasureSpec)/maxX < MeasureSpec.getSize(heightMeasureSpec)/maxY ? (int) (MeasureSpec.getSize(widthMeasureSpec)/maxX) : (int) (MeasureSpec.getSize(heightMeasureSpec)/maxY);
		setMeasuredDimension(cell_size*maxX, cell_size*maxY);
	}
}
