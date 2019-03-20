package com.telchina.libskin.config;

import android.content.Context;

import com.telchina.libskin.util.PreferencesUtils;

/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:21:29
 */
public class SkinConfig {
    public static final String NAMESPACE = "http://schemas.android.com/android/skin";//xml最外层布局加上属性xmlns:skin="http://schemas.android.com/android/skin"来处理当前xml是否根据选择肤色换肤
    public static final String PREF_CUSTOM_SKIN_PATH = "cn_feng_skin_custom_path";
    public static final String DEFALT_SKIN = "default";//默认皮肤
    public static final String ATTR_SKIN_ENABLE = "enable";

    /**
     * get path of last skin package path
     *
     * @param context
     * @return path of skin package
     */
    public static String getCustomSkinPath(Context context) {
        return PreferencesUtils.getString(context, PREF_CUSTOM_SKIN_PATH, DEFALT_SKIN);
    }

    public static void saveSkinPath(Context context, String path) {
        PreferencesUtils.putString(context, PREF_CUSTOM_SKIN_PATH, path);
    }

    public static boolean isDefaultSkin(Context context) {
        return DEFALT_SKIN.equals(getCustomSkinPath(context));
    }
}
