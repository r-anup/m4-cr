



var urlParams = new URLSearchParams(window.location.search);
var urlStr = urlParams.get('url');
if (urlStr != null) {
    urlStr = urlStr.trim();
    var url = decodeURIComponent(urlStr);

    if (!url.startsWith('http', 0)) {
        url = "http://" + url;
    }

    $(function () {
        if (urlParams.get('fetchSource') != null && urlParams.get('fetchSource') == "lightHouseAndSave") {
            $("#lightHouseAndSave").prop('checked',  true);
        }
        $(".main-action input[name='url']").val(url);
        generateReport(url, globalData.strategy, globalData.mainAPI);
    });
}
//031319124857622

$(document).on("click", ".main-submit", function () {
    var inputURL = $(".main-action input[name='url']");
    var inputURLStr = inputURL.val();
    inputURLStr = inputURLStr.trim();
    if (!inputURLStr.startsWith('http', 0)) {
        inputURLStr = "http://" + inputURLStr;
        inputURL.val(inputURLStr);
    }

    if ($("#lightHouseAndSave").prop('checked')) {
        globalData.fetchSource = "lightHouseAndSave";
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
    $(".error-message").hide();
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
            },
            dataType: 'json',
            method: 'GET',
            url: api,
        })
    });

    $.when.apply(this, requests).then(function (mainAPIResponseArr, secondAPIResponseArr) {
        $(".loading-spinner").hide();
        var mainAPIResponse = {};
        var secondAPIResponse = {};
        if (typeof(secondAPIResponseArr) === 'undefined' || secondAPIResponseArr == null || secondAPIResponseArr == 'success') {
            secondAPIResponse = mainAPIResponse = mainAPIResponseArr;
        } else {
            mainAPIResponse = mainAPIResponseArr[0];
            secondAPIResponse = secondAPIResponseArr[0];
        }


        if (secondAPIResponse.status == "failure") {
            if (globalData.fetchSource == "repository") {
                var urlParams = new URLSearchParams(window.location.search);
                urlParams.set('fetchSource','lightHouseNoSave');
                window.location.href = window.location.origin +  window.location.pathname  + "?" + urlParams.toString();
            } else {
                $("#analysis-chart").html("");
                $(".error-message").show();
            }
            return;
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

        /* draw screenshots section */
        var screenshots = $("<div class='border-bottom border-top d-flex flex-wrap col-12 pt-2 pb-2'></div>").appendTo("#screenshots");
        data['screenshots'].forEach(function (screenshot) {
            screenshots.append('<div><img src="data:image/jpeg;base64,' + screenshot.data + '" alt="thumbnail" /><span>' + timeMiliSecondFormatter(screenshot.timing) + '</span></div>');
        });

        /* draw main snapshot section */
       if(data.finalScreenshot) {
           $("#finalScreenshot").html('<img src="' + data.finalScreenshot.data + '" alt="thumbnail" />');
       }


       /* draw more info section */
        $("#more-info-chart").loadTemplate($("#more-info-chart-template"),
            {
                mainThreadWorkTitle: data['lighthouseMisc']['mainthread-work-breakdown']['title'],
                mainThreadWorkDescription: data['lighthouseMisc']['mainthread-work-breakdown']['description'],
                mainThreadWorkDisplayValue: data['lighthouseMisc']['mainthread-work-breakdown']['displayValue'],
                domSizeTitle: data['lighthouseMisc']['dom-size']['title'],
                domSizeDescription: data['lighthouseMisc']['dom-size']['description'],
                domSizeDisplayValue: data['lighthouseMisc']['dom-size']['displayValue'],
                renderBlockingResourcesTitle: data['lighthouseMisc']['render-blocking-resources']['title'],
                renderBlockingResourcesDescription: data['lighthouseMisc']['render-blocking-resources']['description'],
                renderBlockingResourcesDisplayValue: data['lighthouseMisc']['render-blocking-resources']['displayValue'],
                renderBlockingResourcesScore: data['lighthouseMisc']['render-blocking-resources']['score'],
                renderBlockingResourcesOverallSavingsMs: data['lighthouseMisc']['render-blocking-resources']['details']['overallSavingsMs'],
                criticalRequestChainsTitle: data['lighthouseMisc']['critical-request-chains']['title'],
                criticalRequestChainsDescription: data['lighthouseMisc']['critical-request-chains']['description'],
                criticalRequestChainsDisplayValue: data['lighthouseMisc']['critical-request-chains']['displayValue']
            }
        );

        convertMarkdownLinkSnippets(".lh-audit-group__subheader--description");
        plotMiscellaneousDataChart(data['lighthouseMisc']);
        plotStackedBarChart(data['lighthouseMisc']['mainthread-work-breakdown'], '#mainthread-work-breakdown-chart');
        plotRenderBlockingResourcesChart(data['lighthouseMisc']['render-blocking-resources']['details']['items'], "#render-blocking-resources-chart");
        plotDomSizeChart(data['lighthouseMisc']['dom-size']['details']['items'], "#dom-size-chart");
       // plotCriticalRequestChain(data['lighthouseMisc']['critical-request-chains']['details']['chains'], ".lh-crc");

        var d = new DetailsRenderer(new DOM(document));
        $(".lh-crc-container").html($(d.render(data['lighthouseMisc']['critical-request-chains']['details'])));

        $(".tab-bar-wrapper").show();

        $('.report-summary').show();

    })
        .fail(function () {
            if (globalData.fetchSource == "repository") {
                var urlParams = new URLSearchParams(window.location.search);
                urlParams.set('fetchSource','lightHouseNoSave');
              //  window.location.href = window.location.origin +  window.location.pathname  + "?" + urlParams.toString();
            } else {
                $("#analysis-chart").html("");
                $(".error-message").show();
                $(".loading-spinner").hide();
            }
        });

}




function plotDiagnostricsChart(items) {
    Object.keys(items).forEach(function (key) {
        $("#" + key).find(".lh-metric__value").html(items[key].value);
    });
}

function plotDataChart(data, items, overrideValue) {
    items.forEach(function (id) {
        if (overrideValue) {
            $("#" + id).removeClass().find(".lh-metric__value").html(data[id].displayValue);
        }
        var score = getScaleFromScore(data[id].score);
        $("#" + id).addClass('lh-metric lh-metric--' + score.scale);
    });

    /* activate tooltip */
    delayPopoverClose();
}

function plotLabDataChart(data) {
    plotDataChart(data, ["first-contentful-paint", "first-meaningful-paint", "interactive", "first-cpu-idle", "estimated-input-latency", "speed-index"], true);
}

function plotMiscellaneousDataChart(data) {
    plotDataChart(data, ["mainthread-work-breakdown", "render-blocking-resources", "dom-size"], false);
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

function plotRenderBlockingResourcesChart(data, elem) {
    $.each(data, function(i, item) {
        $(elem).find("tbody").append("<tr><td>"
            + item.url + "</td><td class='text-right'>"
            + bytesFormatter(item.totalBytes) + "</td><td class='text-right'>"
            + timeMiliSecondFormatter(item.wastedMs) + "</td></tr>"
        );
    });
}

function plotDomSizeChart(data, elem) {
    $.each(data, function(i, item) {
        $(elem).find("tbody").append("<tr>" +
            "<td>" + item.statistic + "</td>" +
            "<td style='max-width: 500px;'>" + ((item.element.type) ? (item.element.type == 'code' ? '<code>'+$('<div />').text(item.element.value).html()+'</code>': item.element.value) : item.element) + "</td>" +
            "<td>" + item.value + "</td></tr>"
        );
    });
}

function convertMarkdownLinkSnippets(elem) {
    $.each($(elem), function(i, item) {
        var text = $(item).text();
        var parts = text.split(/\[([^\]]*?)\]\((https?:\/\/.*?)\)/g);
        $(item).html("");
        while (parts.length) {
            var textParts = parts.splice(0, 3);
            $(item).append("<span>"+textParts[0] + "</span>");
            if (textParts.length == 3) {
                var a = document.createElement('a');
                a.rel = 'noopener';
                a.target = '_blank';
                a.textContent = textParts[1];
                a.href = (new URL(textParts[2])).href;
                $(item).append(a);
            }
        }
    });
}








