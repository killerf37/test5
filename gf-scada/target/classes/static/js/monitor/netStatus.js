
var TOPIC_fk_Connect="/connect/status";

var bailunName=[];//基础数据数组
var bailunjson={};//摆轮基础数据json字符串

$(window).ready(function () {
    // 解决session失效问题
    setInterval(function () {
        var image = new Image();
        image.src = "/uploads/gf.png";
        delete image;
    }, 300000);
    console.log(window.parent.stompClientCom);
    window.parent.stompClientCom.subscribe(TOPIC_fk_Connect,responseConnect);
    if (bailunInfo!=null)
    {
        for (var i=0;i<bailunInfo.length;i++)
        {
            var ccc=bailunInfo[i].plc;
            var bbb=bailunInfo[i].deviceId;
            //bailunName["'"+bailunInfo[i]['plc']+"'"]=bailunInfo[i]['deviceId'];
            bailunName.push({[ccc]:bbb});
            bailunjson[ccc]=bbb;

        }
    }
})

function responseConnect(data)
{
    if (data!=null)
    {
        var con=JSON.parse(data.body);
        for(var item in con)
        {
            var lineName=bailunjson[item];
            var connectStatus=con[item];
            var eleName="Connect-"+lineName;
            var ConEle=document.getElementById(eleName);
            if (ConEle!=null)
            {
                if (connectStatus==0)
                {
                    ConEle.style.background="#ff0000";
                    ConEle.innerHTML="离线";
                    ConEle.style.color="#ffffff";

                }else
                {
                    ConEle.style.background="#00ff00";
                    ConEle.innerHTML="在线";
                    ConEle.style.color="#ffffff";
                    //ConEle.setAttribute("background","#00ff00");
                }
            }
        }

        //console.log(data.body);
    }else
    {
        console.log("weikong");
    }
}
