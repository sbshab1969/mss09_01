package acp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StrUtils {

  private static final String DATE_FORMAT = "dd.MM.yyyy";
  private static final String TS_FORMAT = "dd.MM.yyyy HH:mm:ss";
  
  private static SimpleDateFormat dtFormat = new SimpleDateFormat(DATE_FORMAT);
  private static SimpleDateFormat tsFormat = new SimpleDateFormat(TS_FORMAT);

  public static boolean emptyString(String str) {
    if (str == null || str.equals("")) {
      return true;
    }
    return false;
  }

  public static String date2Str(Date value) {
    String val = null;
    if (value != null) {
        val = dtFormat.format(value);
    }
    return val;
}

public static String dateTime2Str(Date value) {
    String val = null;
    if (value != null) {
        val = tsFormat.format(value);
    }
    return val;
}
  
}
