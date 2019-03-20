package com.zcolin.zupdate.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.telchina.libskin.load.SkinManager;
import com.zcolin.zupdate.demo.base.ActivityParam;
import com.zcolin.zupdate.demo.base.BaseSkinActivity;

@ActivityParam(isImmerse = true, isSkin = true)
public class SkinDemoActivity extends BaseSkinActivity implements View.OnClickListener {

    private Button btnChangeColor;
    private Button btnChangeSkin;
    private Button btnDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_demo);

        setToolbarTitle("换肤测试");

        btnChangeColor = getView(R.id.btn_change_color);
        btnChangeSkin = getView(R.id.btn_change_skin);
        btnDefault = getView(R.id.btn_default);

        btnChangeColor.setOnClickListener(this);
        btnChangeSkin.setOnClickListener(this);
        btnDefault.setOnClickListener(this);
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
            case R.id.btn_default:
                SkinManager.getInstance().restoreDefaultTheme();
                break;
            default:
                break;
        }
    }
}
