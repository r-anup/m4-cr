package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/urls")
public class UrlsController {
    @Autowired
    private UrlsRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Urls> getAllUrls() {
        return repository.findAll();
    }
}