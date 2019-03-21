package com.zcolin.zupdate.demo;

import android.content.Context;
import android.widget.TextView;

import com.telchina.libskin.attr.AttrFactory;
import com.telchina.libskin.load.SkinInflaterFactory;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.zupdate.demo.base.BaseSkinActivity;

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
        BaseSkinActivity mActivity = (BaseSkinActivity) tvId.getContext();
        mActivity.dynamicAddSkinEnableView(tvId, AttrFactory.TEXT_COLOR, R.color.colorStatus);
        mActivity.dynamicAddSkinEnableView(tvName, AttrFactory.TEXT_COLOR, R.color.colorStatus);
        tvId.setText(data.id);
        tvName.setText(data.name);
    }
}
