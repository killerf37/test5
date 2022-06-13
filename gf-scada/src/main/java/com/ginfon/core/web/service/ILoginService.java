package com.ginfon.core.web.service;

import com.ginfon.core.web.entity.User;

/**
 * @Author: James
 * @Date: 2019/8/14 23:33
 * @Description:
 */
public interface ILoginService {
    public User login(String username, String password);
}
