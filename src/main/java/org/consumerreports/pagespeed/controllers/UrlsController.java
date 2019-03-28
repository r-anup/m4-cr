package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.CompetitorUrl;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.repositories.CompetitorsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/urls")
public class UrlsController {
    @Autowired
    private UrlsRepository urlsRepository;

    @Autowired
    private CompetitorsRepository competitorsRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<CroUrl> getAllUrls() {
        return urlsRepository.findAll(Sort.by("sortOrder"));
    }

    @RequestMapping(value = "/cro-list", method = RequestMethod.GET)
    public List<CroUrl> getAllUrlsOnly() {
        List<CroUrl> urls = urlsRepository.findAllUrls();
        List urllist = new ArrayList();
        for (CroUrl url : urls) {
            urllist.add(url.getUrl());
        }

        return urllist;
    }


    @RequestMapping(value = "/competitor-list", method = RequestMethod.GET)
    public List<CroUrl> getAllCompetitorUrlsOnly() {
        List<CompetitorUrl> urls = competitorsRepository.findAllUrls();
        List urllist = new ArrayList();
        for (CompetitorUrl url : urls) {
            urllist.add(url.getUrl());
        }

        return urllist;
    }

}