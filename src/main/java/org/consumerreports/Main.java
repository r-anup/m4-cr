package org.consumerreports;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@SpringBootApplication
public class Main {


  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }


  @RequestMapping(value = "/process", method=RequestMethod.GET, produces="application/json")
  @ResponseBody
  String process(
          @RequestParam(value = "url", required = true) String url,
          Map<String, Object> model) {
    PageSpeed pageSpeed = new PageSpeed();
    JSONObject output = pageSpeed.processRequest(url);
    //model.put("")

    return output.toString();

//    return "analyze";
  }

}
