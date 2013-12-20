package fr.eurecom.warhammerontheroad;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class WotrButton extends Button {

	public WotrButton(Context context) {
		super(context);
		init();
	}

	public WotrButton(Context context, AttributeSet attr) {
		super(context, attr);
		init();
	}

	public WotrButton(Context context, AttributeSet attr, int i) {
		super(context, attr, i);
		init();
	}
	
	private void init() {
		Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "Mddst.ttf");
		this.setTypeface(myTypeface);
	}

}
