function getScoreEntities(response) {
    if (response.status == 'failure') {
        return null;
    }
    var data = {};

    if (response.isDataFormatted) {
        /* data coming from database */
        data['fetchTime'] = new Date(response.fetchTime).toLocaleString();
        data['lighthouseResult'] = response.lighthouseResult;
        data['score'] = response.lighthouseResult.score;
        data['screenshots'] = response['screenshots'];
    } else {
        /* data coming directly from server call */
        var result = response.lighthouseResult || response;
        data['score'] = result.categories.performance.score;
        data['fetchTime'] = new Date(response.analysisUTCTimestamp).toLocaleString();
        data['lighthouseResult'] = result.audits;
        data['url'] = response.finalUrl;
        data['screenshots'] = result.audits['screenshot-thumbnails'].details.items;
    }

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

function getScaleFromScore(score) {
    score = (score+"").replace(/,/g, "");
    score = parseFloat(score);
    var data = {
        scale: 'pass',
        score: Math.round(score * 100),
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


function plotFileTypeChart(data, elem, title, showLegends) {
    if (typeof(showLegends) == "undefined") {
        showLegends = true;
    }

    //var myChart = echarts.init(document.getElementById(elem));
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
        color: ['#558ba9','#da70d6','#32cd32','#3d52ed','#b75c45','#ff69b4','#ba55d3','#cd5c5c','#ffa500','#40e0d0','#1e90ff','#ff6347','#7b68ee','#00fa9a','#ffd700','#6699FF','#ff6666','#3cb371','#b8860b','#30e0e0'],
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
                    formatter: "{b}: {c}"
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
    var myChart = echarts.init($(elem)[0]);
    var convertToSec = false;
    if (Math.min(...data.values) > 1000) {
        convertToSec = true;
    }
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
            type: 'value'
        },
        series: [{
            name: 'Standard',
            data: data.values,
            type: 'line',
            stack: 'Standard',
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

    if (globalData.showEMA) {
        option.series.push({
            data: EMACalc(data.values, 6),
            name: 'EMA',
            type: 'line',
            stack: 'Standard',
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
            color: '#0946ff',
                data:[
                {
                    name: 'Standard',
                    textStyle: {
                        color: '#0946ff'
                    }
                },{
                    name: 'EMA',
                    textStyle: {
                        color: '#0946ff'
                    }
                }],
                orient: 'vertical',
                left: 0,
                top: 20,
        };
    }

    myChart.setOption(option);
}