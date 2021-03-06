/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午5:16
 * ********************************************************
 */

package com.zpivot.zgather.amodule.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telchina.libskin.attr.AttrFactory;
import com.telchina.libskin.attr.DynamicAttr;
import com.telchina.libskin.listener.IDynamicNewView;
import com.zcolin.frame.app.BaseFrameFrag;
import com.zcolin.frame.util.DisplayUtil;
import com.zcolin.frame.util.ScreenUtil;
import com.zcolin.frame.util.StringUtil;
import com.zcolin.frame.util.ToastUtil;
import com.zpivot.zgather.R;
import com.zpivot.zgather.amodule.role.Role;
import com.zpivot.zgather.amodule.role.RoleControlType;
import com.zpivot.zgather.amodule.role.RoleMgr;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端Fragment的基类
 * <p>
 * 是否需要带ToolBar的  {@link FragmentParam()} isShowToolBar  default true}
 * <p/>
 * 是否需要ToolBar带返回按钮并且实现了返回的  {@link FragmentParam()} isShowReturn  default true}
 * <p/>
 * 沉浸模式是否需要空出顶部状态栏距离{@link FragmentParam() isImmersePaddingTop default false}
 * <p/>
 * 是否需要换肤功能{@link ActivityParam() isSkin default false}
 * <p/>
 * 是否需要权限控制{@link ActivityParam() isRole default false}
 */
public abstract class BaseFragment extends BaseFrameFrag implements IDynamicNewView {
    private static final int INDEX_ISSHOWTOOLBAR       = 0;
    private static final int INDEX_ISSHOWRETURN        = 1;
    private static final int INDEX_ISIMMERSEPADDINGTOP = 2;
    private static final int INDEX_ISSKIN              = 3;
    private static final int INDEX_ISROLE              = 4;

    private boolean[] fragmentParam = new boolean[]{FragmentParam.ISSHOWTOOLBAR_DEF_VALUE, FragmentParam.ISSHOWRETURN_DEF_VALUE, FragmentParam
            .ISIMMERSEPADDINGTOP_DEF_VALUE, ActivityParam.ISSKIN_DEF_VALUE, ActivityParam.ISROLE_DEF_VALUE};

    private View     toolBarView;           //自定义的toolBar的布局
    private TextView toolbarTitleView;       //标题 居中
    private TextView toolbarLeftBtn;        //最左侧预制按钮，一般防止返回
    private TextView toolbarRightBtn;        //最右侧预制按钮

    private View containView = null; //添加toolbar之后的最终view容器

    private IDynamicNewView mIDynamicNewView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        injectFragmentParam();
        if (fragmentParam[INDEX_ISSKIN]) {
            try {
                mIDynamicNewView = (IDynamicNewView) context;
            } catch (ClassCastException e) {
                mIDynamicNewView = null;
            }
        }
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        if (mIDynamicNewView == null) {
            throw new RuntimeException("IDynamicNewView should be implements !");
        } else {
            mIDynamicNewView.dynamicAddView(view, pDAttrs);
        }
    }

    public void dynamicAddSkinView(View view, String attrName, int attrValueResId) {
        List<DynamicAttr> pDAttrs = new ArrayList<>();
        pDAttrs.add(new DynamicAttr(attrName, attrValueResId));
        dynamicAddView(view, pDAttrs);
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        LayoutInflater result = getActivity().getLayoutInflater();
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*在ViewPager切换过程中会重新调用onCreateView，此时如果实例化过，需要移除，会自动再次添加*/
        if (containView == null) {
            rootView = inflater.inflate(getRootViewLayId(), null);
            //            injectFragmentParam();//注入
            injectZClick();

            //添加toolbar
            if (fragmentParam[INDEX_ISSHOWTOOLBAR]) {
                containView = initToolBar(rootView);
                if (fragmentParam[INDEX_ISSHOWRETURN]) {
                    setToolbarLeftBtnText(" ");
                    setToolbarLeftBtnCompoundDrawableLeft(R.drawable.gui_icon_arrow_back);
                }
            } else {
                containView = rootView;
            }

            createView(savedInstanceState);
            isPrepared = true;
            onPreLoad(savedInstanceState);
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(containView);
            }
        }

        injectRole();
        return containView;
    }


    @Override
    protected void createView(@Nullable Bundle savedInstanceState) {
        super.createView(savedInstanceState);
    }

    /**
     * 注解注入值获取
     */
    private void injectFragmentParam() {
        FragmentParam requestParamsUrl = getClass().getAnnotation(FragmentParam.class);
        if (requestParamsUrl != null) {
            fragmentParam[INDEX_ISSHOWTOOLBAR] = requestParamsUrl.isShowToolBar();
            fragmentParam[INDEX_ISSHOWRETURN] = requestParamsUrl.isShowReturn();
            fragmentParam[INDEX_ISIMMERSEPADDINGTOP] = requestParamsUrl.isImmersePaddingTop();
            fragmentParam[INDEX_ISSKIN] = requestParamsUrl.isSkin();
            fragmentParam[INDEX_ISROLE] = requestParamsUrl.isRole();
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
                    view.setOnClickListener(v -> {
                        try {
                            if (isHasParam) {
                                method.invoke(BaseFragment.this, view);
                            } else {
                                method.invoke(BaseFragment.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private void injectRole() {
        if (fragmentParam[INDEX_ISROLE]) {
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

    protected ViewGroup initToolBar(View userView) {
        /*将toolbar引入到父容器中*/
        View toolbarLay = LayoutInflater.from(mActivity).inflate(R.layout.tsf_view_base_toolbar, null);
        Toolbar toolbar = toolbarLay.findViewById(R.id.id_tool_bar);
        if (fragmentParam[INDEX_ISIMMERSEPADDINGTOP]) {
            int statusBarHeight = ScreenUtil.getStatusBarHeight(mActivity);
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            toolbar.getLayoutParams().height += statusBarHeight;
        }
        toolBarView = LayoutInflater.from(mActivity).inflate(getToolBarLayout() == 0 ? R.layout.tsf_view_base_toolbar_baseview : getToolBarLayout(), toolbar);
        toolbarTitleView = toolBarView.findViewById(R.id.toolbar_title);
        toolbarLeftBtn = toolBarView.findViewById(R.id.toolbar_btn_left);
        toolbarRightBtn = toolBarView.findViewById(R.id.toolbar_btn_right);
        toolbarTitleView.setVisibility(View.GONE);
        toolbarLeftBtn.setVisibility(View.GONE);
        toolbarRightBtn.setVisibility(View.GONE);
        BaseClickListener clickListener = new BaseClickListener();
        toolbarLeftBtn.setOnClickListener(clickListener);
        toolbarRightBtn.setOnClickListener(clickListener);

        if (fragmentParam[INDEX_ISSKIN]) {
            dynamicAddSkinView(toolbar, AttrFactory.BACKGROUND, R.color.colorPrimary);
            //            dynamicAddSkinView(toolbarTitleView, AttrFactory.STYLE, R.style.TextStyle_TCPrimary_Big);
            dynamicAddSkinView(toolbarTitleView, AttrFactory.TEXT_COLOR, R.color.toolbarTitleTextColor);
            dynamicAddSkinView(toolbarLeftBtn, AttrFactory.BACKGROUND, R.color.toolbarLeftBackground);
            //            dynamicAddSkinView(toolbarLeftBtn, AttrFactory.STYLE, R.style.TextStyle_TCPrimary_Small);
            dynamicAddSkinView(toolbarLeftBtn, AttrFactory.TEXT_COLOR, R.color.toolbarLeftTextColor);
            dynamicAddSkinView(toolbarLeftBtn, AttrFactory.DRAWABLE_LEFT, 0);
            dynamicAddSkinView(toolbarRightBtn, AttrFactory.BACKGROUND, R.color.toolbarRightBackground);
            //           dynamicAddSkinView(toolbarRightBtn, AttrFactory.STYLE, R.style.TextStyle_TCSubPrimary_Small);
            dynamicAddSkinView(toolbarRightBtn, AttrFactory.TEXT_COLOR, R.color.toolbarRightTextColor);
            dynamicAddSkinView(toolbarRightBtn, AttrFactory.DRAWABLE_LEFT, 0);
        }

        /*直接创建一个布局，作为视图容器的父容器*/
        ViewGroup contentView;
        contentView = new LinearLayout(mActivity);
        ((LinearLayout) contentView).setOrientation(LinearLayout.VERTICAL);
        //不明原因导致布局向右移动了一些，移动回来
        LinearLayout.LayoutParams layParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layParam.leftMargin = DisplayUtil.dip2px(mActivity, -10);
        contentView.addView(toolbarLay, layParam);
        contentView.addView(userView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return contentView;
    }


    /**
     * 可以自己扩展Layout，但是其扩展的Layout里必须包含现在所有的控件Id，也就是可以增加控件不可以移除控件
     */
    public @LayoutRes
    int getToolBarLayout() {
        return 0;
    }

    /**
     * 获取toobarview的高度
     */
    public int getToolbarViewHeight() {
        return toolBarView.getHeight();
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
        if (fragmentParam[INDEX_ISSHOWRETURN]) {
            mActivity.finish();
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
