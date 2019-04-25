package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.CompetitorUrl;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.repositories.CompetitorsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.joining;

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

    @RequestMapping(value = "/cro-list", method = RequestMethod.GET, produces = "application/json")
    public List<CroUrl> getAllUrlsOnly(
            @RequestParam(value = "mediaType", required = false, defaultValue = "json") String mediaType,
            @RequestParam(value = "formatted", required = false, defaultValue = "false") boolean isFormatted,
            @RequestParam(value = "doSave", required = false, defaultValue = "false") boolean doSave,
            HttpServletRequest request
    ) {
        List<CroUrl> urls = urlsRepository.findAllUrls();
        List urlList = new ArrayList();
        if (isFormatted) {
            Map<String, String> params = new HashMap<>();
            String hostPathPrefix = getDomainPath(request) + "/lighthouse?";
            params.put("suppressOutput", "true");
            if (doSave) {
                params.put("fetchSource", "lightHouseAndSave");
            } else {
                params.put("fetchSource", "lightHouseNoSave");
            }

            params.put("strategy", "mobile");
            for (CroUrl url : urls) {
                params.put("url", url.getUrl());
                urlList.add(params.keySet().stream()
                        .map(key -> key + "=" + encodeValue(params.get(key)))
                        .collect(joining("&", hostPathPrefix, "")));
            }
            params.put("strategy", "desktop");
            for (CroUrl url : urls) {
                params.put("url", url.getUrl());
                urlList.add(params.keySet().stream()
                        .map(key -> key + "=" + encodeValue(params.get(key)))
                        .collect(joining("&", hostPathPrefix, "")));
            }
        } else {
            for (CroUrl url : urls) {
                urlList.add(url.getUrl());
            }
        }

        return urlList;
    }

    @RequestMapping(value = "/competitor-list", method = RequestMethod.GET, produces = "application/json")
    public List<CompetitorUrl> getAllCompetitorUrlsOnly(
            @RequestParam(value = "mediaType", required = false, defaultValue = "json") String mediaType,
            @RequestParam(value = "formatted", required = false, defaultValue = "false") boolean isFormatted,
            @RequestParam(value = "doSave", required = false, defaultValue = "false") boolean doSave,
            HttpServletRequest request
    ) {
        Set<CompetitorUrl> urls = new HashSet<>(competitorsRepository.findAllUrls());
        List urlList = new ArrayList();
        if (isFormatted) {
            Map<String, String> params = new HashMap<>();
            String hostPathPrefix = getDomainPath(request) + "/lighthouse?";
            params.put("suppressOutput", "true");
            if (doSave) {
                params.put("fetchSource", "lightHouseAndSave");
            } else {
                params.put("fetchSource", "lightHouseNoSave");
            }

            params.put("strategy", "mobile");
            for (CompetitorUrl url : urls) {
                params.put("url", url.getUrl());
                urlList.add(params.keySet().stream()
                        .map(key -> key + "=" + encodeValue(params.get(key)))
                        .collect(joining("&", hostPathPrefix, "")));
            }
            params.put("strategy", "desktop");
            for (CompetitorUrl url : urls) {
                params.put("url", url.getUrl());
                urlList.add(params.keySet().stream()
                        .map(key -> key + "=" + encodeValue(params.get(key)))
                        .collect(joining("&", hostPathPrefix, "")));
            }
        } else {
            for (CompetitorUrl url : urls) {
                urlList.add(url.getUrl());
            }
        }

        return urlList;
    }

    private static String getDomainPath(HttpServletRequest req) {
        int serverPort = req.getServerPort();
        if ((serverPort == 80) || (serverPort == 443)) {
            return String.format("%s://%s", req.getScheme(), req.getServerName());
        } else {
            return String.format("%s://%s:%s", req.getScheme(), req.getServerName(), serverPort);
        }
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}