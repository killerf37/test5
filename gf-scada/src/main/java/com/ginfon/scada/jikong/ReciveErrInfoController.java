package com.ginfon.scada.jikong;

import com.alibaba.fastjson.JSONObject;
import com.ginfon.core.web.BaseController;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: fan
 * @Date: 2022/03/03/17:00
 * @Description:
 */
@Controller
public class ReciveErrInfoController extends BaseController {

    @MessageMapping("/err/broadcast")
    public void ErrBroadcast(JSONObject request)
    {
        ArrayList<String> errInfo=(ArrayList<String>)request.get("errInfo");
    }
}
