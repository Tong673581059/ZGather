/*
 * *********************************************************
 *   author   zhuxuetong
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-1-9 下午5:16
 * ********************************************************
 */

package com.zpivot.zgather.amodule.base;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telchina.libskin.attr.AttrFactory;
import com.telchina.libskin.attr.DynamicAttr;
import com.telchina.libskin.listener.IDynamicNewView;
import com.telchina.libskin.listener.ISkinUpdate;
import com.telchina.libskin.load.SkinInflaterFactory;
import com.telchina.libskin.load.SkinManager;
import com.telchina.libskin.statusbar.StatusBarBackground;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.util.DisplayUtil;
import com.zcolin.frame.util.ScreenUtil;
import com.zcolin.frame.util.StringUtil;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.gui.ZDialogProgress;
import com.zpivot.zgather.R;
import com.zpivot.zgather.amodule.role.Role;
import com.zpivot.zgather.amodule.role.RoleControlType;
import com.zpivot.zgather.amodule.role.RoleMgr;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;


/**
 * 客户端Activity的基类
 * <p>
 * 是否需要沉浸式的  {@link ActivityParam()} isImmerse  default true}
 * <p>
 * 是否需要带ToolBar的  {@link ActivityParam()} isShowToolBar  default true}
 * <p/>
 * 是否需要ToolBar带返回按钮并且实现了返回的  {@link ActivityParam()} isShowReturn  default true}
 * <p/>
 * 是否需要全屏的  {@link ActivityParam()} isFullScreen  default true}
 * <p/>
 * 沉浸模式是否需要空出顶部状态栏距离{@link ActivityParam() isImmersePaddingTop default false}
 * <p/>
 * 是否需要换肤功能{@link ActivityParam() isSkin default false}
 * <p/>
 * 是否需要权限控制{@link ActivityParam() isRole default false}
 */
public abstract class BaseActivity extends BaseFrameActivity implements ISkinUpdate, IDynamicNewView {
    /*参数在数组中的Index*/
    private static final int INDEX_ISIMMERSE           = 0;
    private static final int INDEX_ISFULLSCREEN        = 1;
    private static final int INDEX_ISIMMERSEPADDINGTOP = 2;
    private static final int INDEX_ISSHOWTOOLBAR       = 3;
    private static final int INDEX_ISSHOWRETURN        = 4;
    private static final int INDEX_ISSKIN              = 5;
    private static final int INDEX_ISROLE              = 6;

    private boolean[] activityParam = new boolean[]{ActivityParam.ISIMMERSE_DEF_VALUE, ActivityParam.ISFULLSCREEN_DEF_VALUE, ActivityParam
            .ISIMMERSEPADDINGTOP_DEF_VALUE, ActivityParam.ISSHOWTOOLBAR_DEF_VALUE, ActivityParam.ISSHOWRETURN_DEF_VALUE,
            ActivityParam.ISSKIN_DEF_VALUE, ActivityParam.ISROLE_DEF_VALUE};

    private Toolbar  toolbar;
    private View     toolBarView;           //自定义的toolBar的布局
    private TextView toolbarTitleView;       //标题 居中
    private TextView toolbarLeftBtn;        //最左侧预制按钮，一般防止返回
    private TextView toolbarRightBtn;        //最右侧预制按钮

    protected SkinInflaterFactory mSkinInflaterFactory;

    /**
     * 注解注入值获取
     */
    private void injectActivityParam() {
        ActivityParam requestParamsUrl = getClass().getAnnotation(ActivityParam.class);
        if (requestParamsUrl != null) {
            activityParam[INDEX_ISIMMERSE] = requestParamsUrl.isImmerse();
            activityParam[INDEX_ISFULLSCREEN] = requestParamsUrl.isFullScreen();
            activityParam[INDEX_ISIMMERSEPADDINGTOP] = requestParamsUrl.isImmersePaddingTop();
            activityParam[INDEX_ISSHOWTOOLBAR] = requestParamsUrl.isShowToolBar();
            activityParam[INDEX_ISSHOWRETURN] = requestParamsUrl.isShowReturn();
            activityParam[INDEX_ISSKIN] = requestParamsUrl.isSkin();
            activityParam[INDEX_ISROLE] = requestParamsUrl.isRole();
        }
    }

    private void injectZClick() {
        Method[] methods = getClass().getDeclaredMethods();
        for (final Method method : methods) {
            ZClick clickArray = method.getAnnotation(ZClick.class);//通过反射api获取方法上面的注解
            if (clickArray != null && clickArray.value().length > 0) {
                method.setAccessible(true);
                final boolean isHasParam = method.getParameterTypes() != null && method.getParameterTypes().length > 0;
                for (int click : clickArray.value()) {
                    final View view = getView(click);//通过注解的值获取View控件
                    if (view == null) {
                        return;
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (isHasParam) {
                                    method.invoke(mActivity, view);
                                } else {
                                    method.invoke(mActivity);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    private void injectRole() {
        if (activityParam[INDEX_ISROLE]) {
            Field[] fields = getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                Class<?> acceptableType = View.class;
                for (Field field : fields) {
                    if (field == null) {
                        continue;
                    }
                    // 这个变量是个可以接收的类型(android.view.View) 并且不是static字段和final字段
                    if (acceptableType.isAssignableFrom(field.getType()) && field.getModifiers() != Modifier.FINAL && field.getModifiers() != Modifier.STATIC) {
                        Role annotation = field.getAnnotation(Role.class);

                        if (annotation != null) {
                            String[] roles = annotation.roles();
                            RoleControlType rolePerm = RoleMgr.hasRoles(roles);
                            try {
                                Method method = field.getType().getMethod("setVisibility", int.class);
                                Method methodClick = field.getType().getMethod("setOnClickListener", View.OnClickListener.class);
                                switch (rolePerm) {
                                    case ROLE_GONE:
                                        method.invoke(field.get(this), View.GONE);
                                        break;
                                    case ROLE_NO_CLICK:
                                        method.invoke(field.get(this), View.VISIBLE);
                                        methodClick.invoke(field.get(this), (View.OnClickListener) v -> {
                                            ToastUtil.toastShort(RoleMgr.ROLE_TOAST);
                                        });
                                        break;
                                    default:
                                        method.invoke(field.get(this), View.VISIBLE);
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectActivityParam();
        //如果需要换肤，用自定义的InflaterFactory，用来代替默认的InflaterFactory
        if (activityParam[INDEX_ISSKIN]) {
            mSkinInflaterFactory = new SkinInflaterFactory();
            LayoutInflaterCompat.setFactory(getLayoutInflater(), mSkinInflaterFactory);
        }
        super.onCreate(savedInstanceState);
        if (activityParam[INDEX_ISSKIN]) {
//            changeStatusColor();
        }

        if (activityParam[INDEX_ISFULLSCREEN] && Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (activityParam[INDEX_ISIMMERSE] && Build.VERSION.SDK_INT >= 19) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activityParam[INDEX_ISSKIN]) {
            SkinManager.getInstance().attach(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activityParam[INDEX_ISSKIN]) {
            SkinManager.getInstance().detach(this);
            mSkinInflaterFactory.clean();
        }
    }

    @Override
    public void onThemeUpdate() {
        Log.i("SkinBaseActivity", "onThemeUpdate");
        if (!activityParam[INDEX_ISSKIN]) {
            return;
        }
        mSkinInflaterFactory.applySkin();
//        changeStatusColor();
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
    protected void onStart() {
        super.onStart();
        injectRole();
    }

    @Override
    public void setContentView(int layoutResID) {
        if (activityParam[INDEX_ISSHOWTOOLBAR] && !activityParam[INDEX_ISFULLSCREEN]) {
            super.setContentView(initToolBar(layoutResID));
            setSupportActionBar(toolbar);

            if (activityParam[INDEX_ISSHOWRETURN]) {
                setToolbarLeftBtnText(" ");
                setToolbarLeftBtnCompoundDrawableLeft(R.drawable.gui_icon_arrow_back);
            }
        } else {
            super.setContentView(layoutResID);

            if (activityParam[INDEX_ISIMMERSE] && activityParam[INDEX_ISIMMERSEPADDINGTOP] && !activityParam[INDEX_ISSHOWTOOLBAR]) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                viewGroup.setPadding(0, ScreenUtil.getStatusBarHeight(this), 0, 0);
            }
        }

        injectZClick();
    }

    @Override
    public void setContentView(View view) {
        if (activityParam[INDEX_ISSHOWTOOLBAR] && !activityParam[INDEX_ISFULLSCREEN]) {
            super.setContentView(initToolBar(view));
            setSupportActionBar(toolbar);

            if (activityParam[INDEX_ISSHOWRETURN]) {
                setToolbarLeftBtnText(" ");
                setToolbarLeftBtnCompoundDrawableLeft(R.drawable.gui_icon_arrow_back);
            }
        } else {
            super.setContentView(view);

            if (activityParam[INDEX_ISIMMERSE] && activityParam[INDEX_ISIMMERSEPADDINGTOP] && !activityParam[INDEX_ISSHOWTOOLBAR]) {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                viewGroup.setPadding(0, ScreenUtil.getStatusBarHeight(this), 0, 0);
            }
        }

        injectZClick();
    }


    private ViewGroup initToolBar(int layoutResID) {
        View userView = LayoutInflater.from(this).inflate(layoutResID, null);
        return initToolBar(userView);
    }

    protected ViewGroup initToolBar(View userView) {
        /*获取主题中定义的悬浮标志*/
        TypedArray typedArray = getTheme().obtainStyledAttributes(R.styleable.ToolBarTheme);
        boolean overly = typedArray.getBoolean(R.styleable.ToolBarTheme_android_windowActionBarOverlay, false);
        typedArray.recycle();

        /*将toolbar引入到父容器中*/
        View toolbarLay = LayoutInflater.from(this).inflate(R.layout.tsf_view_base_toolbar, null);
        toolbar = toolbarLay.findViewById(R.id.id_tool_bar);
        if (activityParam[INDEX_ISIMMERSE]) {
            int statusBarHeight = ScreenUtil.getStatusBarHeight(this);
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            toolbar.getLayoutParams().height += statusBarHeight;
        }
        toolBarView = getLayoutInflater().inflate(getToolBarLayout() == 0 ? R.layout.tsf_view_base_toolbar_baseview : getToolBarLayout(), toolbar);
        toolbarTitleView = toolBarView.findViewById(R.id.toolbar_title);
        toolbarLeftBtn = toolBarView.findViewById(R.id.toolbar_btn_left);
        toolbarRightBtn = toolBarView.findViewById(R.id.toolbar_btn_right);
        toolbarTitleView.setVisibility(View.GONE);
        toolbarLeftBtn.setVisibility(View.GONE);
        toolbarRightBtn.setVisibility(View.GONE);
        BaseClickListener clickListener = new BaseClickListener();
        toolbarLeftBtn.setOnClickListener(clickListener);
        toolbarRightBtn.setOnClickListener(clickListener);

        if (activityParam[INDEX_ISSKIN]){
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbar, AttrFactory.BACKGROUND, R.color.colorPrimary);
//            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarTitleView, AttrFactory.STYLE, R.style.TextStyle_TCPrimary_Big);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarTitleView, AttrFactory.TEXT_COLOR, R.color.toolbarTitleTextColor);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarLeftBtn, AttrFactory.BACKGROUND, R.color.toolbarLeftBackground);
//            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarLeftBtn, AttrFactory.STYLE, R.style.TextStyle_TCPrimary_Small);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarLeftBtn, AttrFactory.TEXT_COLOR, R.color.toolbarLeftTextColor);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarLeftBtn, AttrFactory.DRAWABLE_LEFT,0);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarRightBtn, AttrFactory.BACKGROUND, R.color.toolbarRightBackground);
//            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarRightBtn, AttrFactory.STYLE, R.style.TextStyle_TCSubPrimary_Small);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarRightBtn, AttrFactory.TEXT_COLOR, R.color.toolbarRightTextColor);
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, toolbarRightBtn, AttrFactory.DRAWABLE_LEFT, 0);
        }

        /*直接创建一个布局，作为视图容器的父容器*/
        ViewGroup contentView;
        if (overly) {
            //不明原因导致布局向右移动了一些，移动回来
            RelativeLayout.LayoutParams layParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layParam.leftMargin = DisplayUtil.dip2px(mActivity, -10);
            contentView = new RelativeLayout(this);
            contentView.addView(userView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentView.addView(toolbarLay, layParam);
        } else {
            //不明原因导致布局向右移动了一些，移动回来
            LinearLayout.LayoutParams layParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layParam.leftMargin = DisplayUtil.dip2px(mActivity, -10);
            contentView = new LinearLayout(this);
            ((LinearLayout) contentView).setOrientation(LinearLayout.VERTICAL);
            contentView.addView(toolbarLay, layParam);
            contentView.addView(userView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        return contentView;
    }

    /**
     * 可以自己扩展Layout，但是其扩展的Layout里必须包含现在所有的控件Id，也就是可以增加控件不可以移除控件
     */
    public @LayoutRes
    int getToolBarLayout() {
        return 0;
    }

    public boolean isImmerse() {
        return activityParam[INDEX_ISIMMERSE];
    }

    public boolean isSkin() {
        return activityParam[INDEX_ISSKIN];
    }

    /**
     *  返回自定义的InflaterFactory
     */
    public SkinInflaterFactory getSkinInflaterFactory() {
        return mSkinInflaterFactory;
    }

    /**
     * 动态添加那些有皮肤更改需求的View，及其对应的属性
     */
    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }

    /**
     * 动态添加那些有皮肤更改需求的View，及其对应的属性
     *
     * @param attrName       属性名
     * @param attrValueResId 属性资源id
     */
    public void dynamicAddSkinEnableView(View view, String attrName, int attrValueResId) {
        if (mSkinInflaterFactory != null) {
            mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId);
        }
    }

    /**
     * 设置ToolBar的标题
     *
     * @param title ：ToolBar的标题
     */
    public void setToolbarTitle(String title) {
        if (StringUtil.isNotEmpty(title)) {
            toolbarTitleView.setText(title);
            toolbarTitleView.setVisibility(View.VISIBLE);
        } else {
            toolbarTitleView.setText(null);
            toolbarTitleView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置ToolBar的标题
     *
     * @param title ：ToolBar的标题
     */
    public void setToolbarTitle(SpannableString title) {
        if (StringUtil.isNotEmpty(title)) {
            toolbarTitleView.setText(title);
            toolbarTitleView.setVisibility(View.VISIBLE);
        } else {
            toolbarTitleView.setText(null);
            toolbarTitleView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置ToolBar的预置按钮长按可用
     */
    public void setLongClickEnable() {
        BaseLongClickListener longClickListener = new BaseLongClickListener();
        toolbarLeftBtn.setOnLongClickListener(longClickListener);
        toolbarRightBtn.setOnLongClickListener(longClickListener);
    }

    /**
     * 设置ToolBar的预置按钮长按不可用
     */
    public void setLongClickDisable() {
        toolbarLeftBtn.setOnLongClickListener(null);
        toolbarRightBtn.setOnLongClickListener(null);
    }

    /**
     * 设置ToolBar左侧预置按钮文字
     */
    public void setToolbarLeftBtnText(String extra) {
        if (StringUtil.isNotEmpty(extra)) {
            toolbarLeftBtn.setText(extra);
            toolbarLeftBtn.setVisibility(View.VISIBLE);
        } else {
            toolbarLeftBtn.setText(null);
            toolbarLeftBtn.setVisibility(View.GONE);
        }
    }

    public void setToolbarLeftBtnCompoundDrawableLeft(int res) {
        toolbarLeftBtn.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
        toolbarLeftBtn.setVisibility(View.VISIBLE);
    }

    public void setToolbarLeftBtnCompoundDrawableLeft(Drawable able) {
        toolbarLeftBtn.setCompoundDrawablesWithIntrinsicBounds(able, null, null, null);
        toolbarLeftBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置ToolBar左侧预置按钮的图片
     */
    public void setToolbarLeftBtnBackground(int res) {
        toolbarLeftBtn.setBackgroundResource(res);
        toolbarLeftBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置ToolBar左侧预置按钮的图片
     */
    public void setToolbarLeftBtnBackground(Drawable able) {
        if (able != null) {
            toolbarLeftBtn.setBackgroundDrawable(able);
            toolbarLeftBtn.setVisibility(View.VISIBLE);
        } else {
            toolbarLeftBtn.setBackgroundDrawable(null);
            toolbarLeftBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置ToolBar右侧侧预置按钮的文字
     */
    public void setToolbarRightBtnText(String extra) {
        if (StringUtil.isNotEmpty(extra)) {
            toolbarRightBtn.setText(extra);
            toolbarRightBtn.setVisibility(View.VISIBLE);
        } else {
            toolbarRightBtn.setText(null);
            toolbarRightBtn.setVisibility(View.GONE);
        }
    }

    public void setToolbarRightBtnCompoundDrawableRight(int res) {
        toolbarRightBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        toolbarRightBtn.setVisibility(View.VISIBLE);
    }

    public void setToolbarRightBtnCompoundDrawableRight(Drawable able) {
        toolbarRightBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, able, null);
        toolbarRightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置ToolBar右侧侧预置按钮的图片
     *
     * @param res 显示的资源ID
     */
    public void setToolBarRightBtnBackground(int res) {
        toolbarRightBtn.setBackgroundResource(res);
        toolbarRightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置ToolBar预制按钮二的图片
     *
     * @param able 显示的Drawable对象
     */
    public void setToolBarRightBtnBackground(Drawable able) {
        if (able != null) {
            toolbarRightBtn.setBackgroundDrawable(able);
            toolbarRightBtn.setVisibility(View.VISIBLE);
        } else {
            toolbarRightBtn.setBackgroundDrawable(null);
            toolbarRightBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置ToolBar的背景颜色
     *
     * @param color 颜色值
     */
    public void setToolBarBackgroundColor(int color) {
        toolBarView.setBackgroundColor(color);
    }

    /**
     * 设置ToolBar的背景资源
     *
     * @param res 资源ID
     */
    public void setToolBarBackgroundRes(int res) {
        toolBarView.setBackgroundResource(res);
    }

    /**
     * 获取ToolBar的标题控件
     *
     * @return 标题控件
     */
    public TextView getToolBarTitleView() {
        return toolbarTitleView;
    }

    /**
     * 获取ToolBar左侧侧预置按钮
     */
    public TextView getToolBarLeftBtn() {
        return toolbarLeftBtn;
    }

    /**
     * 获取ToolBar右侧侧预置按钮
     */
    public TextView getToolBarRightBtn() {
        return toolbarRightBtn;
    }

    /**
     * 左侧按钮点击回调，子类如需要处理点击事件，重写此方法
     */
    protected void onToolBarLeftBtnClick() {
        if (activityParam[INDEX_ISSHOWRETURN]) {
            this.finish();
        }
    }

    /**
     * 右侧按钮点击回调，子类如需要处理点击事件，重写此方法
     */
    protected void onToolBarRightBtnClick() {
    }

    /**
     * 左侧按钮长按回调，子类如需要处理点击事件，重写此方法
     */
    protected void onToolBarLeftBtnLongClick() {
    }

    /**
     * 右侧按钮长按回调，子类如需要处理点击事件，重写此方法
     */
    protected void onToolBarRightBtnLongClick() {
    }

    /**
     *
     * @return
     */
    protected void setHideToolBar() {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return new ZDialogProgress(mActivity);
    }

    /**
     * 预置按钮的点击事件类
     */
    private class BaseClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.toolbar_btn_left) {
                onToolBarLeftBtnClick();
            } else if (v.getId() == R.id.toolbar_btn_right) {
                onToolBarRightBtnClick();
            }
        }
    }

    /**
     * 预制按钮的 长按事件类
     */
    private class BaseLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.toolbar_btn_left) {
                onToolBarLeftBtnLongClick();
            } else if (v.getId() == R.id.toolbar_btn_right) {
                onToolBarRightBtnLongClick();
            }
            return true;
        }
    }
}
