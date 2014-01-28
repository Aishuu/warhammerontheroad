package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.network.Describable;
import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Map implements Describable{
	private Case[][] cases;
	private int maxX, maxY;
	private Drawable imageFond;
	private String imageFondFileName;

	private final static String TAG	=	"Map";

	public Map(final Context context, int maxX, int maxY, String imageFondFileName) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.imageFondFileName = imageFondFileName;
		new Thread() {
			public void run() {
				Map.this.imageFond = Drawable.createFromPath(context.getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
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

	public Case getCase(int x, int y) {
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY)
			return null;
		return cases[x][y];
	}

	public void setCase(Case c, int x, int y) {
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY)
			return;
		if(!(cases[x][y] instanceof Vide))
			return;
		int exX = c.getX();
		int exY = c.getY();
		// if the case is already on the grid
		if(exX != -1 && exY != -1)
			cases[exX][exY] = new Vide(exX, exY);
		c.setPos(x, y);
		cases[x][y] = c;
	}

	public void setCase(Case c, Case dest) {
		setCase(c, dest.getX(), dest.getY());
	}

	public ArrayList<Case> getInRangeCases(int x, int y, int min, int max) {
		if(x<0 || x>=this.maxX || y<0 || y>=this.maxY)
			return null;
		ArrayList<Case> result = new ArrayList<Case>();

		for(int i = x-max<0 ? 0 : x-max; i<= (x+max>=this.maxX ? this.maxX-1 : x+max); i++) {
			int n = max-Math.abs(i-x);
			for(int j = y-n<0 ? 0 : y-n; j<= (y+n>=this.maxY? this.maxY-1 : y+n); j++)
				if(Math.abs(i-x)+Math.abs(j-y) >= min)
					result.add(this.cases[i][j]);
		}

		return result;
	}

	public ArrayList<Case> getInRangeCases(Case c, int min, int max) {
		return getInRangeCases(c.getX(), c.getY(), min, max);
	}

	public Drawable getImageFond() {
		return this.imageFond;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void draw(LinearLayout table, int total_width, int total_height, Context context) {
		table.removeAllViews();

		int cell_size = total_width/this.maxX < total_height/this.maxY ? (int) (total_width/this.maxX) : (int) (total_height/this.maxY);

		if (android.os.Build.VERSION.SDK_INT >= 16)
			table.setBackground(this.imageFond);
		else
			table.setBackgroundDrawable(this.imageFond);

		Log.d(TAG, "Size of the layout : "+this.maxX*cell_size+"x"+this.maxY*cell_size);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(this.maxX*cell_size,this.maxY*cell_size);
		lp.setMargins(0, 0, 0, 0);
		table.setLayoutParams(lp);
		for(int i=0; i<this.maxY; i++) {
			LinearLayout line = new LinearLayout(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(this.maxX*cell_size, cell_size);
			params.setMargins(0, 0, 0, 0);
			line.setLayoutParams(params);
			line.setOrientation(LinearLayout.HORIZONTAL);

			for(int j=0; j<this.maxX; j++) {
				ImageView im = new ImageView(context);
				params = new LinearLayout.LayoutParams(cell_size, cell_size);
				params.setMargins(0, 0, 0, 0);
				im.setLayoutParams(params);
				Case c = getCase(i, j);
				if(!(c instanceof Vide))
					im.setImageResource(c.getResource());
				line.addView(im);
			}
			table.addView(line);
		}
	}

	@Override
	public String describeAsString() {
		String result = this.imageFondFileName+NetworkParser.SEPARATOR+this.maxX+NetworkParser.SEPARATOR+this.maxY;

		for(int i=0; i<this.maxY; i++)
			for(int j=0; j<this.maxX; j++)
				result += NetworkParser.SEPARATOR+getCase(i,j).toString();

		return result;
	}

	@Override
	public void constructFromString(final WotrService service, String s) {
		Game game = service.getGame();
		String[] parts = s.split(NetworkParser.SEPARATOR, -1);
		if(parts.length < 3) {
			Log.e(TAG, "Not enough arguments...");
			Log.e(TAG, s);
			return;
		}
		this.imageFondFileName = parts[0];
		new Thread() {
			public void run() {
				Map.this.imageFond = Drawable.createFromPath(service.getContext().getFilesDir().getAbsolutePath()+"/"+Map.this.imageFondFileName);
			}
		}.start();
		try {
			this.maxX = Integer.parseInt(parts[1]);
			this.maxY = Integer.parseInt(parts[2]);

			if(parts.length < 3+this.maxX*this.maxY) {
				Log.e(TAG, "Not enough arguments...");
				Log.e(TAG, s);
				return;
			}

			this.cases = new Case[maxX][maxY];
			for(int i = 0; i<maxX; i++)
				for(int j=0; j<maxY; j++) {
					Hero h = game.getHero(parts[i*this.maxY+j+3]);
					if(h != null) {
						this.cases[i][j] = h;
						h.setPos(i, j);
					}
					else {
						int n = Integer.parseInt(parts[i*this.maxY+j+3]);
						if(n == 0)
							this.cases[i][j] = new Vide(i,j);
						else if(n < 0)
							this.cases[i][j] = new Obstacle(i,j);
						else
							this.cases[i][j] = game.createHeroWithId(service.getContext(), n);
					}
				}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number...");
		}
	}

	@Override
	public String representInString() {
		return "";
	}
}
