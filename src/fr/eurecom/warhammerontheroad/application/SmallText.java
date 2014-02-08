package fr.eurecom.warhammerontheroad.application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.SystemClock;

public class SmallText {
	private String text;
	private int red, green, blue;
	private long timeStart;
	private int x_start, y_start;
	private int size;

	private static final float TIME_PRINT =	1500f;
	private static final float UP_PERCENT = 0.2f;
	private static final float GESTURE_THRESHOLD_DIP = 16.0f;
	
	public SmallText(String text, int red, int green, int blue, int x_start, int y_start, Context context) {
		this.text = text;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.timeStart = SystemClock.elapsedRealtime();
		this.y_start = y_start;

		// Convert the dips to pixels
		final float scale = context.getResources().getDisplayMetrics().density;
		this.size = (int) (GESTURE_THRESHOLD_DIP * scale + 0.5f)*2;
		Paint p = new Paint();
		p.setColor(Color.argb(1, red, green, blue));
		p.setStyle(Style.FILL);
		p.setTextSize(this.size);
		Rect textBounds = new Rect();
		p.getTextBounds(text, 0, text.length(), textBounds);
		this.x_start = x_start-textBounds.right/2;
	}
	
	public boolean hasEnded() {
		return (SystemClock.elapsedRealtime()-this.timeStart) > TIME_PRINT;
	}
	
	public void doDraw(Canvas c, int height) {
		float ratio = (SystemClock.elapsedRealtime()-this.timeStart)/TIME_PRINT;
		if(ratio < 0)
			ratio = 0;
		if(ratio > 1)
			ratio = 1;
		int y = y_start - ((int)(ratio*UP_PERCENT*height));
		Paint p = new Paint();
		p.setColor(Color.argb(((int)((1-ratio)*255)), red, green, blue));
		p.setStyle(Style.FILL);
		p.setTextSize(this.size);
//		Log.d("WOTR", "Writing text \""+text+"\" ("+x_start+","+y+"); time_start : "+this.timeStart+"; now : "+SystemClock.elapsedRealtime());
		c.drawText(text, x_start, y, p);
	}
}
