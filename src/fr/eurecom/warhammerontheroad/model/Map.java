package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class Map {
	private Case[][] cases;
	private int maxX, maxY;
	private Drawable imageFond;

	public Map(int maxX, int maxY, Drawable imageFond) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.imageFond = imageFond;
		this.cases = new Case[maxX][maxY];
		for(int i = 0; i<maxX; i++)
			for(int j=0; j<maxY; j++)
				this.cases[i][j] = new Vide(i,j);
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
}
