package com.telchina.libskin.config;

import android.content.Context;

import com.telchina.libskin.util.PreferencesUtils;

/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:21:29
 */
public class SkinConfig {
    public static final String NAMESPACE        = "http://schemas.android.com/android/skin";//xml最外层布局加上属性xmlns:skin="http://schemas.android.com/android/skin"来处理当前xml是否根据选择肤色换肤
    public static final String CUSTOM_SKIN      = "custom_skin";
    public static final String DEFALT_SKIN      = "default";//默认皮肤
    public static final String ATTR_SKIN_ENABLE = "skinEnable";
    public static final String STATUS_COLOR     = "colorStatus";//状态栏颜色值的key

    /**
     * get path of last skin package path
     *
     * @param context
     * @return path of skin package
     */
    public static String getCustomSkinPath(Context context) {
        return PreferencesUtils.getString(context, CUSTOM_SKIN, DEFALT_SKIN);
    }

    public static void saveSkinPath(Context context, String path) {
        PreferencesUtils.putString(context, CUSTOM_SKIN, path);
    }

    public static boolean isDefaultSkin(Context context) {
        return DEFALT_SKIN.equals(getCustomSkinPath(context));
    }
}
