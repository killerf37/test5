var myIVChart = null;
//鼠标移动提示
function mouseMove() {
    document.getElementById('title').title = $.i18n.prop("more.data");
    // document.getElementById('lineTitle').title = $.i18n.prop("lineChart.data");
}

//电流电压信息
function getCurrentAndVoltageInfo() {
    var chat = document.getElementById('currentAndVoltage');
    myIVChart = echarts.init(chat);
    myIVChart.setOption({
        tooltip: {
            formatter: "{a} <br/>{c} {b}"
        },
        series: [
            {
                name: $.i18n.prop("system.current"),
                type: 'gauge',
                center: ['75%', '75%'],    // 默认全局居中
                radius: "110%",
                min: 0,
                max: 60,
                startAngle: 180,
                endAngle: 0,
                splitNumber: 2,
                axisLine: {            // 坐标轴线
                    lineStyle: {       // 属性lineStyle控制线条样式
                        width: 9,
                        color: [[1, '#91c7ae']]
                    }
                },
                axisTick: {            // 坐标轴小标记
                    splitNumber: 5,
                    length: 15,        // 属性length控制线长
                    lineStyle: {        // 属性lineStyle控制线条样式
                        color: 'auto'
                    }
                },
                axisLabel: {
                    formatter: function (v) {
                        switch (v + '') {
                            case '0' :
                                return '0A';
                            case '30' :
                                return "30A";
                            case '60' :
                                return '60A';
                        }
                    }
                },
                splitLine: {           // 分隔线
                    length: 15,         // 属性length控制线长
                    lineStyle: {       // 属性lineStyle
                        color: 'auto'
                    }
                },
                pointer: {
                    width: 2
                },
                title: {
                    show: false
                },
                detail: {               // 仪表盘详情，用于显示数据。
                    show: true,             // 是否显示详情,默认 true。
                    offsetCenter: [0, "30%"],// 相对于仪表盘中心的偏移位置，数组第一项是水平方向的偏移，第二项是垂直方向的偏移。可以是绝对的数值，也可以是相对于仪表盘半径的百分比。
                    textStyle: {
                        fontSize: 20,
                        color: 'black',
                        lineHeight: 20,
                        fontStyle: 'italic'
                    },
                    formatter: $.i18n.prop("system.current"),  // 格式化函数或者字符串
                },
                data: [{value: 25, name: 'A'}]
            },
            {
                name: $.i18n.prop("system.voltage"),
                type: 'gauge',
                center: ['25%', '75%'],    // 默认全局居中
                radius: "110%",
                min: 350,
                max: 450,
                startAngle: 180,
                endAngle: 0,
                splitNumber: 2,
                axisLine: {            // 坐标轴线
                    lineStyle: {       // 属性lineStyle控制线条样式
                        width: 9,
                        color: [[1, '#91c7ae']]
                    }
                },
                axisTick: {            // 坐标轴小标记
                    splitNumber: 5,
                    length: 15,        // 属性length控制线长
                    lineStyle: {        // 属性lineStyle控制线条样式
                        color: 'auto'
                    }
                },
                axisLabel: {
                    formatter: function (v) {
                        switch (v + '') {
                            case '350' :
                                return '350V';
                            case '400' :
                                return '400V';
                            case '450' :
                                return '450V';
                        }
                    }
                },
                splitLine: {           // 分隔线
                    length: 15,         // 属性length控制线长
                    lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                        color: 'auto'
                    }
                },
                pointer: {
                    width: 2
                },
                title: {
                    show: false
                },
                detail: {                   // 仪表盘详情，用于显示数据。
                    show: true,             // 是否显示详情,默认 true。
                    offsetCenter: [0, "30%"],// 相对于仪表盘中心的偏移位置，数组第一项是水平方向的偏移，第二项是垂直方向的偏移。可以是绝对的数值，也可以是相对于仪表盘半径的百分比。
                    textStyle: {
                        fontSize: 20,
                        color: 'black',
                        lineHeight: 20,
                        fontStyle: 'italic'
                    },
                    formatter: $.i18n.prop("system.voltage"),  // 格式化函数或者字符串
                },
                data: [{value: 420, name: 'V'}]
            }
        ]
    });
}
//电压电流图表信息
function openLineChart() {
    layer.open({
        type: 2,
        title: "电压电流图表信息",
        area: ['60%', '80%'],
        fix: true,
        content: '/system/currentVoltage'
    });
}

//事件信息展示

//父线体转译汉字
// function convertCNParent(parentline) {
//     switch (parentline) {
//         case 1:
//             return $.i18n.prop("parent.lineNo1");
//             break;
//         case 2:
//             return $.i18n.prop("parent.lineNo2");
//             break;
//         case 3:
//             return $.i18n.prop("parent.lineNo3");
//             break;
//         case 4:
//             return $.i18n.prop("parent.lineNo4");
//             break;
//         case 5:
//             return $.i18n.prop("parent.lineNo5");
//             break;
//         case 6:
//             return $.i18n.prop("parent.lineNo6");
//             break;
//         case 7:
//             return $.i18n.prop("parent.lineNo7");
//             break;
//         case 8:
//             return $.i18n.prop("parent.lineNo8");
//             break;
//         case 9:
//             return $.i18n.prop("parent.lineNo9");
//             break;
//         case 10:
//             return $.i18n.prop("parent.lineNo10");
//             break;
//         case 11:
//             return $.i18n.prop("parent.lineNo11");
//             break;
//         case 12:
//             return $.i18n.prop("parent.lineNo12");
//             break;
//         default:
//             return "无";
//             break;
//     }
// }


//状态转译汉字
// function convertCNStatus(status) {
//     switch (status) {
//         case -1:
//             return $.i18n.prop("sys.code-1");
//             break;
//         case 0:
//             return $.i18n.prop("sys.code0");
//             break;
//         case 1:
//             return $.i18n.prop("sys.code1");
//             break;
//         default:
//             return "无";
//             break;
//     }
// }

//异常转译汉字
// function convertCNErr(err) {
//     switch (err) {
//         case 0:
//             return $.i18n.prop("scada.status1.1");
//             break;
//         case 1:
//             return $.i18n.prop("scada.status2.1");
//             break;
//         case 2:
//             return $.i18n.prop("scada.status3.1");
//             break;
//         case 3:
//             return $.i18n.prop("scada.status4.1");
//             break;
//         case 4:
//             return $.i18n.prop("scada.status5.1");
//             break;
//         case 5:
//             return $.i18n.prop("scada.status6.1");
//             break;
//         case 6:
//             return $.i18n.prop("scada.status7.1");
//             break;
//         case 7:
//             return $.i18n.prop("scada.status8.1");
//             break;
//         case 8:
//             return $.i18n.prop("scada.status9.1");
//             break;
//         case 9:
//             return $.i18n.prop("scada.status10.1");
//             break;
//         case 10:
//             return $.i18n.prop("scada.status11.1");
//             break;
//         case 11:
//             return $.i18n.prop("scada.status12.1");
//             break;
//         case 12:
//             return $.i18n.prop("scada.status13.1");
//             break;
//         case 13:
//             return $.i18n.prop("scada.status14.1");
//             break;
//         case 14:
//             return $.i18n.prop("scada.status15.1");
//             break;
//         case 15:
//             return $.i18n.prop("scada.status16.1");
//             break;
//
//         default:
//             return $.i18n.prop("system.scada.log.remark");
//             break;
//     }
// }

function loadData() {
    $.post("/scadaList", {}, function (data) {
        getEventReminder(data);
    }, "json");
}

function getEventReminder(data) {
    var str = "";
    for (var i = 0; i < data.length; i++) {
        let parentName = data[i].parentlineName;
        let lineName = data[i].lineName;
        // if(parentName == "NC"){
        //     lineName = lineName*1 + 17;
        // }
        str += "<tr style='text-align: center;'>"
            + "<td style='white-space: nowrap;'>" + parentName + "</td>"
            + "<td style='white-space: nowrap;'>" + lineName + "</td>"
            + "<td style='white-space: nowrap;'>" + data[i].stateId + "</td>"
            + "<td style='white-space: nowrap;'>" + data[i].type + "</td>"
            + "<td style='white-space: nowrap;'>" + data[i].startime + "</td></tr>";
    }
    $("#eventReminderTable").html(str);
    $("#eventReminderTable").parent(".table").find("thead th").css("text-align", "center");
    $("#eventReminderTable").parent().show();
}


//事件信息展示更多数据
function getMore() {
    window.location.href="/device/event/query";
}

//全屏
function openScreen() {
    var element = document.documentElement;
    if (element.requestFullscreen) {
        element.requestFullscreen();
    } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
    } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    }
}

//退出全屏
function closeScreen() {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    }
}

//全屏事件监听
document.addEventListener("fullscreenchange", function (event) {
    if (document.fullscreen === false ){
        $("#openScreen").css("display","block");
        $("#closeScreen").css("display","none");
    }else{
        $("#openScreen").css("display","none");
        $("#closeScreen").css("display","block");
    }
}, false);
document.addEventListener("msfullscreenchange", function (event) {
    if (document.msFullscreenElement === false ){
        $("#openScreen").css("display","block");
        $("closeScreen").css("display","none");
    }else{
        $("#openScreen").css("display","none");
        $("#closeScreen").css("display","block");
    }
}, false);
document.addEventListener("mozfullscreenchange", function (event) {
    if (document.mozFullScreen === false ){
        $("#openScreen").css("display","block");
        $("#closeScreen").css("display","none");
    }else{
        $("#openScreen").css("display","none");
        $("#closeScreen").css("display","block");
    }
}, false);
document.addEventListener("webkitfullscreenchange", function (event) {
    if (document.webkitIsFullScreen === false ){
        $("#openScreen").css("display","block");
        $("#closeScreen").css("display","none");
    }else{
        $("#openScreen").css("display","none");
        $("#closeScreen").css("display","block");
    }
}, false);
//关闭左侧DIV
function closeLeftDiv() {
    var id = null;
    document.getElementById('leftDiv').style.display = "none";
    // document.getElementById('leftDiv').style.overflow = "hidden";
    document.getElementById('closeLeft').style.display = "none";
    document.getElementById('showLeft').style.display = "block";
    $("#monitorInfo").attr("class", "col-sm-12");
    //Scada布局调整
    for (var i = 1; i <= layerFlag; i++) {
        id = "Layer" + i
        document.getElementById(id).style.width = "82%";
    }
}

//打开左侧DIV
function openLeftDiv() {
    var id = null;
    document.getElementById('leftDiv').style.display = "block";
    //myIVChart.resize();
    document.getElementById('showLeft').style.display = "none";
    document.getElementById('closeLeft').style.display = "block";
    $("#monitorInfo").attr("class", "col-sm-9");
    for (var i = 1; i <= layerFlag; i++) {
        id = "Layer" + i
        document.getElementById(id).style.removeProperty("width");
    }
    resize();
}


//环线运行时间计算 参数：环线启动时间
function loopRunTime() {
    var nowTime = new Date().getTime();
    var sec = (nowTime - loopStartTime) / 1000;
    var result = '';
    if (sec < 0) {
        sec = sec * -1;
    }
    result = {
        'day': sec / 24 / 3600,
        'hours': sec / 3600 % 24,
        'minutes': sec / 60 % 60,
        'seconds': sec % 60
    }
    var seconds = Math.floor(result.seconds),
        minutes = Math.floor(result.minutes),
        hours = Math.floor(result.hours),
        day = Math.floor(result.day)
    var text = '环线运行时间：' + day + ' 天 ' + hours + ' 小时 ' + minutes + ' 分钟 ' + seconds + ' 秒';
    document.getElementById('loopId').innerHTML = text;
}

//放在开机时间订阅的下面
//setInterval(loopRunTime, 1);

$(window).ready(function () {
    mouseMove();
    // getCurrentAndVoltageInfo();
    loadData();
    setInterval(loadData,5000);
});



//页面模块自适应
// window.addEventListener("resize", function() {
//     myIVChart.resize();
// });
