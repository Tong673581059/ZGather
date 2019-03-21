package com.zcolin.zupdate.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.telchina.libskin.load.SkinManager;
import com.zcolin.frame.util.ActivityUtil;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zupdate.demo.base.ActivityParam;
import com.zcolin.zupdate.demo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

@ActivityParam(isImmerse = true, isSkin = true)
public class SkinDemoActivity extends BaseActivity implements View.OnClickListener {

    private Button          btnChangeColor;
    private Button          btnChangeSkin;
    private Button          btnDefault;
    private ZRecyclerView   recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private DialogDemo dialogDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_demo);

        setToolbarTitle("换肤测试");

        dialogDemo=new DialogDemo(this);

        btnChangeColor = getView(R.id.btn_change_color);
        btnChangeSkin = getView(R.id.btn_change_skin);
        btnDefault = getView(R.id.btn_default);
        recyclerView=getView(R.id.recyclerView);
        List<RecyclerVIewInfo> recyclerVIewInfoList=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            RecyclerVIewInfo recyclerVIewInfo=new RecyclerVIewInfo();
            recyclerVIewInfo.id=String.valueOf(i);
            recyclerVIewInfo.name="数据"+i;
            recyclerVIewInfoList.add(recyclerVIewInfo);
        }
        recyclerAdapter=new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setDatas(recyclerVIewInfoList);

        btnChangeColor.setOnClickListener(this);
        btnChangeSkin.setOnClickListener(this);
        btnDefault.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_color:
                dialogDemo.show();
                break;
            case R.id.btn_change_skin:
                ActivityUtil.startActivity(this, RoleActivity.class);
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
