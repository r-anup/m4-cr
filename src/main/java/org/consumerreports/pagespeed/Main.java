package org.consumerreports.pagespeed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.consumerreports.pagespeed.controllers.MetricsController;
import org.consumerreports.pagespeed.converters.EnumConverter;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Controller
@ComponentScan(basePackages = {"org.consumerreports.pagespeed"})
@SpringBootApplication(scanBasePackages = {"org.consumerreports.pagespeed.controllers"})
public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public enum FetchSource  {
        repository, lightHouseNoSave, lightHouseAndSave, googleNoSave
    }

    public enum Strategy  {
        mobile, desktop
    }

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UrlsRepository urlsRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    MetricsController metricsController;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/")
    String index() {

        return "redirect:/reports.html";
    }


    @RequestMapping("/reports.html")
    String reports(Model model) {
        model.addAttribute("tab", "reports");
        return "reports";
    }


    @RequestMapping(value = "/analyze.html")
    String analyze(
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Strategy strategy,
            @RequestParam(value = "overrideAPI", required = false, defaultValue = "") String overrideAPI,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "fetchSource", required = false, defaultValue = "repository") FetchSource fetchSource,
            Model model) {
        model.addAttribute("strategy", strategy);
        model.addAttribute("fetchSource", fetchSource);
        model.addAttribute("overrideAPI", overrideAPI);
        model.addAttribute("date", date);
        model.addAttribute("settings", "true");
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




    @RequestMapping(value = "/metrics.html", method = RequestMethod.GET)
    String metrics(
            @RequestParam(value = "url", required = false, defaultValue = "https://www.consumerreports.org/cro/index.htm") String url,
            @RequestParam(value = "benchmarkurl", required = false) String benchmarkurl,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") Strategy strategy,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "leftDate", required = false) String leftDate,
            @RequestParam(value = "rightDate", required = false) String rightDate,
            @CookieValue(value = "timezone", required = false, defaultValue = "GMT-0400") String timezone,
            Model model
            ) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        if (date == null) {
            if (rightDate == null) {
                rightDate = simpleDateFormat.format(new Date());
            }
            if (leftDate == null) {
                leftDate = simpleDateFormat.format(CommonUtil.addDays(new Date(), -1));
            }
        }

        if (leftDate != null && rightDate != null) {
            try {
                if (new SimpleDateFormat("MM/dd/yyyy").parse(leftDate).compareTo(new SimpleDateFormat("MM/dd/yyyy").parse(rightDate)) > 0) {
                    String tempDate = leftDate;
                    leftDate = rightDate;
                    rightDate = tempDate;
                }
            } catch (ParseException e) {
                LOG.error("Error parsing date string");
            }
        }

        List<Urls> urlList = urlsRepository.findAll(Sort.by("sortOrder"));

        Urls urls = urlsRepository.findFirstByUrl(url);
        if (urls != null) {
            model.addAttribute("pageTitle", urls.title);
        } else {
            model.addAttribute("pageTitle", title);
        }

        model.addAttribute("urlList", urlList);
        model.addAttribute("url", url);
        model.addAttribute("benchmarkurl", benchmarkurl);
        model.addAttribute("strategy", strategy);
        model.addAttribute("leftDate", leftDate);
        model.addAttribute("rightDate", rightDate);
        model.addAttribute("date", date);
        model.addAttribute("tab", "metrics");
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

        PageSpeed pageSpeed = new PageSpeed();
        JSONObject output = pageSpeed.processRequest(url, strategy, fetchSource, metricsRepository, urlsRepository);
        if (output != null) {
            return output.toString();
        } else {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
    }
}
