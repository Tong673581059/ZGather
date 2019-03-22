package com.zpivot.zgather.amodule.role;

/**
 * 权限控制
 */

public class RoleMgr {

    public static String ROLE_TOAST = "对不起，您没有权限！";

    public static RoleControlType hasRoles(String[] viewRoles) {
        for (String str1 : viewRoles) {
            switch (str1) {
                case "1":
                    return RoleControlType.ROLE_VISIABLE;
                case "2":
                    return RoleControlType.ROLE_GONE;
                case "3":
                    return RoleControlType.ROLE_NO_CLICK;
                default:
                    return RoleControlType.ROLE_VISIABLE;
            }
        }
        return RoleControlType.ROLE_VISIABLE;
    }


}
