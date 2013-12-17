package fr.eurecom.warhammerontheroad;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class WotrTextView extends TextView {
	public WotrTextView(Context context){
		super(context);
		init();
	}
	
	public WotrTextView(Context context, AttributeSet attr){
		super(context,attr);
		init();
	}
	
	public WotrTextView(Context context,AttributeSet attr, int i){
		super(context, attr, i);
		init();
	}

	private void init() {
		Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "WashingtonText.ttf");
		this.setTypeface(myTypeface);
		
	}
}
