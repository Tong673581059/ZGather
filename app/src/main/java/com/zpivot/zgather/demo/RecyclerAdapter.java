package com.zpivot.zgather.demo;

import android.widget.TextView;

import com.telchina.libskin.attr.AttrFactory;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zpivot.zgather.R;
import com.zpivot.zgather.amodule.base.BaseActivity;

/**
 * Created by sdj on 2019/3/21.
 */

public class RecyclerAdapter extends BaseRecyclerAdapter<RecyclerVIewInfo> {
    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.recycler_itemview;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, RecyclerVIewInfo data) {
        TextView tvId = getView(holder, R.id.tv_id);
        TextView tvName = getView(holder, R.id.tv_name);
        BaseActivity mActivity = (BaseActivity) tvId.getContext();
        mActivity.dynamicAddSkinEnableView(tvId, AttrFactory.TEXT_COLOR, R.color.colorStatus);
        mActivity.dynamicAddSkinEnableView(tvName, AttrFactory.TEXT_COLOR, R.color.colorStatus);
        tvId.setText(data.id);
        tvName.setText(data.name);
    }
}
