package com.zcolin.zupdate.demo;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;


import com.telchina.libskin.base.BaseSkinActivity;
import com.telchina.libskin.load.SkinManager;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.zupdate.demo.base.ActivityParam;
import com.zcolin.zupdate.demo.base.BaseActivity;

@ActivityParam(isShowToolBar = true, isImmerse = false)
public class SkinDemoActivity extends BaseActivity implements View.OnClickListener {

    private Button btnChangeColor;
    private Button btnChangeSkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_demo);

        btnChangeColor = getView(R.id.btn_change_color);
        btnChangeSkin = getView(R.id.btn_change_skin);

        btnChangeColor.setOnClickListener(this);
        btnChangeSkin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_color:
                SkinManager.getInstance().load("dark");
                break;
            case R.id.btn_change_skin:
                SkinManager.getInstance().load("dark");
                break;
            default:
                break;
        }
    }
}
