package org.consumerreports.pagespeed.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.consumerreports.pagespeed.Main;
import org.consumerreports.pagespeed.config.ConfigProperties;
import org.consumerreports.pagespeed.models.CompetitorUrl;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.repositories.CompetitorsRepository;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.consumerreports.pagespeed.util.PageSpeed;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.jvm.hotspot.debugger.Page;

import java.util.*;


@Component
public class RunLightHouse {


    @Value("${cron.run.enabled:true}")
    private boolean runEnabled;


    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private UrlsRepository urlsRepository;

    @Autowired
    private CompetitorsRepository competitorsRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    private static final Logger LOG = LogManager.getLogger(RunLightHouse.class);

    @Scheduled(cron = "${cron.run.expression:0 30 0 * * ?}")
    public void run() {
        if (runEnabled) {
            List<CroUrl> urls = urlsRepository.findAllUrls();
            PageSpeed pageSpeed = new PageSpeed(configProperties);

            for (CroUrl url : urls) {
                LOG.info("Processing: " + url.getUrl());
                processRequest(pageSpeed, url.getUrl(), Main.Strategy.mobile,  metricsRepository, urlsRepository);
                processRequest(pageSpeed, url.getUrl(), Main.Strategy.desktop,  metricsRepository, urlsRepository);
            }

            Set<CompetitorUrl> competitorUrls = new HashSet<>(competitorsRepository.findAllUrls());
            for (CompetitorUrl url : competitorUrls) {
                LOG.info("Processing: " + url.getUrl());
                processRequest(pageSpeed, url.getUrl(), Main.Strategy.mobile,  metricsRepository, urlsRepository);
                processRequest(pageSpeed, url.getUrl(), Main.Strategy.desktop,  metricsRepository, urlsRepository);
            }
        }
    }

    private boolean processRequest(PageSpeed pageSpeed, String url, Main.Strategy strategy, MetricsRepository metricsRepository, UrlsRepository urlsRepository) {
        JSONObject jo = pageSpeed.processRequest(url, strategy, Main.FetchSource.lightHouseAndSave, metricsRepository, urlsRepository);
        if (jo == null) {
            LOG.error("Error processing, Attempt: 1, URL: " + url + ", Strategy: " + strategy.name());
            jo = pageSpeed.processRequest(url, strategy, Main.FetchSource.lightHouseAndSave, metricsRepository, urlsRepository);
            if (jo == null) {
                LOG.error("Error processing, Attempt: 2, URL: " + url + ", Strategy: " + strategy.name());
                return false;
            }
        }
        return true;
    }
}
