var vChart = null;
var iChart = null;
function toChart() {
    $.ajax({
        type: "POST",
        url: "/system/currentVoltage/info",
        async: false,
        success: function (data) {
            if (data.length > 0) {
                var iAxis = [];
                var vAxis = [];
                var xAxis = [];
                for (var i = 0;i<data.length; i++) {
                    iAxis[i] = data[i].avgCurrent;
                    vAxis[i] = data[i].avgVoltage;
                    xAxis[i] = data[i].times;
                }
                var vchat = document.getElementById('vLineChart');
                var ichat = document.getElementById('cLineChart');
                vChart = echarts.init(vchat);
                iChart = echarts.init(ichat);
                vChart.setOption({
                    title: {
                        text: '电压小时平均值',
                        x: 'center',
                        y: 'top'
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'line' // 'item','shadow'
                        },
                        formatter: '{a0} <br/>{b} <br />{c}' + "V"
                    },
                    xAxis: {
                        boundaryGap: false,
                        type: 'category',
                        data: xAxis,
                        axisLabel: {
                            interval: 0,
                            rotate: 25
                        }, axisLine: {
                            lineStyle: {
                                type: 'solid',
                                color: '#06d6cf',//左边线的颜色
                                width: '1'//坐标线的宽度
                            }
                        }
                    },
                    yAxis: {
                        type: 'value',
                        min: 350,
                        max: 450,
                        splitNumber: 10,
                        axisLine: {
                            lineStyle: {
                                type: 'solid',
                                color: '#06d6cf',//左边线的颜色
                                width: '1'//坐标线的宽度
                            }
                        }
                    },
                    series: [
                        {
                            name: $.i18n.prop("system.voltage"),
                            type: 'line',
                            showSymbol: true,
                            symbol: 'circle',//设定为实心点
                            symbolSize: 5,   //设定实心点的大小
                            hoverAnimation: false,
                            animation: false,
                            data: vAxis
                        }
                    ]
                });

                iChart.setOption({
                    title: {
                        text: '电流小时平均值',
                        x: 'center',
                        y: 'top'
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'line' // 'item','shadow'
                        },
                        formatter: '{a0} <br/>{b} <br />{c}' + "A"
                    },
                    xAxis: {
                        boundaryGap: false,
                        type: 'category',
                        data: xAxis,
                        axisLabel: {
                            interval: 0,
                            rotate: 25
                        }, axisLine: {
                            lineStyle: {
                                type: 'solid',
                                color: '#06d6cf',//左边线的颜色
                                width: '1'//坐标线的宽度
                            }
                        }
                    },
                    yAxis: {
                        type: 'value',
                        min: 0,
                        max: 60,
                        splitNumber: 10,
                        axisLine: {
                            lineStyle: {
                                type: 'solid',
                                color: '#06d6cf',//左边线的颜色
                                width: '1'//坐标线的宽度
                            }
                        }
                    },
                    series: [
                        {
                            name: $.i18n.prop("system.voltage"),
                            type: 'line',
                            showSymbol: true,
                            symbol: 'circle',//设定为实心点
                            symbolSize: 5,   //设定实心点的大小
                            hoverAnimation: false,
                            animation: false,
                            data: iAxis
                        }
                    ]
                });
            }
        }
    });
};
$(window).ready(function () {
   toChart();
});
//页面模块自适应
window.addEventListener("resize", function() {
    vChart.resize();
    iChart.resize();
});
