function getScoreEntities(response) {
    if (response.status == 'failure') {
        return null;
    }
    var data = {};

    if (response.isDataFormatted) {
        /* data coming from database */
        data['fetchTime'] = new Date(response.fetchTime).toLocaleString();
        data['lighthouseResult'] = response.lighthouseResult;
        data['lighthouseMisc'] = response.lighthouseMisc;
        data['url'] = response.url;
        data['score'] = response.lighthouseResult.score;
        data['screenshots'] = response['screenshots'];
        data['finalScreenshot'] = response['finalScreenshot'];
    } else {
        /* data coming directly from server call */
        var result = response.lighthouseResult || response;
        data['score'] = result.categories.performance.score;
        data['fetchTime'] = new Date(response.analysisUTCTimestamp).toLocaleString();
        data['lighthouseResult'] = result.audits;
        data['lighthouseMisc'] = result.lighthouseMisc || result.audits;
        data['url'] = response.finalUrl;
        data['screenshots'] = result.audits['screenshot-thumbnails'].details.items;
        data['finalScreenshot'] = result.audits['final-screenshot'].details;
    }

    data['strategy'] = response.deviceType;
    data['showLoadingExperience'] = false;
    if (response.loadingExperience) {
        data['showLoadingExperience'] = true;
        data['FIRST_CONTENTFUL_PAINT_MS'] = response.loadingExperience.metrics.FIRST_CONTENTFUL_PAINT_MS;
        data['FIRST_INPUT_DELAY_MS'] = response.loadingExperience.metrics.FIRST_INPUT_DELAY_MS;
    }

    if (response.diagnostics) {
        data['diagnostics'] = response.diagnostics;
    }

    return data;
}



/* Formatters */
function isTimeInMiliseconds(value) {
    value = (value+"").replace(/,/g, "");
    if (!isNaN(value)) return false;
    if (value.endsWith("ms")) {
        return true;
    }
    return false;
}
function isTimeInSeconds(value) {
    value = (value+"").replace(/,/g, "");
    if (!isNaN(value)) return false;
    if (value.endsWith("s") && !value.endsWith("ms")) {
        return true;
    }
    return false;
}

function getDifferenceInPercentage(value1, value2) {
    value1 = (value1+"").replace(/,/g, "");
    value2 = (value2+"").replace(/,/g, "");
    if (isTimeInMiliseconds(value1)) {
        if (!isTimeInMiliseconds(value2) && isTimeInSeconds(value2)) {
            value2 = value2+1000;
        }
    } else if (isTimeInSeconds(value1)) {
        if (!isTimeInSeconds(value2) && isTimeInMiliseconds(value2)) {
            value2 = Math.round(value2/1000);
        }
    }
    value1 = parseFloat(value1);
    value2 = parseFloat(value2);
    if (isNaN(value1) || isNaN(value2) || value1 == 0) {
        return false;
    } else {
        return Math.round(((value2-value1)/value1)*100)
    }
}

function getCompareMeta(metric1, metric2) {
    var score1 = parseFloat(metric1.score);
    var score2 = parseFloat(metric2.score);
    var value1 = metric1.displayValue;
    var value2 = metric2.displayValue;
    var improvement = isValueImproved(score1, score2);
    if (getDifferenceInPercentage(value1, value2) == 0) {
        improvement = 0;
    }
    return {
        'differencePercent': getDifferenceInPercentage(value1, value2),
        'isValueImproved': improvement,
        'left': {
            'scale': getScaleFromScore(score1).scale,
        },
        'right': {
            'scale': getScaleFromScore(score2).scale
        }

    };
}

function isValueImproved(score1, score2) {
    if (score2 == score1) {
        return 0;
    } else if (score2 > score1) {
        return 1;
    } else {
        return -1;
    }
}

function timeMiliSecondSanitize(value) {
    var inSeconds = isTimeInSeconds(value);
    value = (value + "").replace(/,/g, "");
    value = parseFloat(value);
    if (!isNaN(value)) {
        if (inSeconds) {
            value = value * 1000;
        }
        return (Math.round(value * 100) / 100);
    } else {
        return value;
    }
}

function timeMiliSecondFormatter(value) {
    value = (value+"").replace(/,/g, "");
    value = parseFloat(value);
    if (!isNaN(value)) {
        if (value < 1000) {
            return (Math.round(value * 100) / 100).toLocaleString() + ' ms';
        } else {
            value = value / 1000;
            return (Math.round(value * 100) / 100).toLocaleString() + ' s';
        }
    } else {
        return value;
    }
}

function bytesFormatter(value) {
    value = (value+"").replace(/,/g, "");
    value = parseFloat(value);
    if (!isNaN(value)) {
        if (value < 1024) {
            return value.toLocaleString() + ' Bytes';
        } else {
            value = value / 1024;
            return Math.round(value).toLocaleString() + ' KB';
        }
    } else {
        return value;
    }
}

function bytesPerSecondFormatter(value) {
    value = (value+"").replace(/,/g, "");
    value = parseFloat(value);
    if (!isNaN(value)) {
        if (value < 1024) {
            return value.toLocaleString() + ' Bps';
        } else if (value > 1024*1024) {
            value = value / (1024*1024);
            return Math.round(value).toLocaleString() + 'Mbps';
        }else {
            value = value / 1024;
            return Math.round(value).toLocaleString() + ' Kbps';
        }
    } else {
        return value;
    }
}

function percentageFormatter(value) {
    value = (value+"").replace(/,/g, "");
    value = parseFloat(value);
    if (isNaN(value)) return value;
    value = value * 100;
    return (Math.round(value));
}

function getScaleFromTime(time) {
    time = (time+"").replace(/,/g, "");
    time = parseFloat(time);
    var data = {
        scale: 'pass',
        time: Math.round(time),
        color: '#675c5c'
    };
    switch (true) {
        case data.time <= 500:
            data.color = "#178239";
            data.scale = 'pass';
            break;
        case data.time <= 1000:
            data.color = "#e67700";
            data.scale = 'average';
            break;
        case data.time > 1000:
            data.color = "#c7221f";
            data.scale = 'fail';
            break;
        default:
            data.color = "#e67700";
            data.scale = 'average';
    }

    return data;
}

function getScaleFromScore(score) {
    score = (score+"").replace(/,/g, "");
    score = parseFloat(score);
    if (score <= 1) {
        score = Math.round(score * 100);
    }

    var data = {
        scale: 'pass',
        score: score,
        color: '#675c5c'
    };
    switch (true) {
        case data.score <= 49:
            data.color = "#c7221f";
            data.scale = 'fail';
            break;
        case data.score <= 89:
            data.color = "#e67700";
            data.scale = 'average';
            break;
        case data.score > 89:
            data.color = "#178239";
            data.scale = 'pass';
            break;
        default:
            data.color = '#675c5c';
            data.scale = 'pass';
    }

    return data;
}

$.addTemplateFormatter({
    TimeMiliSecondFormatter: function (value, template) {
        if (template == "getValue") {
            value = value.value;
        }
        return timeMiliSecondFormatter(value);
    },

    BytesFormatter: function (value, template) {
        if (template == "getValue") {
            value = value.value;
        }
        return bytesFormatter(value);
    },

    BytesPerSecondFormatter: function (value, template) {
        if (template == "getValue") {
            value = value.value;
        }
        return bytesPerSecondFormatter(value);
    },

    NumberFormatter: function (value, template) {
        if (template == "getValue") {
            value = value.value;
        }
        if (isNaN(value)) {
            return value;
        } else {
            value = (value+"").replace(/,/g, "");
            return Number(value).toLocaleString();
        }
    },

    GetValue: function (value, template) {
        return value.value;
    },

    CategoryFormatter: function (value, template) {
        var category = 'slow';
        switch (value) {
            case 'SLOW':
                category = 'slow';
                break;
            case 'AVERAGE':
                category = 'average';
                break;
            case 'FAST':
                category = 'fast';
                break;
            default:
                category = 'fast';

        }

        return 'field-metric ' + category;

    },

    FlexGrowStyleFormatter: function (value, template) {
        if (value == null || isNaN(value)) {
            return '';
        }
        value = parseFloat((value+"").replace(/,/g, ""));
        value = value * 100;
        return 'flex-grow: ' + (Math.round(value)) + ';';
    },

    PercentageStyleFormatter: function (value, template) {
        if (value == null && template == "showHyphenIfNull") {
            return "width: 0;";
        }
        return 'width: ' + percentageFormatter(value) + '%;';
    },

    PercentageFormatter: function (value, template) {
        if (value == null && template == "showHyphenIfNull") {
            return "-";
        }
        return percentageFormatter(value) + "%";
    },

    PercentageFormatterWithoutSign: function (value, template) {
        if (value == null && template == "showHyphenIfNull") {
            return "-";
        }
        return percentageFormatter(value);
    },



});


var getAllIndexes = function(arr, val) {
    var indexes = [], i = -1;
    while ((i = arr.indexOf(val, i+1)) != -1){
        indexes.push(i);
    }
    return indexes;
};

var generateScoreRequests = function(urls, strategy) {
    var generateRequest = function(url, strategy) {
        return $.ajax({
            data: {
                url: url,
                strategy: strategy,
            },
            dataType: 'json',
            method: 'GET',
            url: '/metrics/url/score',
        })
    }

    return urls.map(function (url) {
        return generateRequest(url, strategy);
    });
};

var getScoreData = function(response) {
    var data = {days: [], fullDays:[], scores: {}, id: []};
    $.each(response.reverse(), function (idx, item) {
        var dt = new Date(item.fetchTime);
        data.days.push((dt.getMonth()+1)+"/"+dt.getDate());
        data.fullDays.push(dt.toLocaleDateString());
        data.id.push(item._id);
        Object.keys(item.lighthouseResult).forEach(function (key) {
            if (!data.scores[key]) data.scores[key] = [];
            var itemValue = item.lighthouseResult[key].displayValue;
            if (itemValue) {
                itemValue = timeMiliSecondSanitize(itemValue);
            } else {
                itemValue = Math.round(item.lighthouseResult[key]*100);
            }
            data.scores[key].push(itemValue);
        });

    });
    return data;
}

var isScrolledIntoView = function(elem) {
    var docViewTop = $(window).scrollTop();
    var docViewBottom = docViewTop + $(window).height();
    var elemTop = $(elem).offset().top;
    var elemBottom = elemTop + $(elem).height();
    return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
};




function plotFileTypeChart(data, elem, title, showLegends) {
    if ($(elem).attr('_echarts_instance_')) {
        window.echarts.getInstanceById($(elem).attr('_echarts_instance_')).dispose();
    }
    if (typeof(showLegends) == "undefined") {
        showLegends = true;
    }
    var colorPalette = ['#558ba9','#da70d6','#32cd32','#3d52ed','#b75c45','#ff69b4','#ba55d3','#cd5c5c','#ffa500','#40e0d0','#1e90ff','#ff6347','#7b68ee','#00fa9a','#ffd700','#6699FF','#ff6666','#3cb371','#b8860b','#30e0e0'];
    switch(globalData.colorPalette) {
        case 1:
            colorPalette = ["#003f5c", "#444e86", "#955196", "#dd5182", "#ff6e54", "#ffa600"];
            break;
        case 2:
            colorPalette = ["#004d66", "#41abe1", "#80d4f7", "#00A8A8", "#3D52ED", "#da70d6"];
            break;
        default:
    }

    var myChart = echarts.init($(elem)[0]);

    var option = {
        title : {
            text: title,
            x:'center',
            textStyle: {
                color: '#007bff',
                fontWeight: 400
            }
        },
        color: colorPalette,
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend:
            {
                type: 'scroll',
                orient: 'vertical',
                right: 10,
                top: 20,
                bottom: 20,
                show: showLegends,
                textStyle: {
                    color: '#007bff',
                }
            },
        series: [
            {
                name: title,
                type: 'pie',
                radius : '60%',
                center: ['50%', '60%'],
                label: {
                    formatter: "{b}: {c}",
                },
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                data: data
            }
        ]
    };

    myChart.setOption(option);
}

function EMACalc(mArray,mRange) {
    var k = 2/(mRange + 1);
    var emaArray = [mArray[0]];
    for (var i = 1; i < mArray.length; i++) {
        emaArray.push(Math.round(mArray[i] * k + emaArray[i - 1] * (1 - k)));
    }
    return emaArray;
}

function plotLineChart(data, elem) {
    if ($(elem).attr('_echarts_instance_')) {
        window.echarts.getInstanceById($(elem).attr('_echarts_instance_')).dispose();
    }
    var myChart = echarts.init($(elem)[0]);
    var convertToSec = false;
    if (Math.min(...data.values) > 1000) {
        convertToSec = true;
    }

    var stackName = ((data.competitorValues) ? 'CRO' : 'Standard');
    var option = {
        grid: {
            width: '90%',
            right: '0',
        },
        color: ['#61a0a8', '#a862a5'],
        tooltip: {
            trigger: 'item',
            formatter: function(params) {
                var value = params.value;
                if (convertToSec) {
                    value = (Math.round(value / 10) / 100);
                }
                value = value.toLocaleString();
                return params.name+": "+ value;
            }
        },
        xAxis: {
            type: 'category',
            data: data.days,
            splitLine: {
                show: false
            },
            axisTicks: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#888'
                }
            }
        },
        yAxis: {
            show: false,
            type: 'value',
        },
        series: [{
            name: stackName,
            data: data.values,
            type: 'line',
            stack: stackName,
            smooth: true,
            label: {
                normal: {
                    show: true,
                    position: 'bottom',
                    formatter: function(params) {
                        var value = params.value;
                        if (convertToSec) {
                            value = (Math.round(value / 10) / 100);
                        }
                        value = value.toLocaleString();
                        return value;
                    }
                }
            },
        }

        ]
    };

    if (data.competitorValues) {
        option.series.push({
            data: data.competitorValues,
            name: 'Competitor',
            type: 'line',
            smooth: true,
            label: {
                normal: {
                    show: true,
                    position: 'top',
                    formatter: function(params) {
                        var value = params.value;
                        if (convertToSec) {
                            value = (Math.round(value / 10) / 100);
                        }
                        value = value.toLocaleString();
                        return value;
                    }
                }
            },
        });

        option.legend =  {
            color: '#007bff',
            data:[
                {
                    name: stackName,
                    textStyle: {
                        color: '#007bff'
                    }
                },{
                    name: 'Competitor',
                    textStyle: {
                        color: '#007bff'
                    }
                }],
            orient: 'vertical',
            left: 0,
            top: 20,
        };
    }

    if (globalData.isDisplayEMA && !data.competitorValues) {
        option.series.push({
            data: EMACalc(data.values, 6),
            name: 'EMA',
            type: 'line',
            stack: stackName,
            smooth: true,
            label: {
                normal: {
                    show: true,
                    position: 'top',
                    formatter: function(params) {
                        var value = params.value;
                        if (convertToSec) {
                            value = (Math.round(value / 10) / 100);
                        }
                        value = value.toLocaleString();
                        return value;
                    }
                }
            },
        });
        option.legend =  {
            color: '#007bff',
                data:[
                {
                    name: stackName,
                    textStyle: {
                        color: '#007bff'
                    }
                },{
                    name: 'EMA',
                    textStyle: {
                        color: '#007bff'
                    }
                }],
                orient: 'vertical',
                left: 0,
                top: 20,
        };
    }

    myChart.setOption(option);
}

function plotBarChart(data, elem) {
    if ($(elem).attr('_echarts_instance_')) {
        window.echarts.getInstanceById($(elem).attr('_echarts_instance_')).dispose();
    }
    var myChart = echarts.init($(elem)[0]);
    var formatDataValues = function (values) {
        var data = [];
        $.each(values, function (idx, score) {
            data.push({value: score, itemStyle: {opacity: 1, color: getScaleFromScore(score).color}})
        });
        return data;
    }

    var option = {
        grid: {
            width: '90%',
            height: '90%',
            right: '0',
            bottom: '1',
        },
        color: ['#61a0a8', '#a862a5'],
        tooltip: {
            show: false
        },
        xAxis: {
            show: false,
            type: 'category',
            data: data.days,
            splitLine: {
                show: false
            },
            axisTicks: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#888'
                }
            }
        },
        yAxis: {
            show: false,
            type: 'value',
        },
        series: [{
            data: formatDataValues(data.values),
            type: 'bar',
            barMaxWidth: 32,
            label: {
                normal: {
                    show: true,
                    distance: 0,
                    position: 'insideTop',
                    formatter: function (params) {
                        var value = params.value;
                        value = value.toLocaleString();
                        return value;
                    }
                }
            },
        }

        ]
    };


    option.series.push({
        data: EMACalc(data.values, 6),
        lineStyle: {
            color: '#93eaf4',
        },
        type: 'line',
        markPoint: 'none',
        symbol: 'none',
        smooth: true,
        label: {
            show: false
        },
    });

    var event = "click";
    if (elem == "#header-box") {
        event = "mouseover";
        option.grid.bottom = '5';
    }

    myChart.setOption(option);
    myChart.on(event, function (params) {
        generateReport(globalData.url, globalData.strategy, globalData.apiURL, data.id[params['dataIndex']]);
    });
}

function plotStackedBarChart(data, elem) {
    if ($(elem).attr('_echarts_instance_')) {
        window.echarts.getInstanceById($(elem).attr('_echarts_instance_')).dispose();
    }
    var myChart = echarts.init($(elem)[0]);

    var seriesData = [];
    var axisData = [];
    $.each(data.details.items, function (idx, elem) {
        seriesData.push(Math.round(elem.duration));
        axisData.push(elem.groupLabel);
    });

 //   seriesData = seriesData.reverse();

    var option = {
        color: ["#003f5c", "#444e86", "#955196", "#dd5182", "#ff6e54", "#ffa600"],
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                type : 'line'
            },
            formatter: function (params) {
                var param = params[1];
                return param.name + ": " + timeMiliSecondFormatter(param.value);
            }
        },
        legend: {
            data: axisData.reverse()
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
            height: 260
        },
        xAxis:  {
            type: 'value',
            splitLine: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#888'
                }
            },
            axisLabel: {
                formatter: function (value, index) {
                    return timeMiliSecondFormatter(value);
                }
            }
        },
        yAxis: {
            type: 'category',
            splitLine: {
                show:false
            },
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#888'
                }
            },
            data: axisData,


        },
        series:[
            {
                type: 'bar',
                stack:  'a',
                itemStyle: {
                    normal: {
                        barBorderColor: 'rgba(0,0,0,0.2)',
                        color: 'rgba(0,0,0,0.2)'
                    },
                    emphasis: {
                        barBorderColor: 'rgba(0,0,0,0.2)',
                        color: 'rgba(0,0,0,0.2)'
                    }
                },
                data: seriesData.reduce(function(r,c,i){
                    r.push((r[i-1]+seriesData[i-1] || 0));return r;
                    }, [] ).reverse()
            },
            {
                type: 'bar',
                stack: 'a',
                label: {
                    normal: {
                        show: true,
                        position: 'insideRight',
                        formatter: function (params) {
                            return params.name + ": " + timeMiliSecondFormatter(params.value);
                        }
                    }
                },
                data: seriesData.reverse()
            }
        ]
    };
    myChart.setOption(option);
}


function  plotReportSummary(scoreValue, fetchTime) {
    plotDonutChart(scoreValue);
    var urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get("fetchSource") == "repository") {
        urlParams.delete("fetchSource");
        urlParams.delete("date");
        urlParams.set('rightDate', new Date(fetchTime).toLocaleDateString());
        $(".performance-metrics-url").attr("href", "/metrics.html" + "?" + urlParams.toString());
    } else {
        $(".performance-metrics-url").parent().remove();
    }
}

function plotDonutChart(scoreValue) {
    if ($("#score-chart").attr('_echarts_instance_')) {
        window.echarts.getInstanceById($("#score-chart").attr('_echarts_instance_')).dispose();
    }
    var myChart = echarts.init(document.getElementById('score-chart'));

    var scoreData = getScaleFromScore(scoreValue);

    var color = scoreData.color;
    var score = scoreData.score;


    var option = {
        color: [color, '#cccccc']
        ,
        tooltip: {
            show: false
        },
        legend:
            {
                show: false,
            },
        series: [
            {
                name: '',
                type: 'pie',
                hoverAnimation: false,
                legendHoverLink: false,
                radius: ['70%', '80%'],
                avoidLabelOverlap: true,
                label: {
                    position: 'center',
                    fontSize: 60,
                },

                emphasis: {
                    show: false,
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data: [
                    {value: score, name: score},
                    {value: 100 - score, name: ''},
                ]
            }
        ]
    };

    myChart.setOption(option);

}