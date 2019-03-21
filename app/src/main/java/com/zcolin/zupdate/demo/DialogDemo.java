package com.zcolin.zupdate.demo;

import android.content.Context;
import android.widget.TextView;

import com.telchina.libskin.attr.AttrFactory;
import com.zcolin.gui.ZDialog;
import com.zcolin.zupdate.demo.base.BaseActivity;

/**
 * Created by sdj_0 on 2019/3/21.
 */

public class DialogDemo extends ZDialog {
    private Context context;
    public DialogDemo(Context context) {
        super(context, R.layout.dialog_view);
        this.context=context;
        initView();
    }

    private void initView() {
        TextView textView= (TextView) getView(R.id.tv_show);
        BaseActivity mActivity=(BaseActivity)context;
        mActivity.dynamicAddSkinEnableView(textView, AttrFactory.TEXT_COLOR, R.color.colorStatus);
        mActivity.getSkinInflaterFactory().applySkin();
    }
}
