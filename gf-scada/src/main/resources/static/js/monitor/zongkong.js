//=================================================================================================
//  该代码应该被用于SCADA客户端程序的layout.html中。
//  @author fan
//  @date   2021年11月25日17:19:00
//=================================================================================================


var black = "#2f4050";      //  黑色
var green = "#41CD42";      //  运行绿色
var red = "#EE1A1A";        //  急停红色
var blue = "#4095E3";       //  堵塞蓝色
var orange = "#ffab00";     //  手动橙色
var yellow = "#fbfb00";     //  故障黄色
var gray = "#DDDDDD";       //  停止灰色
var littegreen = "#BCFFC7";   //  休眠绿色


/**
 * websocket连接url
 * @type {string}
 */
var TOPIC_JK_WEBSOCKET_SERVER = "/websocket/jktest";

var JK_STATUS_WEBSOCKET = "/jk/status";

var JK_ERRINFO = "/jk/errinfo";

var TOPIC_fk_Connect = "/connect/status";
var bailunName=[];//基础数据数组
var bailunjson={};//摆轮基础数据json字符串
/**
 *  连接对象
 */
var stompClientjk;
/**
 *  不知道是什么但是需要
 */
var socketjk;

var lineName_t;
var typeDesc_t;
var faultDesc_t;
var deviceNo_t;
var releasetime_t;
var faultActive_t;

var eventQueryName = eventQueryName;

var lineName = lineNo;

var connected = false;

/**
 *  重新连接的定时任务。
 */
var reconnInv = null;

$(window).ready(function () {
    initWebsocket();
    // 解决session失效问题
    setInterval(function () {
        var image = new Image();
        image.src = "/uploads/gf.png";
        delete image;
    }, 300000);
})

/**
 *  初始化Websocket连接，与服务器建立连接。
 */
function initWebsocket() {
    socketjk = new SockJS(TOPIC_JK_WEBSOCKET_SERVER);
    stompClientjk = Stomp.over(socketjk);
    stompClientjk.connect({}, successCallback, failedCallback);
}

function successCallback() {
    //  清除定时重连任务
    if (reconnInv != null) {
        clearInterval(reconnInv);
        reconnInv = null;
    }
    //  设置心跳发送接收频率（ms）
    stompClientjk.heartbeat.outgoing = 10000;
    stompClientjk.heartbeat.incoming = 0;
    //  赋值连接状态
    connected = true;
    console.log("jk连接成功");
    stompClientjk.subscribe(JK_STATUS_WEBSOCKET, response301);
    stompClientjk.subscribe(JK_ERRINFO, getPage);
    stompClientjk.subscribe("/linestatus/receive", linestatusReceive);
    stompClientjk.subscribe(TOPIC_fk_Connect, responseConnect);

    if (bailunInfo!=null)
    {
        for (var i=0;i<bailunInfo.length;i++)
        {
            var ccc=bailunInfo[i].proudctId;
            var bbb=bailunInfo[i].deviceId;
            //bailunName["'"+bailunInfo[i]['plc']+"'"]=bailunInfo[i]['deviceId'];
            bailunName.push({[ccc]:bbb});
            bailunjson[ccc]=bbb;
        }
    }
    opeareDiv();
    getPage(errinfo);
}

function failedCallback() {
    console.log("jk连接失败")
    connected = false;
    //  创建定时任务，每隔5秒进行重新连接操作
    if (reconnInv == null)
        reconnInv = setInterval(reconnect, 5000);
}

function responseConnect(data) {
    if (data != null) {
        var con = JSON.parse(data.body);
        for (var item in con) {
            var lineName = bailunjson[item];
            var connectStatus = con[item]
            console.log($(".map").attr("data-f"))
            $(".map").each(function(){
                if($(this).attr("data-f") == lineName){
                    //"#21b921" : "#a1a3a1"
                    $(this).find(".circle").css("background",connectStatus * 1 == 1 ? "#21b921" : "#a1a3a1");
                    return;
                }
            })
            /*var eleName = "Connect-" + lineName;
            var ConEle = document.getElementById(eleName);
            if (ConEle != null) {
                if (connectStatus == 0) {
                    ConEle.style.background = "#ff0000";
                    ConEle.innerHTML = "离线";
                    ConEle.style.color = "#ffffff";

                } else {
                    ConEle.style.background = "#00ff00";
                    ConEle.innerHTML = "在线";
                    ConEle.style.color = "#ffffff";
                    //ConEle.setAttribute("background","#00ff00");
                }
            }
            console.log(ConEle);*/
        }
        //console.log(data.body);
    } else {
        console.log("weikong");
    }
}

function linestatusReceive(data) {
    //线体号
    var id = null;
    //马达或者皮带编号
    var mtNo = 0;
    //皮带状态
    var status = 0;
    var color = "";

    if (data != null) {
        var list = eval(data.body);
        //console.log(list)
        for (var i = 0; i < list.length; i++) {
            id = lineName[list[i].sn];//线体名称   eg:ZC
            mtNo = list[i].trayArea;//皮带号 eg:1

            status = Math.min.apply(null, list[i].statusNew);
            console.log("id=" + id + "-----mtNo=" + mtNo + "-----status=" + status);
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
                case 5://休眠浅绿
                    color = littegreen;
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
            if (mtNo != 0) {

                var ele;
                var elef;
                if (mtNo < 10) {
                    //deviceNo = "0" + mtNo;
                    ele = id + "0" + mtNo;
                    elef = ele + "-1";
                } else {
                    ele = id + mtNo;
                    elef = ele + "-1";
                }
                var dosc = document.getElementById(ele);
                if (dosc != null) {
                    changeColor(dosc, color);
                } else {
                    console.log(ele);
                }
                var doscf = document.getElementById(elef);
                if (doscf != null) {
                    changeColor(doscf, color);
                } else {
                    console.log(elef);
                }
            }
        }
    }
}


/**
 * 断开重连
 */
function reconnect() {
    //  如果没连接上就重新连接
    if (!connected) {
        console.info("正在重新连接服务端:" + TOPIC_JK_WEBSOCKET_SERVER);
        initWebsocket();
    }
}

function btnclick() {
    opeareDiv();
    var cb = document.getElementById("01CB-11");
    var ele = document.getElementById("02CB-11");
    cb.setAttribute("stroke", "#FA321E");
    cb.setAttribute("fill", "#FA321E");
    ele.setAttribute("stroke", "#FA321E");
    ele.setAttribute("fill", "#FA321E");
}

/**
 * 操作DIV重新命名id
 */
function opeareDiv() {
    if (mapNo != null && mapNo != undefined) {
        var maplegth = Object.keys(mapNo).length;
        for (var i = 1; i <= maplegth; i++) {
            var mapname = mapNo[i];
            var substart = mapname.indexOf("-");//SVG图名称定义为1-CA，2-CB这种以切换顺序
            var svglineName = mapname.toString().substring(substart + 1);//取出文件名中线体名称
            var lineNoid = 0;
            if (maplineNo.hasOwnProperty(svglineName))//从CA:1;CB:02这种对象里取线体号值
            {
                lineNoid = maplineNo[svglineName];//根据名称取出线号
                var SvgMainMapDoc = document.getElementsByName(svglineName);//这个名称必须在svg中起名
                if (SvgMainMapDoc != null && SvgMainMapDoc != undefined && SvgMainMapDoc.length > 0) {
                    var children = SvgMainMapDoc[0].children;
                    if (children != null) {
                        for (var j = 0; j < children.length; j++) {
                            var tagname = children[j].tagName;
                            if (tagname == "line" || tagname == "path" || tagname == "ellipse") {
                                var idname = children[j].getAttributeNode("id");
                                if (idname != null && idname != undefined) {
                                    var id = idname.nodeValue;
                                    children[j].setAttribute("id", lineNoid + id);//最后id的形式为线号+id的形式。eg:3ZA05
                                }
                            }
                        }
                    }
                }
            } else {
                console.log(svglineName);
            }
        }
    }
}

/**
 * 线体运行状态返回结果处理。
 * @param {*} data
 */
function response301(data) {
    if (data != null) {
        var lineInfo = lineNoName;
        var ele = eleNameMap;
        var list = eval(data.body);
        for (var i = 0; i < list.length; i++) {
            var lineNo = list[i].lineNo;
            var linename = lineInfo[lineNo];//线体名称
            var type = list[i].deviceType;//线体类别
            var deviceNo = list[i].deviceNo;//设备号
            if (ele.hasOwnProperty(type)) {
                if (type == 0) {
                    if (statusList.length == 5) {
                        scope = statusList[0];
                        var sleepAble = statusList[4];
                        var blockAble = statusList[3];
                        var fjMode = statusList[2];
                        var bdMode = statusList[1];
                        var sleepControl = document.getElementById(lineNo + "SLEEPABLE");
                        var blockControl = document.getElementById(lineNo + "BLOCKABLE");
                        var fjControl = document.getElementById(lineNo + "FJMODE");
                        var bdControl = document.getElementById(lineNo + "BIAODING");
                        if (sleepControl != null) {
                            if (sleepAble == 1) {
                                changeColor(sleepControl, littegreen);
                            } else {
                                changeColor(sleepControl, black);
                            }
                        }
                        if (blockControl != null) {
                            if (blockAble == 1) {
                                changeColor(blockControl, littegreen);
                            } else {
                                changeColor(blockControl, black);
                            }
                        }
                        if (fjControl != null) {
                            if (fjMode == 1) {
                                changeColor(fjControl, littegreen);
                            } else {
                                changeColor(fjControl, black);
                            }
                        }
                        if (bdControl != null) {
                            if (bdMode == 1) {
                                changeColor(bdControl, littegreen);
                            } else {
                                changeColor(bdControl, black);
                            }
                        }
                    } else {
                    }
                } else {
                    var eleHead;
                    if (type == 8) {
                        eleHead = lineNo + linename
                    } else {
                        eleHead = lineNo + ele[type];
                    }
                    var elename;
                    if (deviceNo < 10) {
                        elename = eleHead + "0" + deviceNo;
                    } else {
                        elename = eleHead + deviceNo;
                    }

                    var statusList = list[i].statusList;
                    var scope = Math.min.apply(null, statusList);
                    var elev = document.getElementById(elename);
                    var color = "";
                    if (elev != null) {
                        switch (scope) {
                            case 0:
                                color = "#DDDDDD";
                                break;
                            case 1://禁用
                                color = black;
                                break;
                            case 2://急停红
                                color = red;
                                break;
                            case 3://暂停
                                color = "#ff7166";
                                break;
                            case 4://临时停机
                                color = "#ffb3b2";
                                break;
                            case 5://故障
                                color = yellow;
                                break;
                            case 6://堵塞
                                color = blue;
                                break;
                            case 7://手动
                                color = orange;
                                break;
                            case 8://休眠
                                color = littegreen;
                                break;
                            case 9://停止灰
                                color = gray
                                break;
                            case 10://运行绿
                                color = green;
                                break;
                            case 11://远程 灰
                                color = gray;
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
                                color = "#faf8f6";
                                break;
                            case 16:
                                color = "#faf8f6";
                                break;
                            case 17:
                                color = "#f8faf8";
                                break;
                        }
                        changeColor(elev, color);
                    }
                }
            } else {
                console.log("该类型不存在");
            }
        }
    } else {
        console.log("收到数据为空");
    }
}


/**
 * 切换SVG元素的颜色。
 * @param {*} element
 * @param {*} color
 */
function changeColor(element, color) {
    if (element != null) {
        element.setAttribute("stroke", color);
        //element.setAttribute("fill", color);
        //
        if (color == littegreen) {
            var id = element.id + "-arrow";
            var arrow = document.getElementById(id);
            if (arrow != null) {
                arrow.classList.add("arrow-run");
                arrow.classList.remove("arrow-default");
            }
        } else {
            var id = element.id + "-arrow";
            var arrow = document.getElementById(id);
            if (arrow != null) {
                arrow.classList.add("arrow-default");
                arrow.classList.remove("arrow-run");
            }
        }
    }
}

// 获取异常数据
function getPage(errinfo) {
    if (errinfo != null) {
        var errinfov;
        if (errinfo.hasOwnProperty("length")) {
            console.log("baohan length");
            errinfov = errinfo;
        } else {
            var jbody = JSON.parse(errinfo.body);
            var elist = jbody.list;
            var showerr = JSON.parse(elist);
            errinfov = showerr.errinfo;
        }
        if (errinfov != null && errinfov.length > 0) {
            dataRow = "";
            $.each(errinfov, function (i, r) {
                if (i >= 5) {
                    return false;
                }
                convertFault(r);
                dataRow += '<tr>'
                    + '<td>'
                    + lineName_t
                    + '</td>'
                    + '<td>'
                    + typeDesc_t
                    + '</td><td>'
                    + deviceNo_t + '</td>'
                    + '<td>' + faultDesc_t + '</td><td>' + faultActive_t + '</td><td>' + r.faultTriggerTime + '</td><td>' + releasetime_t + '</td></tr>'
                ;
            });
            $("#dataTable tbody").empty();
            $("#dataTable tbody").append(dataRow);
        }
    }
}

function convertFault(r) {

    //恢复时间设定
    if (r.faultReleaseTime==null||r.faultReleaseTime==undefined)
    {
        releasetime_t="-";
    }else
    {
        releasetime_t=r.faultReleaseTime;
    }

    if (r.lineName==null||r.lineName==undefined)
    {
        lineName_t=r.lineNo;
    }else
    {
        lineName_t=r.lineName;
    }

    if (r.typeDescrib==null||r.typeDescrib==undefined)
    {
        typeDesc_t=r.deviceType;
    }else
    {
        typeDesc_t=r.typeDescrib;
    }

    if(r.devicePhysicalNo==null||r.devicePhysicalNo==undefined)
    {
        deviceNo_t=r.deviceNo;
    }else
    {
        deviceNo_t=r.devicePhysicalNo;
    }


    if (r.faultDescrib==null||r.faultDescrib==undefined)
    {
        faultDesc_t=r.fault;
    }else
    {
        faultDesc_t=r.faultDescrib;
    }

    //状态
    if (r.faultIndex==1)
    {
        faultActive_t="活动";
    }
    else
    {
        faultActive_t="恢复";
    }
}

// function convertFault(r) {
//     if (lineNoName.hasOwnProperty(r.lineNo)) {
//         lineNo_t = lineNoName[r.lineNo];
//     } else {
//         lineNo_t = r.lineNo;
//     }
//     //恢复时间设定
//     if (r.faultReleaseTime == null || r.faultReleaseTime == undefined) {
//         releasetime_t = "-";
//     } else {
//         releasetime_t = r.faultReleaseTime;
//     }
//     //设备类别
//     if (typedescrib.hasOwnProperty(r.deviceType)) {
//         typeDesc_t = typedescrib[r.deviceType];
//         if (typeDesc_t == undefined || !typeDesc_t) {
//             typeDesc_t = r.deviceType;
//         }
//     } else {
//         typeDesc_t = r.deviceType;
//     }
//
//     //异常描述
//     if (errdescrib.hasOwnProperty(r.deviceType)) {
//         faultDesc_t = (errdescrib[r.deviceType])[r.fault];
//         if (faultDesc_t == undefined || !faultDesc_t) {
//             faultDesc_t = r.fault;
//         }
//     } else {
//         faultDesc_t = r.fault;
//     }
//     //状态
//     if (r.faultIndex == 1) {
//         faultActive_t = "活动";
//     } else {
//         faultActive_t = "恢复";
//     }
// }

function getMore() {
    $.modal.open(eventQueryName, "/device/event/errQuery");
}





