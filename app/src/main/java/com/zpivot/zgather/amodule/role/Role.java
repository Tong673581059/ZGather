package com.zpivot.zgather.amodule.role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
    // value="1,2"
    String[] roles();

    /**
     * 是否拥有权限
     */
    RoleControlType isHasRole() default RoleControlType.ROLE_VISIABLE;
}
