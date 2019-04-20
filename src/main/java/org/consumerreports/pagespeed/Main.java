package org.consumerreports.pagespeed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.consumerreports.pagespeed.config.ConfigProperties;
import org.consumerreports.pagespeed.controllers.MetricsController;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.consumerreports.pagespeed.util.PageSpeed;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Controller
@ComponentScan(basePackages = {"org.consumerreports.pagespeed"})
@SpringBootApplication(scanBasePackages = {"org.consumerreports.pagespeed"})
@EnableConfigurationProperties(ConfigProperties.class)
public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public enum FetchSource  {
        repository, lightHouseNoSave, lightHouseAndSave, googleNoSave
    }

    public enum Strategy  {
        mobile, desktop
    }

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UrlsRepository urlsRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    MetricsController metricsController;

    @Autowired
    void setMapKeyDotReplacement(MappingMongoConverter mappingMongoConverter) {
        mappingMongoConverter.setMapKeyDotReplacement("_");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/")
    String index() {

        return "redirect:/reports.html";
    }


    @RequestMapping("/reports.html")
    String reports(
            @CookieValue(value = "isDarkMode", required = false, defaultValue = "false") boolean isDarkMode,
            Model model) {
        model.addAttribute("isDarkMode", isDarkMode);
        model.addAttribute("tab", "reports");
        return "reports";
    }


    @RequestMapping(value = "/analyze.html")
    String analyze(
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Strategy strategy,
            @RequestParam(value = "overrideAPI", required = false, defaultValue = "") String overrideAPI,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "fetchSource", required = false, defaultValue = "repository") FetchSource fetchSource,
            @RequestParam(value = "_id", required = false) String _id,
            @CookieValue(value = "isDarkMode", required = false, defaultValue = "false") boolean isDarkMode,
            Model model) {

        model.addAttribute("strategy", strategy);
        model.addAttribute("fetchSource", fetchSource);
        model.addAttribute("_id", _id);
        model.addAttribute("overrideAPI", overrideAPI);
        model.addAttribute("date", date);
        model.addAttribute("settings", "true");
        model.addAttribute("isDarkMode", isDarkMode);
        model.addAttribute("tab", "analyze");
        return "analyze";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMismatchParams(MethodArgumentTypeMismatchException ex, Model model) {
        String method = ex.getParameter().getMethod().getName();
        if (method.equals("metrics")) {
            ObjectNode jo = mapper.createObjectNode();
            jo.put("status", "failure");
            jo.put("message", ex.getName() + " can have only one of these values :" + (Arrays.asList(ex.getRequiredType().getEnumConstants())).toString() + ". " + ex.getValue() + " set.");
            return new ResponseEntity<>(jo, HttpStatus.BAD_REQUEST);
        } else if (method.equals("analyze")) {
            model.addAttribute("strategy", "mobile");
            model.addAttribute("fetchSource", "repository");
            model.addAttribute("overrideAPI", "");
            model.addAttribute("settings", "false");
            model.addAttribute("tab", "analyze");
            return "analyze";
        } else {
            return null;
        }
    }




    @RequestMapping(value = {"/metrics.html", "/benchmark.html"}, method = RequestMethod.GET)
    String metrics(
            HttpServletRequest request,
            @RequestParam(value = "url", required = false, defaultValue = "https://www.consumerreports.org/cro/washing-machines.htm") String url,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Strategy strategy,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "leftDate", required = false) String leftDate,
            @RequestParam(value = "rightDate", required = false) String rightDate,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone,
            @CookieValue(value = "isDarkMode", required = false, defaultValue = "false") boolean isDarkMode,
            @CookieValue(value = "isDisplayEMA", required = false, defaultValue = "false") boolean isDisplayEMA,
            @CookieValue(value = "colorPalette", required = false, defaultValue = "0") int colorPalette,
            Model model
    ) {
        String path = request.getServletPath();

        CroUrl croUrl = urlsRepository.findFirstByUrl(url);
        if (croUrl != null) {
            model.addAttribute("pageTitle", croUrl.title);
        } else {
            model.addAttribute("pageTitle", title);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        List<CroUrl> urlList = null;
        String benchmarkUrl = null;
        String benchmarkTitle = null;
        String tab = null;
        if (path.equalsIgnoreCase("/metrics.html")) {
            tab = "metrics";
            urlList = urlsRepository.findAll(Sort.by("sortOrder"));

            if (rightDate == null) {
                rightDate = simpleDateFormat.format(new Date());
            }

            try {
                if (leftDate == null) {
                    LOG.info(rightDate);
                    leftDate = simpleDateFormat.format(CommonUtil.addDays(new SimpleDateFormat("MM/dd/yyyy").parse(rightDate), -1));
                    LOG.info(leftDate);
                }
            } catch (ParseException e) {
                LOG.error("Error parsing date string");
                leftDate = simpleDateFormat.format(CommonUtil.addDays(new Date(), -1));
            }

            try {
                if (new SimpleDateFormat("MM/dd/yyyy").parse(leftDate).compareTo(new SimpleDateFormat("MM/dd/yyyy").parse(rightDate)) > 0) {
                    String tempDate = leftDate;
                    leftDate = rightDate;
                    rightDate = tempDate;
                }
            } catch (ParseException e) {
                LOG.error("Error parsing date string");
            }
        } else if (path.equalsIgnoreCase("/benchmark.html")) {
            tab = "benchmark";
            urlList = urlsRepository.findAllByCompetitorUrlIsNotNullOrderBySortOrderAsc();
            benchmarkUrl = croUrl.getCompetitorUrl().getUrl();
            benchmarkTitle = croUrl.getCompetitorUrl().getTitle() + " (" + croUrl.getCompetitorUrl().getBrand() + ")";
            if (date == null) {
                date = simpleDateFormat.format(new Date());
            }
        }

        model.addAttribute("urlList", urlList);
        model.addAttribute("url", url);
        model.addAttribute("benchmarkurl", benchmarkUrl);
        model.addAttribute("benchmarkTitle", benchmarkTitle);
        model.addAttribute("strategy", strategy);
        model.addAttribute("leftDate", leftDate);
        model.addAttribute("rightDate", rightDate);
        model.addAttribute("date", date);
        model.addAttribute("isDarkMode", isDarkMode);
        model.addAttribute("isDisplayEMA", isDisplayEMA);
        model.addAttribute("colorPalette", colorPalette);
        model.addAttribute("tab", tab);
        return "metrics";
    }

    @RequestMapping(value = "/lighthouse", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    Object lightHouse(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "date", required = false) String date,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone,
            @RequestParam(value = "fetchSource", required = false) FetchSource fetchSource,
            @RequestParam(value = "strategy", required = false) String strategy) {

        if (fetchSource.equals(FetchSource.repository)) {
            return metricsController.getMetrics(url, strategy, date, timezone);
        }

        PageSpeed pageSpeed = new PageSpeed(configProperties);
        JSONObject output = pageSpeed.processRequest(url, strategy, fetchSource, metricsRepository, urlsRepository);
        if (output != null) {
            return output.toString();
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }
}