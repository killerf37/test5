//=================================================================================================
//  该代码应该被用于SCADA客户端程序的main.html中。
//  @author Mark
//  @date   2020年11月18日11:08:00
//=================================================================================================




//=================================================================================================
//  1、请求和订阅的URL定义
//=================================================================================================
/**
 * Websocket服务端URL。
 */
var TOPIC_URL_WEBSOCKET_SERVER = "/websocket/scada";


/**
 * 	WCS和金峰的连接状态的请求URL。
 */
var REQUEST_URL_FOR_SERVER_CONN_STATUS = "/request/serverconn/status";
/**
 * 	WCS和金峰的连接状态的推送URL。
 */
var TOPIC_URL_FOR_SERVER_CONN_STATUS = "/topic/serverconn/status";
    
/**
 * 	控制线体启停状态的请求URL，对应功能码103。
 */
var REQUEST_URL_FOR_CONTROL_LINE_DEVICE = "/request/control/linedevice";
    
/**
 * 	设置堵包功能时间的请求URL，对应功能码203。
 */
var REQUEST_URL_FOR_CONTROL_BLOCKING = "/request/control/blocking";
    
/**
 * 	请求堵包功能启用状态的URL，对应功能码202。
 */
var REQUEST_URL_FOR_BLOCKING_STATUS = "/request/blocking/status";
    
/**
 * 	请求线体运行状态的URL，对应功能码201。
 */
 var REQUEST_URL_FOR_LING_STATUS = "/request/line/status";
/**
 * 	设置线体的休眠时间，对应功能码205。
 */
var REQUEST_URL_FOR_SET_SLEEP_TIME = "/request/control/line/sleeptime";
    
/**
 * 	设置线体的启动间隔时间，对应功能码206。
 */
var REQUEST_URL_FOR_SET_START_TIME = "/request/control/line/starttime";
 /**
 * 	查询已经设定的休眠时长，对应功能码207。
 */
var REQUEST_URL_FOR_SLEEP_TIME = "/request/line/sleeptime";
    
/**
 * 	查询已经设定的启动时长，对应功能码208。
 */
var REQUEST_URL_FOR_START_TIME = "/request/line/starttime";
    
/**
 * 	线体运行状态的推送URL。对应功能码301。
 */
 var TOPIC_URL_FOR_LINE_STATUS = "/topic/line/status";
    
/**
 * 	堵包功能状态的推送URL。对应功能码204。
 */
var TOPIC_URL_FOR_BLOKCING_STATUS = "/topic/blocking/status";

/**
 * 	对应功能码209。
 */
var TOPIC_URL_FOR_SLEEP_TIME = "/topic/line/sleeptime";
    
/**
 * 	对应功能码210。
 */
var TOPIC_URL_FOR_START_TIME = "/topic/line/starttime";

/**
 *  异常消息的推送URL。
 */
var TOPIC_URL_FOR_SCADA_ERROR = "/topic/scada/error";

/**
 *  控制类型的请求的URL。功能代码211
 */
var REQUEST_URL_FOR_CONTROL_COMMAND = "/request/control/command";



//=================================================================================================
//  2、Websocket连接的一些参数
//=================================================================================================

/**
 *  输送线连接对象
 */
var stompClient;
/**
 *  不知道是什么但是需要
 */
var socket;

/**
 *  重新连接的定时任务。
 */
var reconnInv = null;

/**
 *  请求数据的定时任务。
 */
var requestInv = null;

/**
 *  不知道是什么但是需要
 */
var timeout = null;
/**
 *  是否连接上服务器
 */
var connected = false;

/**
 *  登陆用户的角色，这影响到一些功能是否能够使用。
 */
var roles = roles;



//=================================================================================================
//  3、线体设备的定义，这部分的值不在该代码文件中，这些值由框架赋予HTML文件，当该代码运行于HTML中时便可以取到值。
//=================================================================================================
/**
 *  输送线和皮带线的定义，其结构为：[1:{1,2,3},2:{1,2,3}]
 */
var linevar = allLine;

/**
 *  输送线名称和其编号的对应关系定义，其结构为：[1:JB,2:CA,3:CB]
 */
var lineName = lineNo;

/**
 *  图纸的数量，为整数类型。
 */
var layerFlag = layerFlag;



//=================================================================================================
//  4、SCADA展示部分的定义参数——颜色。
//=================================================================================================
var black = "#2f4050";      //  黑色
var green = "#41CD42";      //  运行绿色
var red = "#EE1A1A";        //  急停红色
var blue = "#4095E3";       //  堵塞蓝色
var orange = "#ffab00";     //  手动橙色
var yellow = "#fbfb00";     //  故障黄色
var gray = "#DDDDDD";       //  停止灰色
var sleepGreen="#BCFFC7";   //  休眠绿色


//=================================================================================================
//  5、连接函数和业务功能函数的实现。
//=================================================================================================
//
/**
 *  初始化Websocket连接，与服务器建立连接。
 */
function initWebsocket() {
    socket = new SockJS(TOPIC_URL_WEBSOCKET_SERVER);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, successCallback, failedCallback);
}


/**
 * 连接成功回调方法
 */
function successCallback() {
    //  清除定时重连任务
    if(reconnInv != null){
        clearInterval(reconnInv);
        reconnInv = null;
    }
    //  设置心跳发送接收频率（ms）
    stompClient.heartbeat.outgoing = 10000;
    stompClient.heartbeat.incoming = 0;
    //  赋值连接状态
    connected = true;
    //  创建定时请求数据的任务
    requestInv = setInterval(initScadaStatus, 5000);
    //  订阅数据推送

    //  订阅WCS和金峰云连接状态的推送。
    stompClient.subscribe(TOPIC_URL_FOR_SERVER_CONN_STATUS, responseServerConnStatus);
    //  订阅线体状态推送-301
    stompClient.subscribe(TOPIC_URL_FOR_LINE_STATUS, response301);
    //  订阅堵包功能状态推送-204
    stompClient.subscribe(TOPIC_URL_FOR_BLOKCING_STATUS, response204);
    //  订阅异常消息推送
    stompClient.subscribe(TOPIC_URL_FOR_SCADA_ERROR, errorMessage);
    //  订阅线体睡眠时间报文
    stompClient.subscribe(TOPIC_URL_FOR_SLEEP_TIME, responseStartSleep);
    //  订阅线体启动时间报文
    stompClient.subscribe(TOPIC_URL_FOR_START_TIME, responseStartSleep);
    //  初始化SCADA画面，请求相关数据。
    //initScadaStatus();
}

/**
 *  连接失败的回调函数
 */
function failedCallback(){
    connected = false;
    //  创建定时任务，每隔5秒进行重新连接操作
    if(reconnInv == null)
        reconnInv = setInterval(reconnect, 5000);
}

/**
 * 断开重连
 */
function reconnect() {
    //  如果没连接上就重新连接
    if(!connected){
        console.info("正在重新连接服务端:" + this.TOPIC_URL_WEBSOCKET_SERVER);
        initWebsocket();
    }
}

/**
 *  初始化Scada参数配置。
 */
function iniScadaConfig() {
    for (var property in linevar){
        var vl = linevar[property];
        if (vl.length > 0){
            for (var i = 0; i < vl.length; i++){
                var doce = property + vl[i];
                var docef = property + vl[i] + "-1";
                var ele = document.getElementById(doce);
                var elef = document.getElementById(docef);
                if (ele != null){
                    changeColor(ele, gray);
                }
                if (elef != null){
                    changeColor(elef, gray);
                }
            }
        }
    }
}

/**
 *  重设所有设备为默认的状态，也就是图像上变为黑色。
 */
function resetStatus() {
    $("#comStatus1").css("color", black);
}

/**
 *  向服务端请求数据以更新Scada画面。
 */
function initScadaStatus() {
    //  确认是否连接到服务端
    if (stompClient.connected) {
        //  请求WCS和金峰云的连接状态信息
        requestServerConnStatus();
        //  请求线体运行状态201
        request201();
        //  请求堵包功能状态202
        //request202();

    }else{
        console.info("请求失败！与服务端断开连接！");
        //  清除定时请求的任务
        if(requestInv != null){
            clearInterval(requestInv);
            requestInv = null;
        }
        //  再次发起连接
        failedCallback();
    }
}

/**
 *  请求WCS和金峰云的连接状态信息
 */
function requestServerConnStatus(){
    stompClient.send(REQUEST_URL_FOR_SERVER_CONN_STATUS);
}

/**
 * 连接状态返回结果处理。
 * @param {*} data 
 */
function responseServerConnStatus(data){
    if(data != null){
        var bd = JSON.parse(data.body);
        var wcsc = bd.connectWcs;
        var yunc = bd.connectYun;

        if(wcsc){
            //  将通讯状态设置为绿色
            $("#conStatus1").css("color", green);
            
        }else{
            //  将通讯状态设置为黑色
            $("#conStatus1").css("color", black);
        }
    }
}

function responseStartSleep(data){
    if (null != data){
        var opselct = $("#sortPlanCode");
        var selvaue = opselct.val();

        if (selvaue == null || selvaue == ""){
            return;
        }
        var listdata = eval(data.body);
        var functiontype = listdata[1];
        var list = listdata[0];
        //功能选项
        var selectedoption = $("#functionoption").children('option:selected').val();
        if (selectedoption != null){
            for (var i = 0; i < list.length; i++) {
                if (list[i].sn == selvaue){
                    var clo = document.getElementById("cloggedtimedisplay");
                    if (list[i].cloggedtime != null && list[i].cloggedtime != undefined){
                        if ((selectedoption == "2" && functiontype == 209)){
                            var clotime=list[i].cloggedtime;
                            clo.value="当前休眠时间:"+clotime+"分";
                            if (list[i].status==0)
                            {
                                $("#dubaoStatus").css("color", green);
                            }
                            else if (list[i].status==1)
                            {
                                $("#dubaoStatus").css("color", red);
                            }
                            else
                            {
                                $("#dubaoStatus").css("color", gray);
                            }
                        }else if ((selectedoption == "3"&&functiontype == 210)){
                            var clotime = list[i].cloggedtime;
                            clo.value="当前启动间隔时间:" + clotime + "秒";
                            $("#dubaoStatus").css("color", green);
                        }
                    }
                }
            }
        }
    }
}

/**
 *  请求堵包功能状态。
 */
function request202(){
    var opselect = $("#sortPlanCode");
    var selectedValue = opselect.val();
    if (selectedValue == null || selectedValue == ""){
        return;
    }
    var tearr = Object.keys(lineName);
    stompClient.send(REQUEST_URL_FOR_BLOCKING_STATUS,{},JSON.stringify({
        "lineNo":tearr,"funtype":3
    }));
}

/**
 *  堵包状态返回结果处理。
 * @param {*} data 
 */
function response204(data){
    if(data != null){
        var opselect = $("#sortPlanCode");
        var selectedValue = opselect.val();
        if(selectedValue == null || selectedValue == "")
            return;
        var list = eval(data.body);
        var selectedoption = $("#functionoption").children('option:selected').val();
        for (var i = 0; i < list.length; i++) {
            var sn = list[i].sn;
            if (list[i].sn == selectedValue){
                if (list[i].status == 0){
                    //启用堵包
                    $("#dubaoStatus").css("color", green);
                }else if(list[i].status == 1){
                    $("#dubaoStatus").css("color", red);
                }else{
                    $("#dubaoStatus").css("color", gray);
                }
                if (selectedoption != null && selectedoption == "1"){
                    var clo = document.getElementById("cloggedtimedisplay");
                    if (list[i].cloggedtime != null && list[i].cloggedtime != undefined){
                        var clotime = list[i].cloggedtime;
                        clo.value = "当前堵包时间:" + clotime + "秒";
                    }
                }
            }
        }
        /*
        for (var i = 0;i < list.length;i++){
            if(list[i].sn == selectedValue){
                $("cloggedtimedisplay").val(list[i].cloggedtime);
                if (list[i].status==1){
                    $("#dubaoStatus").css("color", black);
                }else{
                    $("#dubaoStatus").css("color", green);
                }
            }
        }
        */

    }
}

/**
 * 禁止堵包功能。
 */
// function request203_stop(){
//     var opselect = $("#sortPlanCode");
//     var selectedValue = opselect.val();
//     if (selectedValue == null || selectedValue == ""){
//         alert("未选择线体!")
//         return;
//     }
//     stompClient.send(REQUEST_URL_FOR_CONTROL_BLOCKING,{},JSON.stringify({
//         "lineNo":selectedValue,"funtype":1,"time":0
//     }));
//     alert("指令已发送");
// }

/**
 * 启用堵包功能。
 */
// function request203_start(){
//     var opselect = $("#sortPlanCode");
//     var selectedValue = opselect.val();
//     if (selectedValue == null || selectedValue == ""){
//         alert("未选择线体!")
//         return;
//     }
//     var settime = $("#cloggedtime");
//     var timeValue = settime.val();
//
//     if (!(/(^[1-9]\d*$)/.test(timeValue)) || timeValue=="" || timeValue==null){
//         alert("输入时间格式不正确，请输入正整数");
//         return;
//     }
//
//     stompClient.send(REQUEST_URL_FOR_CONTROL_BLOCKING,{},JSON.stringify({
//         "lineNo":selectedValue,"funtype":2,"time":timeValue
//     }));
//     alert("指令已发送");
// }


/**
 *  请求线体运行状态。
 */
function request201(){
    stompClient.send(REQUEST_URL_FOR_LING_STATUS, {}, JSON.stringify({"lineNo": "all","deviceNo": "all"}));
}

/**
 *	控制线体启动或者停止。
 */
function request103(funtype) {
    var opselect = $("#sortPlanCode");
    var selectedValue = opselect.val();
    if (selectedValue == null || selectedValue == ""){
        alert("未选择线体！")
        return;
    }
    var linetype = lineName[selectedValue];//线体类别
    var lineNumber = linevar[linetype];//线体号数组
    var flag = false;
    var ty = 0;
    if (funtype == "start"){
        ty = 1;
    }else{
        ty = 2;
    }
    for (var i = 0;i < lineNumber.length; i++){
        var wline = twoWord(lineNumber[i]);
        var eleline = linetype + wline+"-1";
        var ele = document.getElementById(eleline);
        if (ele!=null)
        {
            var colour =ele.getAttributeNode("fill").nodeValue;
            if (ty==1)
            {
                if (colour == "#DDDDDD"
                    || colour == red
                    || colour == blue
                    || colour == yellow
                    || colour == black
                    || colour == green
                    || colour == orange)
                {
                    flag = true;
                    break;
                }
            }
        }

    }
    if (flag){
        alert("状态不允许启动");
        return;
    }


    if (window.confirm("确认发送启停指令吗")) {
        stompClient.send(REQUEST_URL_FOR_CONTROL_LINE_DEVICE, {}, JSON.stringify({
            "lineNo": selectedValue, "funtype": ty, "time": 0
        }));
        alert("指令发送成功");
    }
}

/**
 * 线体运行状态返回结果处理。
 * @param {*} data 
 */
function response301(data){
    //线体号
    var id = null;
    //马达或者皮带编号
    var mtNo = 0;
    //皮带状态
    var status = 0;
    var color = "";

    if(data != null){
        var list = eval(data.body);
        console.log(list)
        for(var i = 0; i < list.length; i++){
            id = lineName[list[i].sn];//线体名称   eg:ZC
            mtNo = list[i].trayArea;//皮带号 eg:1
            status = Math.min.apply(null, list[i].statusNew);
            console.log("id="+id+"-----mtNo="+mtNo+"-----status="+status);
            switch (status) {
                case 0:
                    color = "#DDDDDD";
                    break;
                case 1://急停红
                    color = red;
                    break;
                case 2://过载黄
                    color = yellow;
                    break;
                case 3://堵包蓝
                    color = blue;
                    break;
                case 4://本地橙
                    color = orange;
                    break;
                case 5://休眠绿
                    color = sleepGreen;
                    break;
                case 6://停止灰
                    color = gray;
                    break;
                case 7://运行绿
                    color = green;
                    break;
                case 8://远程 灰
                    color = gray;
                    break;
                case 9://复位 灰
                    color = gray;
                    break;
                case 10:
                    color = "#faf8f6";
                    break;
                case 11:
                    color = "#faf8f6";
                    break;
                case 12:
                    color = "#faf8f6";
                    break;
                case 13:
                    color = "#faf8f6";
                    break;
                case 14:
                    color = "#faf8f6";
                    break;
                case 15:
                    color = "#f8faf8";
                    break;
                case 16:
                    color = "#fafafa";
                    break;
                case 17:
                    color = "#fafafa";
                    break;
            }
            if(mtNo != 0){

                var ele;
                var elef;
                if (mtNo < 10)
                {
                    //deviceNo = "0" + mtNo;
                    ele=id+"0"+mtNo;
                    elef=ele+"-1";
                }
                else
                {
                    ele=id+mtNo;
                    elef=ele+"-1";
                }
                var dosc = document.getElementById(ele);
                if (dosc != null){
                    changeColor(dosc, color);
                }else{
                    console.log(ele);
                }
                var doscf = document.getElementById(elef);
                if (doscf != null){
                    changeColor(doscf, color);
                }else{
                    console.log(elef);
                }
            }

        }
    }
}



//=================================================================================================
//  6、页面操作的一些函数实现。
//=================================================================================================

/**
 *  通过下拉选单切换图纸。
 */
$("#layerLevel").change(function () {
    var selectedValue = $(this).children('option:selected').val();
    /*
    if(selectedValue == "总图"){
        for(i = 0; i < optionval.length; i++){
            var temp = "Line-" + optionval[i];
            var element = document.getElementById(temp);
            if(element != null)
                element.style.opacity = 1;
        }    
    }else{
        var id = "Line-" + selectedValue;
    
        for(i = 0; i < optionval.length; i++){
            var temp = "Line-" + optionval[i];
            var element = document.getElementById(temp);
            if(temp != id && element != null)
                element.style.opacity = 0;
            else if(element != null)
                element.style.opacity = 1;
        }    
    }*/

    /*
    var selectedValue = $(this).children('option:selected').val();
    var num = selectedValue.substring(5);
    //var name = lineName[twoWord(num)];
    var id = "Line-" + name;
    document.getElementById(id);

    
    for(var i = 1; i <= layerFlag; i++){
        var name = lineName[twoWord(i)];
        var id = "Line-" + name;
        var element = document.getElementById(id);
        if(i == num){
            element.style.opacity = 1;
        }else{
            element.style.opacity = 0;
        }
    }
    */

   var id = $(this).children('option:selected').val();
   for (var i = 1; i <= layerFlag; i++) {
       if (id == "Layer" + i) {
           document.getElementById(id).style.display = "block";
       } else {
           document.getElementById("Layer" + i).style.display = "none";
       }
   }
    //  自适应大小
    resize();
});


function allInOne(){
    var opselct = $("#sortPlanCode");
    var selvaue = opselct.val();
    var functiontype = 0;
    if (selvaue == null || selvaue == ""){
        alert("未选择线体!")
        return;
    }
    var settime = $("#cloggedtime");
    var settimeval = settime.val();
    if (!(/(^[1-9]\d*$)/.test(settimeval)) || settimeval == "" || settimeval == null){
        alert("输入时间格式不正确，应输入正整数");
        return;
    }
    //功能选项
    var requestUrl = null;
    var selectedoption = $("#functionoption").children('option:selected').val();
    if (selectedoption != null){
        switch (selectedoption) {
            case "1"://堵包
                requestUrl = REQUEST_URL_FOR_CONTROL_BLOCKING;
                break;
            case "2"://休眠
                requestUrl = REQUEST_URL_FOR_SET_SLEEP_TIME;
                break;
            case "3"://启动
                requestUrl = REQUEST_URL_FOR_SET_START_TIME;
                break;
        }
    }
    else{
        alert("未选择功能!");
        return;
    }
    if (window.confirm("确认发送指令吗")){
        stompClient.send(requestUrl,{},JSON.stringify({"lineNo":selvaue,"time":settimeval,"funtype":0}));
        alert("指令已发送");
    }
}

/**
 *  禁用堵包/休眠/启动这三种功能。
 */
function controlCommand(command){
    var opselct = $("#sortPlanCode");
    var selvaue = opselct.val();
    var functiontype = 20;
    if (selvaue == null || selvaue == ""){
        alert("未选择线体")
        return;
    }
    //功能选项
    var selectedoption =$("#functionoption").children('option:selected').val();
    if (selectedoption != null){
        switch (selectedoption) {
            case "1"://堵包
                functiontype = 1;
                break;
            case "2"://休眠
                functiontype = 2;
                break;
            case "3"://启动
                functiontype = 20;
                break;
        }
    }
    else{
        alert("未选择功能码");
        return;
    }
    if (functiontype == 20){
        alert("请选择正确的功能码！")
        return;
    }
    if(window.confirm("确定发送指令吗？")){
        stompClient.send(REQUEST_URL_FOR_CONTROL_COMMAND,{},JSON.stringify({
            "lineNo":selvaue,"funtype":functiontype,"enable":command
        }));
        alert("指令已发送！");
    }
}

/**
 * 切换SVG元素的颜色。
 * @param {*} element 
 * @param {*} color 
 */
function changeColor(element, color) {
	if(element != null){
        element.setAttribute("stroke", color);
        element.setAttribute("fill", color);
        //  
        if(color == green){
            var id = element.id + "-arrow";
            var arrow = document.getElementById(id);
            if(arrow != null){
                arrow.classList.add("arrow-run");
                arrow.classList.remove("arrow-default");
            }
        }else{
            var id = element.id + "-arrow";
            var arrow = document.getElementById(id);
            if(arrow != null){
                arrow.classList.add("arrow-default");
                arrow.classList.remove("arrow-run");   
            }
        }
    }
}

/**
 *  功能选单发生变化时，触发此函数。
 */
function functionOnChange(){
    var opselct = $("#sortPlanCode");
    if (opselct == null || opselct == undefined){
        return;
    }
    var selvaue = opselct.val();
    if (selvaue == null || selvaue == ""){
        return;
    }

    var requestUrl = null;
    var selectedoption = $("#functionoption").children('option:selected').val();
    if (selectedoption != null){
        var inputime = document.getElementById("cloggedtime");
        var displaytime = document.getElementById("cloggedtimedisplay");
        switch (selectedoption) {
            case "1"://堵包
                requestUrl = REQUEST_URL_FOR_BLOCKING_STATUS;
                inputime.placeholder="设置堵包时长/秒";
                displaytime.placeholder="当前时间";
                break;
            case "2"://休眠
                requestUrl = REQUEST_URL_FOR_SLEEP_TIME;
                inputime.placeholder="设置休眠时长/分";
                displaytime.placeholder="当前时间";
                break;
            case "3"://启动
                requestUrl = REQUEST_URL_FOR_START_TIME;
                inputime.placeholder="设置启动间隔/秒";
                displaytime.placeholder="当前时间";
                break;
        }
    }else
        return;
    var clo = document.getElementById("cloggedtimedisplay");
    clo.value = null;
    var lineofname = lineName[selvaue];
    var linearry = linevar[lineofname];
    stompClient.send(requestUrl,{},JSON.stringify({"lineNo":linearry}));
}



//=================================================================================================
//  7、页面的一些功能函数的实现。
//=================================================================================================



/**
 *  根据后台传来的参数，动态地创建下拉选单可选内容。
 */
function createOptions() {
    // if (optionval.length > 0){
    //     var parenTag = document.getElementById("layerLevel");
    //     for (i = 0; i < optionval.length; i++){
    //         var opname = "Layer" + (i + 1);
    //         parenTag.options.add(new Option(optionval[i], opname));
    //     }
    // }

    var parenTag = document.getElementById("sortPlanCode");
    for(var key in lineName){
        parenTag.appendChild((new Option(lineName[key], key)));
    }
};

/**
 *  布局自适应大小。
 */
function resize() {
    var div_h = $(window).height() - 126;
    if (div_h != 24) {
        for (var i = 1; i <= layerFlag; i++) {
            $("#Layer" + i).children(":first").attr("width", "100%");
            $("#Layer" + i).children(":first").attr("height", div_h);
            $("#Layer" + i).css("padding-top", (0));
        }
    }
}


//=================================================================================================
//  8、监听器函数
//=================================================================================================

/**
 *  页面加载完毕后调用
 */
$(window).ready(function () {
    //  建立连接
    initWebsocket();
    // if (!parent.stompClient.connected)
    // {
    //     parent.stompClient.connect({}, successCallback, failedCallback);
    // }
    //  自适应页面大小
    resize();
    //  
    createOptions();

});

/**
 *  添加对resize事件的监听。
 */
window.addEventListener("resize", function () {
    resize();
});


//=================================================================================================
//  9、其它功能函数
//=================================================================================================

function twoWord(word) {
    if (word.length < 2){
        word = "0" + word;
    }
    return word;
}

/**
 * 异常消息处理函数。
 * @param {*} data 
 */
function errorMessage(data){
    //$.modal.msgError(JSON.parse(data.body).description);
	$.modal.msgError(data.body);
    resetStatus();
}



//=================================================================================================
//  Websocket连接的一些参数
//=================================================================================================



//=================================================================================================
//  Websocket连接的一些参数
//=================================================================================================