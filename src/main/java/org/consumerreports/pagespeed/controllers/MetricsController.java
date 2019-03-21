package org.consumerreports.pagespeed.controllers;

import org.apache.logging.log4j.LogManager;
import org.consumerreports.pagespeed.PageSpeed;
import org.consumerreports.pagespeed.models.Metrics;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@RestController
@RequestMapping("/metrics")
public class MetricsController {
    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private UrlsRepository urlsRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Metrics> getAllMetrics() {
        return metricsRepository.findAll();
    }

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(PageSpeed.class);

    @RequestMapping(value = "/url", method = RequestMethod.GET, produces = "application/json")
    public Object getMetrics(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") String deviceType,
            @RequestParam(value = "date", required = false) String date,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone
    ) {
        Date parsedDate;
        Metrics metrics;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

            if (date == null || date.equals("")) {
                date = simpleDateFormat.format(new Date());
            }
            parsedDate = simpleDateFormat.parse(date);

            metrics = metricsRepository.findFirstByUrlContainingAndDeviceTypeEqualsAndFetchTimeBetweenOrderByFetchTimeDesc(url, deviceType, parsedDate, CommonUtil.addDays(parsedDate, 1));
        } catch (ParseException e) {
            e.printStackTrace();
            metrics = metricsRepository.findFirstByUrlOrderByFetchTimeDesc(url);
        }

        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }


    @RequestMapping(value = "/url/score", method = RequestMethod.GET, produces = "application/json")
    public Object getUrlsAndScore(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") String deviceType
    ) {
        List<Metrics> metrics;
        metrics = metricsRepository.findByUrlContainingAndDeviceTypeEqualsOrderByFetchTimeDesc(url, deviceType, PageRequest.of(0, 6));

        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }
}