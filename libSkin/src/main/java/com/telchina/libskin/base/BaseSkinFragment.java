package com.telchina.libskin.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.telchina.libskin.attr.DynamicAttr;
import com.telchina.libskin.listener.IDynamicNewView;
import com.zcolin.frame.app.BaseFrameFrag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdj on 2019/3/19.
 */

public class BaseSkinFragment extends BaseFrameFrag implements IDynamicNewView {
    private  IDynamicNewView mIDynamicNewView;
    private LayoutInflater  mLayoutInflater;

    @Override
    protected int getRootViewLayId() {
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIDynamicNewView = (IDynamicNewView) context;
        } catch (ClassCastException e) {
            mIDynamicNewView = null;
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

    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        LayoutInflater result = getActivity().getLayoutInflater();
        return result;
    }
}
