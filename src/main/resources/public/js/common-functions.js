function getScoreEntities(response) {
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