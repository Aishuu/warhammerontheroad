package fr.eurecom.warhammerontheroad.application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


public class WotrEditText extends EditText {

	public WotrEditText(Context context) {
		super(context);
		init();
	}

	

	public WotrEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WotrEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "WashingtonText.ttf");
		this.setTypeface(myTypeface);

	}
}
