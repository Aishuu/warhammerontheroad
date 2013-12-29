package fr.eurecom.warhammerontheroad.model;

public class Combat {
	int[][] resources;
	int maxx, maxy;
	
	public Combat(int maxx, int maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
		this.resources = new int[maxy][maxx];
		for(int j=0; j< maxy; j++)
			for(int i=0; i<maxx; i++)
				this.resources[j][i] = -1;
	}
	
	public boolean isSomeoneHere(int x, int y) {
		if(x<0 || x>=maxx || y<0 || y>=maxy)
			return false;
		return (resources[y][x] != -1);
	}
	
	public int getResourceInCell(int x, int y) {
		if(x<0 || x>=maxx || y<0 || y>=maxy)
			return -1;
		return resources[y][x];
	}
	
	public void setResourceINCell(int x, int y, int resource) {
		if(x<0 || x>=maxx || y<0 || y>=maxy)
			return;
		this.resources[y][x] = resource;
	}
}
