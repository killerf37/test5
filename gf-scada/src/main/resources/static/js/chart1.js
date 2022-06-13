$(window).ready(function () {
    mouseMove();
    // getCurrentAndVoltageInfo();
    loadData();
    setInterval(loadData,5000);
});

//鼠标移动提示
function mouseMove() {
    document.getElementById('title').title = $.i18n.prop("more.data");
    // document.getElementById('lineTitle').title = $.i18n.prop("lineChart.data");
}

function loadData() {
    $.post("/scadaList", {}, function (data) {
        getEventReminder(data);
    }, "json");
}

//加载时计算高度和宽度
onload = function () {
    let width = $(".map svg").css("width");
    $(".map svg").css("height",width)
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
function hideTable(){
    // var div_h = $(window).height() - 85;
    // if (div_h != 24) {
    //     $(".thumbnail").attr("width", "100%");
    //     $(".thumbnail").attr("height", div_h);
    //     $(".thumbnail").attr("style", "height:" + div_h + "px;");
    //     $(".thumbnail").css("padding-top", (0));
    //     $("#eventReminder").attr("height", 0);
    //     $("#eventReminder").attr("style", "height:0px;margin-top:10px;");
    //     $(".map svg").css("height",(div_h - 45) / 3 + "px")
    // }
    document.getElementById('eventReminder').style.display = "none";

    let str = '<a id="showTable" onclick="showTable()"><i class="fa fa-chevron-up"></i></a>'
        +' <a id="title" onclick="getMore();"><i class="fa fa-ellipsis-h"></i></a>'
    $("#controllBtn").html(str);
}
function showTable(){
    // var div_h = $(window).height() - 85;
    // if (div_h != 24) {
    //     $(".thumbnail").attr("width", "100%");
    //     $(".thumbnail").attr("height", div_h - 235);
    //     $(".thumbnail").attr("style", "height:" + (div_h - 235) + "px;");
    //     $(".thumbnail").css("padding-top", (0));
    //     $(".map svg").css("height",(div_h - 235) / 3 + "px")
    //     $("#tabledata").attr("height", 235);
    //     $("#tabledata").attr("style", "height:235px;margin-top:10px;");
    // }
    document.getElementById('eventReminder').style.display = "block";

    let str = '<a id="hideTable" onclick="hideTable()"><i class="fa fa-chevron-down"></i></a>'
        +'<a id="title" onclick="getMore();"><i class="fa fa-ellipsis-h"></i></a>';
    $("#controllBtn").html(str);
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
    var div_h = $(window).height()
    $(".map svg").css("height",(div_h - 45) / 3 + "px")
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
    var div_h = $(window).height() - 85
    $(".map svg").css("height",(div_h - 235) / 3 + "px")
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