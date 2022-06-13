package com.ginfon.scada.web;

import com.ginfon.core.web.BaseController;
import com.ginfon.scada.entity.CurrentVoltage;
import com.ginfon.scada.service.ICurrentVoltageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CurrentVoltageController extends BaseController {
    @Autowired
    private ICurrentVoltageService currentVoltageService;

    @RequestMapping("/system/currentVoltage")
    public String info(){
        return "mes/currentVoltageLineChart";
    }

    @RequestMapping("/system/currentVoltage/info")
    @ResponseBody
    public List<CurrentVoltage> getCurrentVoltageInfo(CurrentVoltage currentVoltage){
        List<CurrentVoltage> list = currentVoltageService.selectCurrentVoltageIn24Hours(currentVoltage);
        return list;
    }
}
