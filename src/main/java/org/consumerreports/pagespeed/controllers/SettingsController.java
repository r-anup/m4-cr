package org.consumerreports.pagespeed.controllers;

import org.consumerreports.pagespeed.models.CompetitorUrl;
import org.consumerreports.pagespeed.models.CroUrl;
import org.consumerreports.pagespeed.models.Email;
import org.consumerreports.pagespeed.repositories.CompetitorsRepository;
import org.consumerreports.pagespeed.repositories.EmailsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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
            @CookieValue(value = "isDarkMode", required = false, defaultValue = "false") boolean isDarkMode,
            @CookieValue(value = "isEditAllowed", required = false, defaultValue = "false") boolean isEditAllowed,
            @RequestParam(value = "isEditAllowed", required = false,  defaultValue = "false") boolean isEditAllowedParam,
            Model model
    ) {

        List<CroUrl> urlList = urlsRepository.findAll(Sort.by("sortOrder"));
        model.addAttribute("urlList", urlList);

        List<CompetitorUrl> competitorList = competitorsRepository.findAll();
        model.addAttribute("competitorList", competitorList);

        List<Email> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);

        model.addAttribute("isDarkMode", isDarkMode);
        if (!isEditAllowed && isEditAllowedParam) {
            isEditAllowed = true;
        }
        model.addAttribute("isEditAllowed", isEditAllowed);
        model.addAttribute("tab", "settings");
        if (null == urlList || null == emailList) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object processSettings(
            @RequestParam(value = "cro-url", required = false) String croUurl,
            @RequestParam(value = "cro-title", required = false) String croTitle,
            @RequestParam(value = "competitor-url", required = false) String competitorNewUrl,
            @RequestParam(value = "competitor-title", required = false) String competitorTitle,
            @RequestParam(value = "competitor-brand", required = false) String competitorBrand,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mapping-url-competitor", required = false) String mappingUrlCompetitor,
            @RequestParam(value = "mapping-url-cro", required = false) String mappingUrlCro,
            Model model
    ) {

        List errors = new ArrayList();
        try {

            if (name != null && !name.equals("") && email != null && !email.equals("")) {
                Email emails = new Email(name, email, true);
                emailsRepository.save(emails);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("Email already exists.");
        }

        try {
            if (croUurl != null && !croUurl.equals("") && croTitle != null && !croTitle.equals("")) {
                CroUrl croUrl = new CroUrl(croUurl, croTitle);
                urlsRepository.save(croUrl);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }

        try {
            if (competitorNewUrl != null && !competitorNewUrl.equals("") && competitorTitle != null && !competitorTitle.equals("")  && competitorBrand != null && !competitorBrand.equals("")) {
                CompetitorUrl competitorUrl = new CompetitorUrl(competitorNewUrl, competitorTitle, competitorBrand);
                competitorsRepository.save(competitorUrl);
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }


        try {
            if (mappingUrlCompetitor != null && !mappingUrlCompetitor.equals("") && mappingUrlCro != null && !mappingUrlCro.equals("")) {
                CroUrl croUrl = urlsRepository.findFirstByUrl(mappingUrlCro);
                CompetitorUrl competitorUrl = competitorsRepository.findFirstByUrl(mappingUrlCompetitor);
                if (competitorUrl != null && croUrl != null) {
                    croUrl.setCompetitorUrl(competitorUrl);
                    croUrl.setCompetitorUrlValue(mappingUrlCompetitor);
                    urlsRepository.save(croUrl);
                }
            }
        } catch(org.springframework.dao.DuplicateKeyException dk) {
            errors.add("URL already exists.");
        }

        model.addAttribute("errors", errors);

        model.addAttribute("tab", "settings");

        List<CroUrl> urlList = urlsRepository.findAll(Sort.by("sortOrder"));
        model.addAttribute("urlList", urlList);

        List<CompetitorUrl> competitorList = competitorsRepository.findAll();
        model.addAttribute("competitorList", competitorList);

        List<Email> emailList = emailsRepository.findAll();
        model.addAttribute("emailList", emailList);


        if (null == urlList || null == emailList) {
            return "{\"status\": \"failure\", \"message\": \"No Data\"}";
        }
        return "settings";
    }
}