package org.consumerreports.pagespeed.controllers;

import org.apache.logging.log4j.LogManager;
import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.Main;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.PageSpeed;
import org.consumerreports.pagespeed.models.Metrics;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Main.Strategy deviceType,
            @RequestParam(value = "date", required = false) String date,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone
    ) {
        Metrics metrics = this.getMetricsData(date, timezone, url, deviceType, metricsRepository);
        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }

    @RequestMapping(value = "/download/{fileName}/{days}", headers = "Accept=text/csv", method = RequestMethod.GET, produces = "text/csv")
    public String download(
            @PathVariable("fileName") String fileName,
            @PathVariable("days") String days,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Main.Strategy deviceType,
            @RequestParam(value = "date", required = false) String date,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone,
            HttpServletResponse response
            ) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", fileName + ".csv");
        JSONArray ja = new JSONArray();
        try {
            if (days.equalsIgnoreCase("30")) {
                List<Metrics> metrics = metricsRepository.findScoresByUrlEqualsAndDeviceTypeEqualsOrderByFetchTimeDesc(url, deviceType.name(), PageRequest.of(0, 30));
                for (Metrics metric : metrics) {
                    ja.put(MetricsController.convertMetricsToMap(metric, timezone));
                }
            } else {
                Metrics metrics = this.getMetricsData(date, timezone, url, deviceType, metricsRepository);
                try {
                    ja.put(MetricsController.convertMetricsToMap(metrics, timezone));
                } catch (JSONException e) {
                    LOG.error(e.getMessage());
                }
            }
            return CDL.toString(ja);
        } catch (JSONException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static Map<String, String> convertMetricsToMap(Metrics metrics, String timezone) throws JSONException{
        Map result = new LinkedHashMap();
        result.put("URL", metrics.getUrl());
        result.put("Device Type", metrics.getDeviceType());
        result.put("Fetch Time", CommonUtil.getFormattedDate(metrics.getFetchTime(), timezone));
        result.putAll(metrics.getLighthouseResult().getResult());
        return result;
    }

    private Metrics getMetricsData(String date, String timezone, String url, Main.Strategy deviceType, MetricsRepository metricsRepository) {
        Date parsedDate;
        Metrics metrics;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

            if (null == date || date.equals("")) {
                metrics = metricsRepository.findFirstByUrlAndDeviceTypeOrderByFetchTimeDesc(url, deviceType.name());
            } else {
                parsedDate = simpleDateFormat.parse(date);
                metrics = metricsRepository.findFirstByUrlEqualsAndDeviceTypeEqualsAndFetchTimeBetweenOrderByFetchTimeDesc(url, deviceType.name(), parsedDate, CommonUtil.addDays(parsedDate, 1));
            }
        } catch (ParseException e) {
            LOG.error(e.getMessage());
            metrics = metricsRepository.findFirstByUrlAndDeviceTypeOrderByFetchTimeDesc(url, deviceType.name());
        }
        return metrics;
    }


    @RequestMapping(value = "/url/id", method = RequestMethod.GET, produces = "application/json")
    public Object getMetricsById(
            @RequestParam(value = "_id") ObjectId _id
    ) {
        Metrics metrics;
            metrics = metricsRepository.findBy_id(_id);
        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }


    @RequestMapping(value = "/url/score", method = RequestMethod.GET, produces = "application/json")
    public Object getUrlsAndScore(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") String deviceType,
            @RequestParam(value = "isShowAll", required = false, defaultValue = "true") boolean isShowAll
    ) {
        List<Metrics> metrics;
        if (isShowAll) {
            metrics = metricsRepository.findByUrlEqualsAndDeviceTypeEqualsOrderByFetchTimeDesc(url, deviceType, PageRequest.of(0, 7));
        } else {
            metrics = metricsRepository.findScoresByUrlEqualsAndDeviceTypeEqualsOrderByFetchTimeDesc(url, deviceType, PageRequest.of(0, 30));
        }

        if (metrics != null) {
            return metrics;
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }
}