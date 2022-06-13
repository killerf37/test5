package com.ginfon.core.web.model;

import com.ginfon.core.model.Query;

/**
 * @Author James
 */
public class UserQuery extends Query {
    private String username;

    private String name;

    private String account;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
