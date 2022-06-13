package com.ginfon.scada.entity;

import java.util.List;

public class CurrentStatusEvent {

    private int parentlineno;//线体号
    private String lineno;//皮带号
    private int limt;//操作状态等级，数字越小操作的状态重要等级越高，状态越少
    private List<Integer> status;//状态数组

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public int getLimt() {
        return limt;
    }

    public void setLimt(int limt) {
        this.limt = limt;
    }

    public String getLineno() {
        return lineno;
    }

    public void setLineno(String lineno) {
        this.lineno = lineno;
    }

    public int getParentlineno() {
        return parentlineno;
    }

    public void setParentlineno(int parentlineno) {
        this.parentlineno = parentlineno;
    }
}
