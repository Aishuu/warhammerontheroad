package fr.eurecom.warhammerontheroad.model;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import fr.eurecom.warhammerontheroad.network.Describable;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

public class Map implements Describable{
	private Case[][] cases;
	private int maxX, maxY;
	private Bitmap imageFond;
	private String imageFondFileName;
	private int cell_size;

	private final static String TAG	=	"Map";

	public Map(final Context context, int maxX, int maxY, String imageFondFileName) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.imageFondFileName = imageFondFileName;
		new Thread() {
			public void run() {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				//Map.this.imageFond = Drawable.createFromPath(context.getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
				Map.this.imageFond = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Map.this.imageFondFileName, options);
				//Map.this.imageFond = Drawable.createFromPath("/sdcard/"+Map.this.imageFondFileName); 	
			}
		}.start();
		this.cases = new Case[maxX][maxY];
		for(int i = 0; i<maxX; i++)
			for(int j=0; j<maxY; j++)
				this.cases[i][j] = new Vide(i,j);
	}

	public Map(WotrService service, String s) {
		this.constructFromString(service, s);
	}
	
	public void setHighlighted(boolean highlighted) {
		for(int i = 0; i<maxX; i++)
			for(int j=0; j<maxY; j++)
				this.cases[i][j].setHighlighted(highlighted);
	}
	
	public int getMaxX() {
		return this.maxX;
	}
	
	public int getMaxY() {
		return this.maxY;
	}

	public int getCellSize() {
		return this.cell_size;
	}
	
	public Case getCase(int x, int y) {
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY) {
			Log.e(TAG, "Position ("+x+","+y+") is not in Map ("+this.maxX+","+this.maxY+")...");
			return null;
		}
		return cases[x][y];
	}
	
	public void removeCase(Case c) {
		if(c == null) {
			Log.e(TAG, "Can't remove null case...");
			return;
		}
		int x = c.getX();
		int y = c.getY();
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY) {
			Log.e(TAG, "Case is not in Map ("+this.maxX+","+this.maxY+")...");
			return;
		}
		this.cases[x][y] = new Vide(x, y);
		c.setPos(-1, -1);
	}

	public void setCase(Case c, int x, int y) {
		if(c == null) {
			Log.e(TAG, "Can't set null case at position ("+x+","+y+")...");
			return;
		}
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY) {
			Log.e(TAG, "Position ("+x+","+y+") is not in Map ("+this.maxX+","+this.maxY+")...");
			return;
		}
		if(!(cases[x][y] instanceof Vide)) {
			Log.e(TAG, "There is something ("+cases[x][y]+") in case ("+x+","+y+")...");
			return;
		}
		int exX = c.getX();
		int exY = c.getY();
		// if the case is already on the grid
		if(exX != -1 && exY != -1)
			cases[exX][exY] = new Vide(exX, exY);
		c.setPos(x, y);
		cases[x][y] = c;
	}

	public void setCase(Case c, Case dest) {
		if(dest == null) {
			Log.e(TAG, "Destination is null...");
			return;
		}
		if(c == null) {
			Log.e(TAG, "Trying to set a null case on the Map at position ("+dest.getX()+","+dest.getY()+")...");
			return;
		}
		setCase(c, dest.getX(), dest.getY());
	}

	public ArrayList<Case> getInRangeCases(int x, int y, int min, int max, boolean shoot, boolean charge) {
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY) {
			Log.e(TAG, "Position ("+x+","+y+") is not in Map ("+this.maxX+","+this.maxY+")...");
			return null;
		}
		ArrayList<Case> result = new ArrayList<Case>();

		progressByOne(result, x, y, min, max, shoot, charge);
		for(int i = 0; i<maxX; i++)
			for(int j=0; j<maxY; j++)
				this.cases[i][j].setFlag(0);
		return result;
	}

	private void progressByOne (ArrayList<Case> result, int x, int y, int min, int range, boolean shoot, boolean charge){
		Case c = this.cases[x][y];
		c.setFlag(range);
		if (min == 0){
			if (!(result.contains(c))){
				if (c instanceof Vide)
					result.add(c);	
				if ((c instanceof Obstacle) && shoot)
					result.add(c);
				if ((c instanceof Hero) && (shoot || charge))
					result.add(c);
			}
		}else{
			if (result.contains(c))
				result.remove(c);
		}
		if ((range == 0) || (this.cases[x][y] instanceof Hero && charge))
			return;
		int xmin = x-1<=0 ? 0 : x-1;
		int xmax = x+1>=this.maxX ? this.maxX-1 : x+1;
		int ymin = y-1<=0 ? 0 : y-1;
		int ymax = y+1>=this.maxY ? this.maxY-1 : y+1;
		int tmpmin = min-1<=0 ? 0 : min-1;
		ArrayList<Case> tmp = new ArrayList<Case>();
		tmp.add(this.cases[xmin][y]);
		tmp.add(this.cases[xmax][y]);
		tmp.add(this.cases[x][ymin]);
		tmp.add(this.cases[x][ymax]);
		for (Case cc:tmp){
			if (c.getFlag() < range-1)
			{
				if (cc instanceof Vide)
					progressByOne(result, cc.x, cc.y, tmpmin, range-1, shoot, charge);
				if ((cc instanceof Obstacle) && shoot)
					progressByOne(result, cc.x, cc.y, tmpmin, range-1, shoot, charge);
				if ((cc instanceof Hero) && (shoot || charge))
					progressByOne(result, cc.x, cc.y, tmpmin, range-1, shoot, charge);
			}
		}
		return;
	}
	public ArrayList<Case> getInRangeCases(Case c, int min, int max, boolean shoot, boolean charge) {
		return getInRangeCases(c.getX(), c.getY(), min, max, shoot, charge);
	}

	public Bitmap getImageFond() {
		return this.imageFond;
	}

	public synchronized void doDraw(Canvas canvas, int width, int height) {
		if(canvas == null)
			return;
		canvas.drawBitmap(this.imageFond, 0, 0, null);

		for(int i=0; i<this.maxY; i++) {
			for(int j=0; j<this.maxX; j++) {
				Case c = getCase(j, i);
				if(c instanceof Hero)
					((Hero) c).doDraw(canvas, cell_size);
			}
		}
	}
	
	public void scaleImage(int width, int height) {
		this.imageFond = Bitmap.createScaledBitmap(this.imageFond, width, height, true);
		this.cell_size = width/this.maxX < height/this.maxY ? (int) (width/this.maxX) : (int) (height/this.maxY);
	}
	
	@Override
	public String describeAsString() {
		String result = this.imageFondFileName+NetworkParser.SEPARATOR+this.maxX+NetworkParser.SEPARATOR+this.maxY;

		Log.d(TAG, "Describing Map ("+this.maxX+","+this.maxY+")");
		for(int i=0; i<this.maxY; i++)
			for(int j=0; j<this.maxX; j++)
				result += NetworkParser.SEPARATOR+getCase(j,i).representInString();

		return result;
	}

	@Override
	public void constructFromString(final WotrService service, String s) {
		Log.d(TAG, "Trying to create Map from string");
		Game game = service.getGame();
		String[] parts = s.split(NetworkParser.SEPARATOR, -1);
		if(parts.length < 3) {
			Log.e(TAG, "Not enough arguments...");
			Log.e(TAG, s);
			return;
		}
		this.imageFondFileName = parts[0];
		Log.d(TAG, "Background image : "+this.imageFondFileName);
		new Thread() {
			public void run() {
				File file=new File(service.getContext().getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
				if(!file.exists())
					Log.e(TAG, "The file : "+file.getAbsolutePath()+" doesn't exist...");
				else {

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					//Map.this.imageFond = Drawable.createFromPath(context.getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
					Map.this.imageFond = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Map.this.imageFondFileName, options);
				}
				//Map.this.imageFond = Drawable.createFromPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Map.this.imageFondFileName);
				//Map.this.imageFond = Drawable.createFromPath("/sdcard/"+Map.this.imageFondFileName);
			}
		}.start();
		try {
			this.maxX = Integer.parseInt(parts[1]);
			this.maxY = Integer.parseInt(parts[2]);

			Log.d(TAG, "Size of the Map : ("+this.maxX+","+this.maxY+")");

			if(parts.length < 3+this.maxX*this.maxY) {
				Log.e(TAG, "Not enough arguments...");
				Log.e(TAG, s);
				return;
			}

			this.cases = new Case[maxX][maxY];
			for(int i = 0; i<maxY; i++)
				for(int j=0; j<maxX; j++) {
					Hero h = game.getHero(parts[i*this.maxX+j+3]);
					if(h != null) {
						this.cases[j][i] = h;
						h.setPos(j, i);
					}
					else {
						int n = Integer.parseInt(parts[i*this.maxX+j+3]);
						if(n == 0)
							this.cases[j][i] = new Vide(j,i);
						else if(n < 0)
							this.cases[j][i] = new Obstacle(j,i);
						else
							this.cases[j][i] = game.createHeroWithId(service.getContext(), n);
					}
				}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number...");
		}
	}

	public void newFileReceived(final WotrService service) {
		new Thread() {
			public void run() {
				File file=new File(service.getContext().getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
				if(!file.exists())
					Log.e(TAG, "The file : "+file.getAbsolutePath()+" doesn't exist...");
				else {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					//Map.this.imageFond = Drawable.createFromPath(context.getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
					Map.this.imageFond = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Map.this.imageFondFileName, options);
				}
				//Map.this.imageFond = Drawable.createFromPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Map.this.imageFondFileName);
				//Map.this.imageFond = Drawable.createFromPath("/sdcard/"+Map.this.imageFondFileName);
			}
		}.start();
	}

	@Override
	public String representInString() {
		return "";
	}
}