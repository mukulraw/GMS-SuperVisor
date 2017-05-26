package morgantech.com.gms.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWorkSansRegular extends TextView {

	public TextViewWorkSansRegular(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextViewWorkSansRegular(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextViewWorkSansRegular(Context context) {
		super(context);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"WorkSans-Regular.otf");
		setTypeface(tf);
	//	setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

	}
	


}
