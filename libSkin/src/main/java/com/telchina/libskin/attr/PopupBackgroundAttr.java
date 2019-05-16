package com.telchina.libskin.attr;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Spinner;

import com.telchina.libskin.load.SkinManager;
import com.telchina.libskin.util.L;

/**
 * src属性替换
 */

public class PopupBackgroundAttr extends SkinAttr {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {

            int color = SkinManager.getInstance().getColor(attrValueRefId);
            if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                spinner.setPopupBackgroundResource(SkinManager.getInstance().getColor(attrValueRefId));
            }
        } else if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
            Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
            Spinner spinner = (Spinner) view;
            spinner.setPopupBackgroundDrawable(bg);
        }

    }
}
