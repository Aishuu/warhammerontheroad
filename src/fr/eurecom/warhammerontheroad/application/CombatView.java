package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import fr.eurecom.warhammerontheroad.model.Game;

public class CombatView extends SurfaceView implements SurfaceHolder.Callback {
	public class CombatThread extends Thread {
		private Game g;
		private int width, height, cell_size;
		private SurfaceHolder surfaceHolder;
		private ArrayList<SmallText> smallTexts;
		private boolean running;
		private final Object mRunLock = new Object();
		private Context context;

		public CombatThread(SurfaceHolder surfaceHolder, Context context)  {
			this.surfaceHolder = surfaceHolder;
			this.smallTexts = new ArrayList<SmallText>();
			this.running = true;
			this.context = context;
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

		private void update() {
			for(Iterator<SmallText> iterator = this.smallTexts.iterator(); iterator.hasNext();) {
				SmallText st = iterator.next();
				if(st.hasEnded())
					iterator.remove();
			}
		}

		public void printDamage(int x, int y, int dmg) {
			synchronized(this.surfaceHolder) {
				Log.d("WOTR", "Added damage for case ("+x+","+y+")");
				this.smallTexts.add(new SmallText(dmg+" hp", 255, 0, 0, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}
		
		public void printStatus(int x, int y, String status) {
			synchronized(this.surfaceHolder) {
				this.smallTexts.add(new SmallText(status, 0, 255, 255, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}
		
		public void printStandard(int x, int y, String text) {
			synchronized(this.surfaceHolder) {
				this.smallTexts.add(new SmallText(text, 25, 25, 25, x*this.cell_size + this.cell_size/2, y*this.cell_size, this.context));
			}
		}

		public boolean doOnKeyDown(int keycode, KeyEvent msg) {
			return true;
		}

		public boolean doOnKeyUp(int keycode, KeyEvent msg) {
			return true;
		}

		public void setSurfaceSize(int width, int height) {
			synchronized(this.surfaceHolder) {
				this.width = width;
				this.height = height;
				this.g.getMap().scaleImage(width, height);
				int maxX = g.getMap().getMaxX();
				int maxY = g.getMap().getMaxY();
				this.cell_size = width/maxX < height/maxY ? (int) (width/maxX) : (int) (height/maxY);
			}
		}

		public void setRunning(boolean running) {
			synchronized (mRunLock) {
				this.running = running;
			}
		}
		
		private void doDraw(Canvas c) {
			this.g.getMap().doDraw(c, this.width, this.height);
			for(SmallText st: this.smallTexts)
				st.doDraw(c, height);
		}
	}
	
	public final static long DELAY_DEATH_ANIM =	1000;

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
	public boolean onKeyDown(int keycode, KeyEvent msg) {
		return combatThread.doOnKeyDown(keycode, msg);
	}

	@Override
	public boolean onKeyUp(int keycode, KeyEvent msg) {
		return combatThread.doOnKeyUp(keycode, msg);
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
