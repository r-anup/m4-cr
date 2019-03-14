package org.consumerreports.pagespeed;

import org.consumerreports.PageSpeed;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@ComponentScan(basePackages = {"org.consumerreports.pagespeed"})
@SpringBootApplication(scanBasePackages = {"org.consumerreports.pagespeed.controllers"})
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }


    @RequestMapping("/reports.html")
    String reports(Model model) {
        model.addAttribute("tab", "reports");
        return "reports";
    }

    @RequestMapping("/analyze.html")
    String analyze(
            @RequestParam(value = "fetch", required = false, defaultValue = "process") String fetch,
            @RequestParam(value = "apiSource", required = false, defaultValue = "mobile") String apiSource,
            @RequestParam(value = "strategy", required = false, defaultValue = "mobile") String strategy,
            @RequestParam(value = "overrideAPI", required = false, defaultValue = "") String overrideAPI,
            Model model) {
        model.addAttribute("strategy", strategy);
        model.addAttribute("fetch", fetch);
        model.addAttribute("apiSource", apiSource);
        model.addAttribute("overrideAPI", overrideAPI);
        model.addAttribute("tab", "analyze");
        return "analyze";
    }

    @Autowired
    UrlsRepository urlsRepository;

    @Autowired
    private MetricsRepository metricsRepository;


    @RequestMapping(value = "/metrics.html", method = RequestMethod.GET)
    String metrics(
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "strategy", required = false) String strategy,
            Model model
            ) {

        List<Urls> urls = urlsRepository.findAll();
        model.addAttribute("urls", urls);
        model.addAttribute("tab", "metrics");
        return "metrics";
    }

    @RequestMapping(value = "/processAndSave", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String processAndSave(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "apiSource", required = false) String apiSource,
            @RequestParam(value = "strategy", required = false) String strategy) {
        PageSpeed pageSpeed = new PageSpeed();
        JSONObject output = pageSpeed.processRequest(url, strategy, apiSource, metricsRepository);
        return output.toString();
    }


    @RequestMapping(value = "/process", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String process(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "apiSource", required = false) String apiSource,
            @RequestParam(value = "strategy", required = false) String strategy) {
        PageSpeed pageSpeed = new PageSpeed();
        JSONObject output = pageSpeed.processRequest(url, strategy, apiSource);
        return output.toString();
    }
}
