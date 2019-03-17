package org.consumerreports.pagespeed.controllers;

import org.apache.logging.log4j.LogManager;
import org.consumerreports.pagespeed.PageSpeed;
import org.consumerreports.pagespeed.models.Metrics;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
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
    private MetricsRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Metrics> getAllMetrics() {
        return repository.findAll();
    }

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(PageSpeed.class);

    @RequestMapping(value = "/url", method = RequestMethod.GET)
    public Metrics getMetrics(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "strategy", required = false) String deviceType,
            @RequestParam(value = "date", required = false) String date
    ) {
        Date parsedDate = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            if (date == null || date.equals("")) {
                date = simpleDateFormat.format(new Date());
            }
            parsedDate = simpleDateFormat.parse(date);

            if (deviceType == null || deviceType.equals("")) {
                deviceType = "mobile";
            }
            return repository.findFirstByUrlContainingAndDeviceTypeEqualsAndFetchTimeBetweenOrderByFetchTimeDesc(url, deviceType, parsedDate, CommonUtil.addDays(parsedDate, 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return repository.findFirstByUrlOrderByFetchTimeDesc(url);

    }



}