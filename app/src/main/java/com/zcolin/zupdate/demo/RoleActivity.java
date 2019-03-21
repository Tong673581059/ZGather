package com.zcolin.zupdate.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.telchina.libskin.load.SkinManager;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.zupdate.demo.base.ActivityParam;
import com.zcolin.zupdate.demo.base.BaseActivity;
import com.zcolin.zupdate.demo.role.Role;

// 使用Role必须要在findViewById之后，否则找不到该控件
@ActivityParam(isImmerse = true, isSkin = true, isRole = true)
public class RoleActivity extends BaseActivity {
    @Role(roles = "1")
    public Button mBtn1;
    @Role(roles = "2")
    public Button mBtn2;
    @Role(roles = "3")
    public Button mBtn3;
    @Role(roles = "4")
    public Button mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        setToolbarTitle("权限测试");

        mBtn1 = findViewById(R.id.mBtn1);
        mBtn2 = findViewById(R.id.mBtn2);
        mBtn3 = findViewById(R.id.mBtn3);
        mBtn4 = findViewById(R.id.mBtn4);

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toastShort("正常权限");
            }
        });

        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toastShort("不显示");
            }
        });

        mBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().restoreDefaultTheme();
                ToastUtil.toastShort("默认正常权限");
            }
        });
    }
}
