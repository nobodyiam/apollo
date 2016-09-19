package com.ctrip.framework.apollo.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.MetaDomainConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.core.enums.EnvUtils;
import com.ctrip.framework.foundation.Foundation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidal.lookup.annotation.Named;

import java.util.concurrent.TimeUnit;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Named(type = ConfigUtil.class)
public class ConfigUtil {
  private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
  private static final String TOOLS_CLUSTER = "TOOLS";
  private int refreshInterval = 5;
  private TimeUnit refreshIntervalTimeUnit = TimeUnit.MINUTES;
  private int connectTimeout = 1000; //1 second
  private int readTimeout = 5000; //5 seconds
  private String cluster;
  private int loadConfigQPS = 2; //2 times per second
  private int longPollQPS = 2; //2 times per second

  public ConfigUtil() {
    initRefreshInterval();
    initConnectTimeout();
    initReadTimeout();
    initCluster();
    initQPS();
  }

  /**
   * Get the app id for the current application.
   *
   * @return the app id or ConfigConsts.NO_APPID_PLACEHOLDER if app id is not available
   */
  public String getAppId() {
    String appId = Foundation.app().getAppId();
    if (Strings.isNullOrEmpty(appId)) {
      appId = ConfigConsts.NO_APPID_PLACEHOLDER;
      logger.warn("app.id is not set, apollo will only load public namespace configurations!");
    }
    return appId;
  }

  /**
   * Get the data center info for the current application.
   *
   * @return the current data center, null if there is no such info.
   */
  public String getDataCenter() {
    return Foundation.server().getDataCenter();
  }

  private void initCluster() {
    //Load data center from system property
    cluster = System.getProperty(ConfigConsts.APOLLO_CLUSTER_KEY);

    //Use TOOLS cluster if tools=true in server.properties
    if (Strings.isNullOrEmpty(cluster) && isToolingZone()) {
      cluster = TOOLS_CLUSTER;
    }

    //Use data center as cluster
    if (Strings.isNullOrEmpty(cluster)) {
      cluster = getDataCenter();
    }

    //Use default cluster
    if (Strings.isNullOrEmpty(cluster)) {
      cluster = ConfigConsts.CLUSTER_NAME_DEFAULT;
    }
  }

  private boolean isToolingZone() {
    if ("true".equalsIgnoreCase(Foundation.server().getProperty("tools", "false").trim())) {
      return true;
    }
    return false;
  }

  /**
   * Get the cluster name for the current application.
   *
   * @return the cluster name, or "default" if not specified
   */
  public String getCluster() {
    return cluster;
  }

  /**
   * Get the current environment.
   *
   * @return the env
   * @throws IllegalStateException if env is set
   */
  public Env getApolloEnv() {
    Env env = EnvUtils.transformEnv(Foundation.server().getEnvType());
    Preconditions.checkState(env != null, "env is not set");
    return env;
  }

  public String getLocalIp() {
    return Foundation.net().getHostAddress();
  }

  public String getMetaServerDomainName() {
    return MetaDomainConsts.getDomain(getApolloEnv());
  }

  private void initConnectTimeout() {
    String customizedConnectTimeout = System.getProperty("apollo.connectTimeout");
    if (!Strings.isNullOrEmpty(customizedConnectTimeout)) {
      try {
        connectTimeout = Integer.parseInt(customizedConnectTimeout);
      } catch (Throwable ex) {
        logger.error("Config for apollo.connectTimeout is invalid: {}", customizedConnectTimeout);
      }
    }
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  private void initReadTimeout() {
    String customizedReadTimeout = System.getProperty("apollo.readTimeout");
    if (!Strings.isNullOrEmpty(customizedReadTimeout)) {
      try {
        readTimeout = Integer.parseInt(customizedReadTimeout);
      } catch (Throwable ex) {
        logger.error("Config for apollo.readTimeout is invalid: {}", customizedReadTimeout);
      }
    }
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  private void initRefreshInterval() {
    String customizedRefreshInterval = System.getProperty("apollo.refreshInterval");
    if (!Strings.isNullOrEmpty(customizedRefreshInterval)) {
      try {
        refreshInterval = Integer.parseInt(customizedRefreshInterval);
      } catch (Throwable ex) {
        logger.error("Config for apollo.refreshInterval is invalid: {}", customizedRefreshInterval);
      }
    }
  }

  public int getRefreshInterval() {
    return refreshInterval;
  }

  public TimeUnit getRefreshIntervalTimeUnit() {
    return refreshIntervalTimeUnit;
  }

  private void initQPS() {
    String customizedLoadConfigQPS = System.getProperty("apollo.loadConfigQPS");
    if (!Strings.isNullOrEmpty(customizedLoadConfigQPS)) {
      try {
        loadConfigQPS = Integer.parseInt(customizedLoadConfigQPS);
      } catch (Throwable ex) {
        logger.error("Config for apollo.loadConfigQPS is invalid: {}", customizedLoadConfigQPS);
      }
    }

    String customizedLongPollQPS = System.getProperty("apollo.longPollQPS");
    if (!Strings.isNullOrEmpty(customizedLongPollQPS)) {
      try {
        longPollQPS = Integer.parseInt(customizedLongPollQPS);
      } catch (Throwable ex) {
        logger.error("Config for apollo.longPollQPS is invalid: {}", customizedLongPollQPS);
      }
    }
  }

  public int getLoadConfigQPS() {
    return loadConfigQPS;
  }

  public int getLongPollQPS() {
    return longPollQPS;
  }

  public String getDefaultLocalCacheDir() {
    String cacheRoot = isOSWindows() ? "C:\\opt\\data\\%s" : "/opt/data/%s";
    return String.format(cacheRoot, getAppId());
  }

  public boolean isInLocalMode() {
    try {
      Env env = getApolloEnv();
      return env == Env.LOCAL;
    } catch (Throwable ex) {
      //ignore
    }
    return false;
  }

  public boolean isOSWindows() {
    String osName = System.getProperty("os.name");
    if (Strings.isNullOrEmpty(osName)) {
      return false;
    }
    return osName.startsWith("Windows");
  }
}
