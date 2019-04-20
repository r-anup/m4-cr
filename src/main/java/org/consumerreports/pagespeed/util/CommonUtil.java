package org.consumerreports.pagespeed.util;

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(CommonUtil.class);

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        LOG.info(cal);
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
        if (s == null) {
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
}
