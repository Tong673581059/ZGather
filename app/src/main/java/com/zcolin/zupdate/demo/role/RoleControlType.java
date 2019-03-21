/*
 * *********************************************************
 *   author   zxt
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-12-20 下午3:27
 * ********************************************************
 */

/*
 * *********************************************************
 *   author   zhuxuetong
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-10-8 下午4:49
 * ********************************************************
 */
package com.zcolin.zupdate.demo.role;

/**
 * 权限控制的类型 三种：
 * 1、ROLE_VISIABLE  正常权限
 * 2、ROLE_GONE      隐藏
 * 3、ROLE_NO_CLICK 正常显示，不可点击
 */
public enum RoleControlType {
    /**
     * 显示 默认
     */
    ROLE_VISIABLE,
    /**
     * 隐藏
     */
    ROLE_GONE,
    
    /**
     * 不可点击
     */
    ROLE_NO_CLICK,
}
