package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.CompetitorUrls;
import org.consumerreports.pagespeed.models.Urls;
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
    public List<Urls> getAllUrls() {
        return urlsRepository.findAll(Sort.by("sortOrder"));
    }

    @RequestMapping(value = "/cro-list", method = RequestMethod.GET)
    public List<Urls> getAllUrlsOnly() {
        List<Urls> urls = urlsRepository.findAllUrls();
        List urllist = new ArrayList();
        for (Urls url : urls) {
            urllist.add(url.getUrl());
        }

        return urllist;
    }


    @RequestMapping(value = "/competitor-list", method = RequestMethod.GET)
    public List<Urls> getAllCompetitorUrlsOnly() {
        List<CompetitorUrls> urls = competitorsRepository.findAllUrls();
        List urllist = new ArrayList();
        for (CompetitorUrls url : urls) {
            urllist.add(url.getUrl());
        }

        return urllist;
    }

}