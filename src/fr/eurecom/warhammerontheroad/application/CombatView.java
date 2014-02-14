package fr.eurecom.warhammerontheroad.application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import fr.eurecom.warhammerontheroad.R;
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

		private int alpha_victory;
		private boolean isVictory;
		private Typeface tf;
		private int size;
		private int text_height;
		private int text_width;
		private final static long VICTORY_DURATION 	= 5000;

		public CombatThread(SurfaceHolder surfaceHolder, Context context)  {
			this.surfaceHolder = surfaceHolder;
			this.smallTexts = new SmallText[NB_SMALL_TEXT];
			this.startSmallTexts = 0;
			this.nbSmallTexts = 0;
			this.running = true;
			this.context = context;
			this.menu = null;
			this.savedStatePause = null;
			this.alpha_victory = -1;
			this.tf = Typeface.createFromAsset(context.getAssets(), "Mddst.ttf");
			this.size = context.getResources().getDimensionPixelSize(R.dimen.bigFontSize);
		}

		public void setGame(Game g) {
			this.g = g;
			if(this.g != null) {
				Hero h = this.g.getHeroTurn();
				if(h != null)
					this.displayMenu(h);
			}
				
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
				//this.restoreState(this.savedStatePause);
				if(this.g != null) {
					Hero h = this.g.getHeroTurn();
					if(h != null)
						this.displayMenu(h);
				}
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
			long start_time = SystemClock.elapsedRealtime();
			long t;
			synchronized (this.surfaceHolder) {
				this.isVictory = victory;
				this.alpha_victory = 0;
			}
			do {
				synchronized(this.surfaceHolder) {
					t = SystemClock.elapsedRealtime();
					this.alpha_victory = (int)(755 * (t - start_time) / VICTORY_DURATION);
				}
			} while(t-start_time < VICTORY_DURATION);
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
			synchronized (this.surfaceHolder) {
				this.menu = new CombatMenu(this.g, h, this.width, this.height, this.context);
			}
		}

		public void hideMenu() {
			synchronized (this.surfaceHolder) {
				this.g.getMap().setHighlighted(false);
				this.menu = null;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}

		private void addSmallText(SmallText st) {
			synchronized (this.surfaceHolder) {
				this.smallTexts[(this.startSmallTexts+this.nbSmallTexts)%NB_SMALL_TEXT] = st;
				this.nbSmallTexts++;
				if(this.nbSmallTexts > NB_SMALL_TEXT) {
					this.startSmallTexts = (this.startSmallTexts+1)%NB_SMALL_TEXT;
					this.nbSmallTexts = NB_SMALL_TEXT;
				}
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
			if(this.alpha_victory >= 0)
				return true;

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

			if(this.g == null || c == null)
				return;

			c.drawBitmap(this.g.getMap().getImageFond(), 0, 0, null);

			for(Hero h: g.getHeros())
				h.doDraw(c, cell_size);

			for(int i=0; i<this.nbSmallTexts; i++)
				this.smallTexts[(this.startSmallTexts+i)%NB_SMALL_TEXT].doDraw(c, height);

			if(this.alpha_victory >= 0) {
				Paint grey = new Paint();
				if(this.alpha_victory < 255)
					grey.setColor(Color.argb(this.alpha_victory, 0, 0, 0));
				else
					grey.setColor(Color.argb(255, 0, 0, 0));
				grey.setStyle(Style.FILL);
				c.drawRect(0, 0, this.width, this.height, grey);

				int alpha;
				if(this.alpha_victory < 255)
					alpha = this.alpha_victory;
				else if(this.alpha_victory < 500)
					alpha = 255;
				else
					alpha = 755-this.alpha_victory;
				if(alpha > 255)
					alpha = 255;
				if(alpha < 0)
					alpha = 0;

				if(this.isVictory) {
					Paint gold = new Paint();
					gold.setColor(Color.argb(alpha, 104, 81, 0));
					gold.setStyle(Style.FILL);
					gold.setTypeface(tf);
					gold.setTextSize(this.size);
					if(this.text_height == 0) {
						Rect textBounds = new Rect();
						gold.getTextBounds("Victory", 0, "Victory".length(), textBounds);
						this.text_height = textBounds.bottom-textBounds.top;
						this.text_width = textBounds.right-textBounds.left;
					}
					c.drawText("Victory", this.width/2-this.text_width/2+2, this.height/2-this.text_height/2+2, gold);
					gold.setColor(Color.argb(alpha, 255, 215, 0));
					c.drawText("Victory", this.width/2-this.text_width/2, this.height/2-this.text_height/2, gold);
				} else {
					Paint crimson = new Paint();
					crimson.setColor(Color.argb(alpha, 72, 0, 0));
					crimson.setStyle(Style.FILL);
					crimson.setTypeface(tf);
					crimson.setTextSize(this.size);
					if(this.text_height == 0) {
						Rect textBounds = new Rect();
						crimson.getTextBounds("Defeat", 0, "Defeat".length(), textBounds);
						this.text_height = textBounds.bottom-textBounds.top;
						this.text_width = textBounds.right-textBounds.left;
					}
					c.drawText("Defeat", this.width/2-this.text_width/2+2, this.height/2-this.text_height/2+1, crimson);
					crimson.setColor(Color.argb(alpha, 139, 0, 0));
					c.drawText("Defeat", this.width/2-this.text_width/2, this.height/2-this.text_height/2, crimson);
				}
			}
			else
				if(this.menu != null)
					this.menu.doDraw(c);
		}
	}

	public final static long DELAY_DEATH_ANIM 	= 1000;
	public final static long DELAY_CROSS_X		= 2000;

	private CombatThread combatThread;

	public CombatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initThread(context);
		setFocusable(true);
	}
	
	public void initThread(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		this.combatThread = new CombatThread(holder, context);
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
