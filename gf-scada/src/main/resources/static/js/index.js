$('.menuItem').on('click', menuItem);

function menuItem() {
    // 获取标识数据
    var dataUrl = $(this).attr('href');
    var dataIndex = $(this).data("index");
    if (dataUrl == undefined || $.trim(dataUrl).length == 0) return false;

    $(".menuItem").each(function () {
        $(this).parent().removeClass("active");
        $(this).parent().removeClass("open");
    });

    $(".nav-list>li").each(function () {
        $(this).removeClass("active");
        $(this).removeClass("open");
        $(this).find(".submenu").css("display", "none");
    });

    if (dataUrl == "/system/main") {
        $(this).parent().addClass("active");
    }

    $(this).parent().addClass("active");

    $("#" + dataIndex).parent().addClass("active");
    $("#" + dataIndex).parent().addClass("open");
    $("#" + dataIndex).parent().find(".submenu").css("display", "block");

    // 添加选项卡对应的iframe
    var str1 = '<iframe id="mainFrame" name="mainFrame" allowfullscreen="" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="yes" ' +
        'onload="javascript:this.height = document.documentElement.clientHeight - 50;" width="100%" height="100%" src="' + dataUrl + '" frameborder="0" data-id="' + dataUrl + '" seamless></iframe>';

    $('.main-content').html(str1);
    return false;
}

function changeFrameHeight() {
    var ifm = document.getElementById("mainFrame");
    ifm.height = document.documentElement.clientHeight - 50;
}

window.onresize = function () {
    changeFrameHeight();
}

var TOPIC_COM_WEBSOCKET_SERVER="/websocket/comindex";

/**
 *  输送线连接对象
 */
//var stompClient;
/**
 *  不知道是什么但是需要
 */
//var socket;


/**
 *  连接对象
 */
var stompClientCom;
/**
 *  不知道是什么但是需要
 */
var socketCom;



$(window).ready(function () {
    initWebsocket();
    setInterval(checkAudio,3000);
})

/**
 *  初始化Websocket连接，与服务器建立连接。
 */
function initWebsocket() {
    // socket = new SockJS("/websocket/scada");
    // stompClient = Stomp.over(socket);
    // stompClient.connect({}, lineSuccess, failedCallback);

    socketCom = new SockJS(TOPIC_COM_WEBSOCKET_SERVER);
    stompClientCom = Stomp.over(socketCom);
    stompClientCom.connect({}, successCallback, failedCallback);

    // function initWebsocket() {
    //     socket = new SockJS("/websocket/scada");
    //     stompClient = Stomp.over(socket);
    //     //stompClient.connect({}, successCallback, failedCallback);
    // }
}

/**
 * 连接成功
 */
function successCallback() {
    stompClientCom.subscribe("/audio/baojing",audioErr);
    stompClientCom.subscribe("/audio/line",lineErr);
}

// var lineStatusData;
//
// function lineSuccess() {
//     //  订阅线体状态推送-301
//     stompClient.subscribe("/topic/line/status", linerecive);
// }
//
// function linerecive(data) {
//     lineStatusData=data;
// }

var blflag=false;//摆轮报警标志位
var lineflag=false;//输送线报警标志位
var readflag=0;
var AudioPlayer=new window.SpeechSynthesisUtterance();
AudioPlayer.onstart=function (event) {
    readflag=1;
}
AudioPlayer.onend=function (event) {
    readflag=0;
    audioContent="";
    lineErrContent="";
}
var audioContent="";//摆轮语音播报内容上下文
var lineErrContent="";//输送线语音报警内容
function audioErr(data) {
    if (readflag==0)
    {
        var tfg=JSON.parse(data.body);//获取分表异常信息
        audioContent="";
        for(key in tfg)
        {
            var msgj=tfg[key];//获取内容
            for (ckey in msgj)
            {
                var errContext=msgj[ckey];
                var jsonErr=JSON.parse(errContext);
                var errInfo=jsonErr.errinfo;
                if (errInfo.length>0)
                {
                    for (var k=0;k<errInfo.length;k++)
                    {
                        var lineName1=errInfo[k].lineName;
                        var deviceType1=errInfo[k].typeDescrib;
                        var devicePhysicalNo1=errInfo[k].devicePhysicalNo;
                        var faultDescrib1=errInfo[k].faultDescrib;
                        if (lineName1!=undefined&&deviceType1!=undefined&&devicePhysicalNo1!=undefined&&faultDescrib1!=undefined)
                        {
                            var auErr=lineName1+"号线,"+deviceType1+devicePhysicalNo1+"异常:"+faultDescrib1;
                            audioContent=audioContent+auErr;
                        }
                    }
                }
            }
        }
        blflag=true;
    }
}

function lineErr(data) {
    lineErrContent=data;
    lineflag=true;

}

function checkAudio() {
    var audioVoice="";
    if (blflag||lineflag)
    {
        if (readflag==0)
        {
            if (audioContent!=""||lineErrContent!="")
            {
                audioVoice=audioContent+lineErrContent;
                blflag=false;
                lineflag=false;
                AudioPlayer.lang='zh-CN';
                AudioPlayer.text=audioVoice;
                AudioPlayer.pitch=1.5;
                AudioPlayer.volume=1;
                window.speechSynthesis.speak(AudioPlayer);
            }
        }
    }
}

/**
 * 连接失败
 */
function failedCallback() {

}