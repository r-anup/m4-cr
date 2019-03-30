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
@RequestMapping("/history.html")
public class HistoryController {


    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public Object loadSettings(
            @CookieValue(value = "isDarkMode", required = false, defaultValue = "false") boolean isDarkMode,
            Model model
    ) {
        model.addAttribute("isDarkMode", isDarkMode);
        model.addAttribute("tab", "history");

        return "history";
    }

}