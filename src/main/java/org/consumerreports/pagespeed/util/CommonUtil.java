package org.consumerreports.pagespeed.util;

import io.micrometer.core.instrument.util.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.consumerreports.pagespeed.controllers.MetricsController;
import org.consumerreports.pagespeed.models.Metrics;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(CommonUtil.class);

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String getFormattedDate(Date date, String timezone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return simpleDateFormat.format(date);
    }

    public static String getFormattedDate(String dateStr, String timezone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return simpleDateFormat.format(DatatypeConverter.parseDateTime(dateStr).getTime());
    }

    public static JSONObject mergeJSON(JSONObject... jsonObjects) throws JSONException {
        JSONObject jsonObject = new JSONObject() {
            @Override
            public Iterator keys() {
                TreeSet<Object> sortedKeys = new TreeSet<Object>();
                Iterator keys = super.keys();
                while (keys.hasNext()) {
                    sortedKeys.add(keys.next());
                }
                return sortedKeys.iterator();
            }
        };
        for (JSONObject temp : jsonObjects) {
            Iterator<String> keys = temp.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonObject.put(key, temp.get(key));
            }

        }

        for (JSONObject obj : jsonObjects) {
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                jsonObject.put(key, obj.get(key));
            }
        }
        return jsonObject;
    }

    public static String toPercentage(String s) {
        return toPercentage(s, 0);
    }

    public static String toPercentage(String s, int digits) {
        try {
            float n = Float.parseFloat(s);
            return String.format("%." + digits + "f", n * 100);
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    public static String toMiliSeconds(String s) {
        if (null == s) {
            return "";
        }
        float n = Float.valueOf(s.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        if (s.endsWith("ms")) {
            return String.valueOf(n);
        } else if (s.endsWith("s")) {
            return String.valueOf(n * 1000);
        } else {
            return s;
        }
   }

   public static List<Integer> getScores(List<Metrics> metrics, String value, String url) {
       List<Integer> values = new ArrayList<>();
       for (int i = metrics.size(); i > 0; i--) {
           Metrics metric = metrics.get(i-1);
           try {
               values.add(Math.round(Float.parseFloat(metric.getLighthouseResult().getScore())*100));
           } catch(Exception ex) {
               LOG.error("Error parsing score for " + url + " , score: " + metric.getLighthouseResult().getScore());
           }
       }
       values.add(Math.round(Float.parseFloat(value)*100));
       return values;
   }

   public static List<Integer> getEMAScores(List<Integer> values, int mRange) {
        List<Integer> ema = new ArrayList<>();
       double k = 2.0/(mRange + 1);
       ema.add(values.get(0));
       for (int i = 1; i < values.size(); i++) {
           ema.add((int) (long) Math.round(values.get(i) * k + ema.get(i - 1) * (1 - k)));
       }
       return ema;
   }

   public static Integer getScoreRange(List<Integer> values, Integer currentValue) {
        //Integer cV = Math.round(Float.parseFloat(currentValue)*100);
        //int min = Collections.min(scores);
        int max = Collections.max(values);
        return max - currentValue;
   }

    public static Integer getMinMaxRange(List<Integer> values) {
        int min = Collections.min(values);
        int max = Collections.max(values);
        return max-min;
    }
}
