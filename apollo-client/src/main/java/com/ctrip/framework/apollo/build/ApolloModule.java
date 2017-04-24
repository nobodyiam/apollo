package com.ctrip.framework.apollo.build;

import com.ctrip.framework.apollo.internals.ConfigManager;
import com.ctrip.framework.apollo.internals.ConfigServiceLocator;
import com.ctrip.framework.apollo.internals.DefaultConfigManager;
import com.ctrip.framework.apollo.internals.RemoteConfigLongPollService;
import com.ctrip.framework.apollo.spi.ConfigFactory;
import com.ctrip.framework.apollo.spi.ConfigFactoryManager;
import com.ctrip.framework.apollo.spi.ConfigRegistry;
import com.ctrip.framework.apollo.spi.DefaultConfigFactory;
import com.ctrip.framework.apollo.spi.DefaultConfigFactoryManager;
import com.ctrip.framework.apollo.spi.DefaultConfigRegistry;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.apollo.util.http.HttpUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ApolloModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ConfigManager.class).to(DefaultConfigManager.class).in(Singleton.class);
    bind(ConfigFactoryManager.class).to(DefaultConfigFactoryManager.class).in(Singleton.class);
    bind(ConfigRegistry.class).to(DefaultConfigRegistry.class).in(Singleton.class);
    bind(ConfigFactory.class).to(DefaultConfigFactory.class).in(Singleton.class);
    bind(ConfigUtil.class).in(Singleton.class);
    bind(HttpUtil.class).in(Singleton.class);
    bind(ConfigServiceLocator.class).in(Singleton.class);
    bind(RemoteConfigLongPollService.class).in(Singleton.class);
  }
}
