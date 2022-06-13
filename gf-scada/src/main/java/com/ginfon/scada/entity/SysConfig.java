package com.ginfon.scada.entity;

import java.util.Date;

public class SysConfig {
        private String paramId;

        private String paramValue;

        private String paramNote;

        private String createUser;

        private String updateUser;

        private Date createTime;

        private String updateTime;

        public String getParamId() {
            return paramId;
        }

        public void setParamId(String paramId) {
            this.paramId = paramId;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }

        public String getParamNote() {
            return paramNote;
        }

        public void setParamNote(String paramNote) {
            this.paramNote = paramNote;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
}
