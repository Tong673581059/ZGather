package com.telchina.libskin.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.View;

import com.telchina.libskin.attr.DynamicAttr;
import com.telchina.libskin.listener.IDynamicNewView;
import com.telchina.libskin.listener.ISkinUpdate;
import com.telchina.libskin.load.SkinInflaterFactory;
import com.telchina.libskin.load.SkinManager;
import com.telchina.libskin.statusbar.StatusBarBackground;
import com.zcolin.frame.app.BaseFrameActivity;

import java.util.List;

/**
 * Created by sdj on 2019/3/19.
 */

public class BaseSkinActivity extends BaseFrameActivity implements ISkinUpdate, IDynamicNewView {
    // 当前Activity是否需要响应皮肤更改需求
    private boolean isResponseOnSkinChanging = true;
    private SkinInflaterFactory mSkinInflaterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinInflaterFactory = new SkinInflaterFactory();
        //getLayoutInflater().cloneInContext(this).setFactory(mSkinInflaterFactory);
        LayoutInflaterCompat.setFactory(getLayoutInflater(), mSkinInflaterFactory);
        super.onCreate(savedInstanceState);
        changeStatusColor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SkinManager.getInstance().attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().detach(this);
        mSkinInflaterFactory.clean();
    }

    @Override
    public void onThemeUpdate() {
        Log.i("SkinBaseActivity", "onThemeUpdate");
        if (!isResponseOnSkinChanging) {
            return;
        }
        mSkinInflaterFactory.applySkin();
        changeStatusColor();

        //        //设置window的背景色
        //        Drawable drawable = new ColorDrawable(SkinManager.getInstance().getColorPrimaryDark());
        //        getWindow().setBackgroundDrawable(drawable);
    }

    public void changeStatusColor() {
        //如果当前的Android系统版本大于4.4则更改状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("SkinBaseActivity", "changeStatus");
            int color = SkinManager.getInstance().getStatusColor();
            StatusBarBackground statusBarBackground = new StatusBarBackground(this, color);
            if (color != -1) {
                statusBarBackground.setStatusBarbackColor();
            }
        }
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }

    protected void dynamicAddSkinEnableView(View view, String attrName, int attrValueResId) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId);
    }

    protected void dynamicAddSkinEnableView(View view, List<DynamicAttr> pDAttrs) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }

    final protected void enableResponseOnSkinChanging(boolean enable) {
        isResponseOnSkinChanging = enable;
    }
}
