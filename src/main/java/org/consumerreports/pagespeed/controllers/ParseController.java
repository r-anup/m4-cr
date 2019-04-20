package org.consumerreports.pagespeed.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.consumerreports.pagespeed.Main;
import org.springframework.web.bind.annotation.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@RestController
@RequestMapping("/parse")
public class ParseController {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    @RequestMapping(value = "/url", method = RequestMethod.GET)
    @ResponseBody
    public String parseURL(
            @RequestParam(value = "url", defaultValue = "https://www.consumerreports.org/cro/index.htm") String url
    ) throws IOException {

        Document doc = Jsoup.connect(url).get();
        LOG.info(doc.title());
        Elements newsHeadlines = doc.select(".relatedarticles .article a");
        for (Element headline : newsHeadlines) {
            LOG.info(headline.attr("data-title"));
            LOG.info("%s\n\t%s",
                    headline.attr("data-title"), headline.absUrl("href"));
        }
        return "";

    }


}