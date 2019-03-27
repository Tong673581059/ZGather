package com.telchina.libskin.load;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.telchina.libskin.config.SkinConfig;
import com.telchina.libskin.listener.ISkinLoader;
import com.telchina.libskin.listener.ISkinUpdate;
import com.telchina.libskin.util.L;
import com.zcolin.frame.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:21:07
 */
public class SkinManager implements ISkinLoader {

    private                 List<ISkinUpdate> mSkinObservers;
    private static volatile SkinManager       mInstance;
    private                 Context           context;
    private                 Resources         mResources;
    private boolean isDefaultSkin = false;//当前的皮肤是否是默认的
    private String skinPackageName;//皮肤的包名
    private String skin;//皮肤路径

    private SkinManager() {
    }

    public void init(Context ctx) {
        context = ctx.getApplicationContext();
    }

    /**
     * 根据skin获取状态栏背景颜色
     */
    public int getStatusColor() {
        String skinName = SkinConfig.getCustomSkinPath(context);
        String name = SkinConfig.STATUS_COLOR;
        if (!SkinConfig.DEFALT_SKIN.equals(skinName)) {
            name = SkinConfig.STATUS_COLOR + "_" + skinName;
        }
        if (mResources != null) {
            int identify = mResources.getIdentifier(name, "color", context.getPackageName());
            return mResources.getColor(identify);
        }
        return -1;
    }

    /**
     * 判断当前使用的皮肤是否来自外部
     */
    public boolean isExternalSkin() {
        return !isDefaultSkin && mResources != null;
    }

    /**
     * 得到当前的皮肤路径
     */
    public String getSkin() {
        return StringUtil.isEmpty(skin) ? SkinConfig.getCustomSkinPath(context) : skin;
    }

    /**
     * 得到当前皮肤的包名
     */
    public String getSkinPackageName() {
        return skinPackageName;
    }

    public Resources getResources() {
        return mResources;
    }

    /**
     * 恢复到默认主题
     */
    public void restoreDefaultTheme() {
        SkinConfig.saveSkinPath(context, SkinConfig.DEFALT_SKIN);
        isDefaultSkin = true;
        mResources = context.getResources();
        skinPackageName = context.getPackageName();
        notifySkinUpdate();
    }

    public static SkinManager getInstance() {
        if (mInstance == null) {
            synchronized (SkinManager.class) {
                if (mInstance == null) {
                    mInstance = new SkinManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void attach(ISkinUpdate observer) {
        if (mSkinObservers == null) {
            mSkinObservers = new ArrayList<>();
        }
        if (!mSkinObservers.contains(mSkinObservers)) {
            mSkinObservers.add(observer);
        }
    }

    @Override
    public void detach(ISkinUpdate observer) {
        if (mSkinObservers == null) {
            return;
        }
        if (mSkinObservers.contains(observer)) {
            mSkinObservers.remove(observer);
        }
    }

    @Override
    public void notifySkinUpdate() {
        if (mSkinObservers == null) {
            return;
        }
        for (ISkinUpdate observer : mSkinObservers) {
            observer.onThemeUpdate();
        }
    }

    /**
     * 加载当前保存的主题皮肤
     */
    public void load() {
        String skin = SkinConfig.getCustomSkinPath(context);
        load(skin);
    }

    /**
     * 按传值切换主题皮肤
     * @param skinPackagePath 如果是主题包是另外打包的，就代表是主题包的路径；本包的就是主题皮肤名
     */
    public void load(String skinPackagePath) {
        mResources = context.getResources();
        SkinConfig.saveSkinPath(context, skinPackagePath);
        skin = skinPackagePath;
        isDefaultSkin = false;
        notifySkinUpdate();
    }

    /**
     * 按颜色资源id，获取当前主题对应的颜色id
     * @param resId 颜色资源id
     */
    public int getColor(int resId) {
        int originColor = context.getResources().getColor(resId);
        if (mResources == null || isDefaultSkin) {
            return originColor;
        }
        String skinName = SkinConfig.getCustomSkinPath(context);
        String resName = context.getResources().getResourceEntryName(resId);
        resName = resName + "_" + skinName;

        int trueResId = mResources.getIdentifier(resName, "color", context.getPackageName());
        int trueColor = 0;

        try {
            trueColor = mResources.getColor(trueResId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueColor = originColor;
        }

        return trueColor;
    }

    /**
     * 按Drawable资源id，获取当前主题对应的Drawable id
     * @param resId Drawable资源id
     */
    public Drawable getDrawable(int resId) {
        Drawable originDrawable = context.getResources().getDrawable(resId);
        if (mResources == null || isDefaultSkin) {
            return originDrawable;
        }
        String resName = context.getResources().getResourceEntryName(resId);
        String skinName = SkinConfig.getCustomSkinPath(context);
        int trueResId = mResources.getIdentifier(resName + "_" + skinName, "drawable", context.getPackageName());

        Drawable trueDrawable = null;
        try {
            L.i("SkinManager getDrawable", "SDK_INT = " + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = mResources.getDrawable(trueResId);
            } else {
                trueDrawable = mResources.getDrawable(trueResId, null);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }

        return trueDrawable;
    }

    /**
     * 按style资源id，获取当前主题对应的style id
     * @param resName style资源id
     */
    public int getStyle(String resName) {
        int orignStyle = mResources.getIdentifier(resName, "style", context.getPackageName());
        if (mResources == null || isDefaultSkin) {
            return orignStyle;
        }
        String skinName = SkinConfig.getCustomSkinPath(context);
        int trueResId = mResources.getIdentifier(resName + "_" + skinName, "style", context.getPackageName());
        int tureStyle = 0;
        try {
            tureStyle = trueResId;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            tureStyle = orignStyle;
        }
        return tureStyle;
    }

    /**
     * 加载指定资源颜色drawable,转化为ColorStateList，保证selector类型的Color也能被转换。</br>
     * 无皮肤包资源返回默认主题颜色
     */
    public ColorStateList convertToColorStateList(int resId) {
        boolean isExtendSkin = true;
        if (mResources == null || isDefaultSkin) {
            isExtendSkin = false;
        }

        String resName = context.getResources().getResourceEntryName(resId);
        if (isExtendSkin) {
            int trueResId = mResources.getIdentifier(resName + "_" + skin, "color", context.getPackageName());
            ColorStateList trueColorList = null;
            if (trueResId == 0) { // 如果皮肤包没有复写该资源，但是需要判断是否是ColorStateList
                try {
                    ColorStateList originColorList = context.getResources().getColorStateList(resId);
                    return originColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    L.e("resName = " + resName + " NotFoundException : " + e.getMessage());
                }
            } else {
                try {
                    trueColorList = mResources.getColorStateList(trueResId);
                    return trueColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    L.e("resName = " + resName + " NotFoundException :" + e.getMessage());
                }
            }
        } else {
            try {
                ColorStateList originColorList = context.getResources().getColorStateList(resId);
                return originColorList;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                L.e("resName = " + resName + " NotFoundException :" + e.getMessage());
            }

        }

        int[][] states = new int[1][1];
        return new ColorStateList(states, new int[]{context.getResources().getColor(resId)});
    }
}
