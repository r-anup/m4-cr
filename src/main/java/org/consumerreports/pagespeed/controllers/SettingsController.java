package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.CompetitorUrls;
import org.consumerreports.pagespeed.models.Emails;
import org.consumerreports.pagespeed.models.Urls;
import org.consumerreports.pagespeed.repositories.CompetitorsRepository;
import org.consumerreports.pagespeed.repositories.EmailsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    private CompetitorsRepository competitorsRepository;

    @Autowired
    private EmailsRepository emailsRepository;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public Object loadSettings(
            Model model
    ) {
        model.addAttribute("tab", "settings");

        List<Urls> urlList = urlsRepository.findAll(Sort.by("sortOrder"));
        model.addAttribute("urlList", urlList);

        List<CompetitorUrls> competitorList = competitorsRepository.findAll();
        model.addAttribute("competitorList", competitorList);

        List<Emails> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);

        if (urlList == null || emailList == null) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object processSettings(
            @RequestParam(value = "cro-url", required = false) String croUurl,
            @RequestParam(value = "cro-title", required = false) String croTitle,
            @RequestParam(value = "competitor-url", required = false) String competitorUrl,
            @RequestParam(value = "competitor-title", required = false) String competitorTitle,
            @RequestParam(value = "competitor-brand", required = false) String competitorBrand,
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
            if (croUurl != null && !croUurl.equals("") && croTitle != null && !croTitle.equals("")) {
                Urls urls = new Urls(croUurl, croTitle);
                urlsRepository.save(urls);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }

        try {
            if (competitorUrl != null && !competitorUrl.equals("") && competitorTitle != null && !competitorTitle.equals("")  && competitorBrand != null && !competitorBrand.equals("")) {
                CompetitorUrls urls = new CompetitorUrls(competitorUrl, competitorTitle, competitorBrand);
                competitorsRepository.save(urls);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }

        model.addAttribute("errors", errors);

        model.addAttribute("tab", "settings");

        List<Urls> urlList = urlsRepository.findAll(Sort.by("sortOrder"));
        model.addAttribute("urlList", urlList);

        List<CompetitorUrls> competitorList = competitorsRepository.findAll();
        model.addAttribute("competitorList", competitorList);

        List<Emails> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);

        if (urlList == null || emailList == null) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }
}