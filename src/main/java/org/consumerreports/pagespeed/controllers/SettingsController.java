package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.Emails;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.EmailsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/settings.html")
public class SettingsController {

    @Autowired
    private UrlsRepository urlsRepository;

    @Autowired
    private EmailsRepository emailsRepository;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public Object loadSettings(
            Model model
    ) {
        model.addAttribute("tab", "settings");

        List<Urls> urlList = urlsRepository.findAll();
        model.addAttribute("urlList", urlList);

        List<Emails> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);

        if (urlList == null || emailList == null) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object processSettings(
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            Model model
    ) {

        List errors = new ArrayList();
        try {

            if (name != null && !name.equals("") && email != null && !email.equals("")) {
                Emails emails = new Emails(name, email, true);
                emailsRepository.save(emails);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("Email already exists.");
        }

        try {
            if (url != null && !url.equals("") && title != null && !title.equals("")) {
                Urls urls = new Urls(url, title);
                urlsRepository.save(urls);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }

        model.addAttribute("errors", errors);

        model.addAttribute("tab", "settings");

        List<Urls> urlList = urlsRepository.findAll();
        model.addAttribute("urlList", urlList);

        List<Emails> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);

        if (urlList == null || emailList == null) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }
}