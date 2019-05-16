package com.telchina.libskin.attr;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Spinner;

import com.telchina.libskin.load.SkinManager;

/**
 * src属性替换
 */

public class PopupBackgroundAttr extends SkinAttr {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void apply(View view) {
        if (view instanceof Spinner) {
            Spinner spinner = (Spinner) view;
            spinner.setPopupBackgroundResource(SkinManager.getInstance().getColor(attrValueRefId));
        }
    }
}
