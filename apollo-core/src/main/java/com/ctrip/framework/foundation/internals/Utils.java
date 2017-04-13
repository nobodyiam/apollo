package com.ctrip.framework.foundation.internals;

public class Utils {
  public static boolean isBlank(String str) {
    if (str == null || str.length() == 0) {
      return true;
    }

    int length = str.length();
    for (int i = 0; i < length; i++) {
      char ch = str.charAt(i);

      if (!Character.isWhitespace(ch)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isOSWindows() {
    String osName = System.getProperty("os.name");
    if (Utils.isBlank(osName)) {
      return false;
    }
    return osName.startsWith("Windows");
  }
}
