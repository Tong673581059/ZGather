package com.telchina.libskin.attr;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telchina.libskin.load.SkinManager;

import java.nio.InvalidMarkException;

/**
 * src属性替换
 */

public class SrcAttr extends SkinAttr {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_SRC.equals(attrName)) {
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setImageResource(SkinManager.getInstance().getStyle(attrValueRefName));
            }
        }
    }
}
