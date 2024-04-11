package com.project.shopapp.utils;

import org.springframework.stereotype.Component;

@Component
public class MessageUtils {
    public static final String LOGIN_SUCCESFULLY = "user.login.login_successfully";
    public static final String REGISTER_FAIL = "user.register.register_fail";
    public static final String PHONE_EXIST = "user.register.phone_number_exist";
    public static final String ROLE_NOT_FOUND = "user.register.role_not_found";
    public static final String PERMISSION_DENIED = "user.register.permission_denied";
    public static final String LOGIN_FAIL = "user.login.login_fail";
    public static final String USER_NOT_FOUND = "user.user_not_found";
}
