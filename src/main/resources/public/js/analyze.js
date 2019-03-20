



//TODO: remove this variable
var responseData = {};


var urlParams = new URLSearchParams(window.location.search);
var urlStr = urlParams.get('url');
if (urlStr != null) {
    var url = decodeURIComponent(urlStr);

    if (!url.startsWith('http', 0)) {
        url = "http://" + url;
    }


    $(function () {
        $(".main-action .url").val(url);
        generateReport(url, globalData.strategy, globalData.mainAPI);
    });
}
//031319124857622

$(document).on("click", ".main-submit", function () {
    var inputURL = $(".main-action .url");
    var inputURLStr = inputURL.val();
    if (!inputURLStr.startsWith('http', 0)) {
        inputURLStr = "http://" + inputURLStr;
        inputURL.val(inputURLStr);
    }

    generateReport(inputURLStr, 'mobile', globalData.mainAPI);
});


$(document).on("click", ".goog-tab", function () {
    generateReport(globalData.url, $(this).data().id, globalData.mainAPI);
});

function generateReport(url, strategy, mainAPI, secondAPI) {
    globalData.url = url;
    globalData.strategy = strategy;

    window.history.pushState(null, null, "/analyze.html?url=" + encodeURIComponent(url) + "&strategy=" + strategy + ((globalData.fetchSource != '') ? "&fetchSource=" + globalData.fetchSource : "") + ((globalData.overrideAPI != '') ? "&overrideAPI=" + globalData.overrideAPI: '') + ((globalData.date != null) ? "&date=" + globalData.date: ''));

    $(".tab-bar-wrapper .goog-tab").removeClass('goog-tab-selected');
    if (strategy == 'mobile') {
        $(".tab-bar-wrapper .goog-tab:nth-child(1)").addClass('goog-tab-selected');
    } else {
        $(".tab-bar-wrapper .goog-tab:nth-child(2)").addClass('goog-tab-selected');
    }
    $(".tab-bar-wrapper").show();

    /!* reset DOM *!/
    $("#analysis-chart").html('');
    $("#field-data-chart").html('');
    $("#diagnostics-chart").html('');
    $('.report-summary').hide();
    $("#screenshots").html('');

    $(".loading-spinner").show();
    /!* reset DOM *!/


    if (typeof(mainAPI) === 'undefined') {
        mainAPI = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed";
    }

    var requestPaths = [mainAPI];

    if (!(typeof(secondAPI) === 'undefined' || secondAPI == null)) {
        requestPaths.push(secondAPI);
    }

    var requests = requestPaths.map(function (api) {
        return $.ajax({
            data: {
                category: 'performance',
                url: url,
                key: 'AIzaSyAQp8vshwJq1nwhsryxOfK__GshqnpXvUA',
                locale: 'en_US',
                strategy: globalData.strategy,
                fetchSource: globalData.fetchSource,
                date: globalData.date,
                timezone: new Date().toString().match(/([A-Z]+[\+-][0-9]+)/)[1],
            },
            dataType: 'json',
            method: 'GET',
            url: api,
        })
    });

    $.when.apply(this, requests).then(function (mainAPIResponseArr, secondAPIResponseArr) {
        var mainAPIResponse = {};
        var secondAPIResponse = {};
        if (typeof(secondAPIResponseArr) === 'undefined' || secondAPIResponseArr == null || secondAPIResponseArr == 'success') {
            secondAPIResponse = mainAPIResponse = mainAPIResponseArr;
        } else {
            mainAPIResponse = mainAPIResponseArr[0];
            secondAPIResponse = secondAPIResponseArr[0];
        }


        var data = getScoreEntities(secondAPIResponse);

        if (data['showLoadingExperience']) {
            $("#field-data-chart").loadTemplate($("#field-data-template"),
                {
                    fcp_sec: data['FIRST_CONTENTFUL_PAINT_MS'].percentile,
                    fcp_category: data['FIRST_CONTENTFUL_PAINT_MS'].category,
                    fcp_flex_grow_fast: data['FIRST_CONTENTFUL_PAINT_MS'].distributions[0].proportion,
                    fcp_flex_grow_average: data['FIRST_CONTENTFUL_PAINT_MS'].distributions[1].proportion,
                    fcp_flex_grow_slow: data['FIRST_CONTENTFUL_PAINT_MS'].distributions[2].proportion,
                    fid_sec: data['FIRST_INPUT_DELAY_MS'].percentile,
                    fid_category: data['FIRST_INPUT_DELAY_MS'].category,
                    fid_flex_grow_fast: data['FIRST_INPUT_DELAY_MS'].distributions[0].proportion,
                    fid_flex_grow_average: data['FIRST_INPUT_DELAY_MS'].distributions[1].proportion,
                    fid_flex_grow_slow: data['FIRST_INPUT_DELAY_MS'].distributions[2].proportion,

                });
        }

        data = getScoreEntities(mainAPIResponse);
        $("#analysis-chart").loadTemplate($("#analysis-chart-template"),
            {
                score: data['score'],
                fetchTime: data['fetchTime'],
                url: data['url'],
            });

        $("#fetchTime").html(new Date(data['fetchTime']).toLocaleString());

        if (data['diagnostics']) {
            $("#diagnostics-chart").loadTemplate($("#diagnostics-chart-template"),
                data['diagnostics']
            );

            //plotDiagnostricsChart(data['diagnostics']);

            plotFileTypeChart(
                $(".file-type-metrics").map(function () {
                    return {
                        name: $(this).find(".lh-metric__title").text(),
                        value: $(this).find(".lh-metric__value").text()
                    };
                }).get(), '#file-type-chart', 'Page weight'
            );

            plotFileTypeChart(
                $(".tasks-metrics").map(function () {
                    return {
                        name: $(this).find(".lh-metric__title").text(),
                        value: $(this).find(".lh-metric__value").text()
                    };
                }).get(), '#tasks-chart', 'Page tasks'
            );

        }

        plotDonutChart(data['score']);
        plotLabDataChart(data['lighthouseResult']);

        data['screenshots'].forEach(function (screenshot) {
            $("#screenshots").append('<div><img src="data:image/jpeg;base64,' + screenshot.data + '" alt="thumbnail" /><span>' + timeMiliSecondFormatter(screenshot.timing) + '</span></div>');
        });

        $(".tab-bar-wrapper").show();

        responseData = mainAPIResponse;

        $('.report-summary').show();
        $(".loading-spinner").hide();
    })
        .fail(function () {
            if (globalData.fetchSource == "repository") {
                var urlParams = new URLSearchParams(window.location.search);
                urlParams.set('fetchSource','lightHouseNoSave');
                window.location.href = window.location.origin +  window.location.pathname  + "?" + urlParams.toString();
            } else {
                $("#analysis-chart").html("");
                //$("#analysis-chart").html("there was an error");
                $(".loading-spinner").hide();
            }
        });

}




function plotDiagnostricsChart(items) {
    Object.keys(items).forEach(function (key) {
        $("#" + key).find(".lh-metric__value").html(items[key].value);
    });
}


function plotLabDataChart(data) {
    var items = ["first-contentful-paint", "first-meaningful-paint", "interactive", "first-cpu-idle", "estimated-input-latency", "speed-index"];
    items.forEach(function (id) {
        $("#" + id).removeClass().find(".lh-metric__value").html(data[id].displayValue);
        var score = getScaleFromScore(data[id].score);
        $("#" + id).addClass('lh-metric lh-metric--' + score.scale);
    });

    /* activate tooltip */
    delayPopoverClose();

}

function delayPopoverClose() {
    //$('[data-toggle="tooltip"]').tooltip({delay: { "show": 100, "hide": 100 }});
    $('[data-toggle="tooltip"]').tooltip({trigger: "manual", html: true, animation: false})
        .on("mouseenter", function () {
            var _this = this;
            $(this).tooltip("show");
            $(".tooltip").on("mouseleave", function () {
                $(_this).tooltip('hide');
            });
        }).on("mouseleave", function () {
        var _this = this;
        setTimeout(function () {
            if (!$(".tooltip:hover").length) {
                $(_this).tooltip("hide");
            }
        }, 300);
    });
}



function plotDonutChart(scoreValue) {
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