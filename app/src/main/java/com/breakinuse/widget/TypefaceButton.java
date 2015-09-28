package com.breakinuse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.breakinuse.R;

public class TypefaceButton extends Button {

    public TypefaceButton(Context context, AttributeSet attrs) {

        super(context, attrs);
        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {

            return;

        }
        parseAttributes(context, attrs);

    }

    public TypefaceButton(Context context) {

        super(context);
        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {

            return;

        }
        parseAttributes(context);

    }

    public TypefaceButton(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {

            return;

        }
        parseAttributes(context, attrs);

    }

    private void parseAttributes(Context context, AttributeSet attrs){

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceWidget);
        String fontName = styledAttrs.getString(R.styleable.TypefaceWidget_typeface);
        styledAttrs.recycle();

        if (fontName != null) {

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            setTypeface(typeface);

        }

    }

    private void parseAttributes(Context context){

        TypedArray styledAttrs = context.obtainStyledAttributes(R.styleable.TypefaceWidget);
        String fontName = styledAttrs.getString(R.styleable.TypefaceWidget_typeface);
        styledAttrs.recycle();

        if (fontName != null) {

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            setTypeface(typeface);

        }

    }

}
