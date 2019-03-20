package com.telchina.libskin.attr;

import android.view.View;
import android.widget.TextView;

import com.telchina.libskin.load.SkinManager;
import com.telchina.libskin.util.L;

public class TextColorAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                L.i("applyAttr", "TextColorAttr");
                tv.setTextColor(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
            }
        }
    }
}
