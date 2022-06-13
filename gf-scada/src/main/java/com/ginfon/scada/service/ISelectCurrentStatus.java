package com.ginfon.scada.service;

import com.ginfon.scada.entity.CurrentStatus;

import java.util.List;


public interface ISelectCurrentStatus {
    List<CurrentStatus> selectcurrentsta();
    List<CurrentStatus> selectallsta(CurrentStatus cs);
}
