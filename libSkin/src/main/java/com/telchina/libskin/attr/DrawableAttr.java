package com.telchina.libskin.attr;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.telchina.libskin.load.SkinManager;
import com.telchina.libskin.util.L;


/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:21:46
 */
public class DrawableAttr extends SkinAttr {

    @Override
    public void apply(View view) {

       if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
            Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
            if (view instanceof Button) {
                if (AttrFactory.DRAWABLE_LEFT.equals(attrName)) {
                    ((Button) view).setCompoundDrawablesWithIntrinsicBounds(bg, null, null, null);
                } else if (AttrFactory.DRAWABLE_RIGHT.equals(attrName)) {
                    ((Button) view).setCompoundDrawablesWithIntrinsicBounds(null, null, bg, null);
                } else if (AttrFactory.DRAWABLE_TOP.equals(attrName)) {
                    ((Button) view).setCompoundDrawablesWithIntrinsicBounds(null, bg, null, null);
                } else if (AttrFactory.DRAWABLE_BOTTOM.equals(attrName)) {
                    ((Button) view).setCompoundDrawablesWithIntrinsicBounds(null, null, null, bg);
                }
            }
            L.i("applyAttr", "apply as drawable");
        }
    }
}
