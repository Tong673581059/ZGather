# ZGather
## 具有独立功能的，代码提交较小或功能较单一的会放置到此库下。
此项目包含：
#### 1. libUpdate 更新组件，包括更新检测，判断更新种类，显示更新信息，更新下载及安装等。
#### 2. libSkin   换肤管理，主要实现app的颜色、图片的切换
#### 3. 权限管理  主要用户自定义权限，进行对view的三种控制：正常使用，隐藏，显示不可点击（显示，但无权限）
##### dependencies
```
dependencies {
    implementation "com.android.support:appcompat-v7:$supprotVersion"
    implementation "com.android.support:design:$supprotVersion"
    implementation "com.android.support:recyclerview-v7:$supprotVersion"
    implementation "com.github.z-pivot:ZFrame:version"//程序框架
    implementation "com.github.zcolin:ZUILib:version"//UI库
    implementation 'com.github.Tong673581059.ZGather:libUpdate:latest.release'
    implementation 'com.github.Tong673581059.ZGather:libSkin:latest.release'
}
```
##### 使用方法及特别注意
一、自动更新
```
ZUpdate.instance()
       .setUpdateUrl("")
       .setOnlyWifi(false)
       .setUpdateReplyClass(ZDefReply.class)
       .setDownloadSilent(true)
       .checkVersion(mActivity);
```
二、换肤管理libSkin
```
(1) 在build.gradle中添加libSkin的依赖 implementation 'com.github.Tong673581059.ZGather:libSkin:latest.release'
(2) 主题皮肤的res资源文件。默认主题皮肤用默认的res资源文件；一套自定义主题皮肤对应资源文件命名统一以'_skinName'结尾
    例如：默认字体颜色textColor，自定义皮肤的对应命名就是textColor_skinName(skinName是你的自定义主题皮肤名称)
    其他，图片，drawable、style都是如此
(3) 在需要换肤的功能模块对应的xml最外层布局中，添加 xmlns:skin="http://schemas.android.com/android/skin"
    需要换肤或换颜色的view中添加换肤属性，skin:skinEnable="true"
    注意：xml中所用的资源颜色、大小，都要用资源引用的方式 @color/ @dimen/，不要直接写颜色和大小，否则不生效
    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <Button
                android:id="@+id/btn_change_color"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@color/colorPrimary"
                android:text="换颜色"
                android:layout_margin="10dp"
                skin:skinEnable="true"/>
    </LinearLayout>
(4) 在application中，初始化SkinManager
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();
    集成libSkin下BaseSkinActivity、BaseFragment，或根据这两个类将主要代码放到你的通用父类中
    也可以集成demo中的BaseActivity或BaseSkinActivity，需要加入开启换肤的注解（@ActivityParam（isSkin = true））否则无效
(5) 除了XML布局写的控件，自定义的View或者是通过代码添加的控件，需要手动调动添加对应属性的换肤功能
    例如：toolbar dynamicAddSkinEnableView(this, toolbar, AttrFactory.BACKGROUND, R.color.colorPrimary);
    具体的时候可以查看libSkin代码或者demo
(6) 特别注意一下状态栏的颜色设置 如果皮肤管理需要改变状态栏颜色，调用changeStatusColor()
    状态栏对应的颜色命名：colorStatus、colorStatus_skinName，用其他名字不生效
```
三、权限控制
```
(1) 将Demo中的Role文件夹、base文件夹和需要的资源文件复制到项目中
(2) Activity都集成BaseActivity或BaseRoleActivity，并在类头加开启权限控制的注解（@ActivityParam（isRole = true））
    Fragment和Activity一样，只是注解变为（@FragmentParam（isRole = true））
(3) 在你需要权限控制的View上加上权限注解 @Role(roles = "权限字符串")或@Role(roles = {"权限字符串1","权限字符串2"})
    需要权限控制的View必须为public， private和protected都不生效
(3) 根据自己的权限定义及逻辑，重写role/RoleMgr中的hasRoles(String[] roles)的实现，
    返回类型必须为RoleControlType类型，注意当View的权限不存在时，请默认指定一种
    无权限的提示信息，可以自定义，只需修改role/RoleMgr中ROLE_TOAST
```
