package org.consumerreports.pagespeed.controllers;

import org.apache.logging.log4j.LogManager;
import org.consumerreports.pagespeed.PageSpeed;
import org.consumerreports.pagespeed.models.Metrics;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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
            @RequestParam(value = "strategy", required = false) String deviceType,
            @RequestParam(value = "date", required = false) String date
    ) {
        Date parsedDate;
        Metrics metrics;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            if (date == null || date.equals("")) {
                date = simpleDateFormat.format(new Date());
            }
            parsedDate = simpleDateFormat.parse(date);

            if (deviceType == null || deviceType.equals("")) {
                deviceType = "mobile";
            }
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
            @RequestParam(value = "strategy", required = false) String deviceType
    ) {
        Urls metrics = null;
       /* metrics = urlsRepository.findFirstByUrl(url);
        metrics.setDesktopLatestScore();
        urlsRepository.save();*/

        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }
}