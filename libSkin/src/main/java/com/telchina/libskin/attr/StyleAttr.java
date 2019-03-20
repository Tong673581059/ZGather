package com.telchina.libskin.attr;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.telchina.libskin.load.SkinManager;

/**
 * Created by sdj_0 on 2019/3/20.
 */

public class StyleAttr extends SkinAttr {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void apply(View view) {
        if(RES_TYPE_NAME_STYLE.equals(attrName)){
            if(view instanceof TextView){
                TextView textView= (TextView) view;
                textView.setTextAppearance(SkinManager.getInstance().getStyle(attrValueRefName));
            }else if(view instanceof Button){
                Button button= (Button) view;
                button.setTextAppearance(SkinManager.getInstance().getStyle(attrValueRefName));
            }
        }
    }
}
