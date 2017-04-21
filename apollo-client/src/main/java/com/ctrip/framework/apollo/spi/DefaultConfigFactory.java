package com.ctrip.framework.apollo.spi;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.internals.ConfigRepository;
import com.ctrip.framework.apollo.internals.DefaultConfig;
import com.ctrip.framework.apollo.internals.JsonConfigFile;
import com.ctrip.framework.apollo.internals.LocalFileConfigRepository;
import com.ctrip.framework.apollo.internals.PropertiesConfigFile;
import com.ctrip.framework.apollo.internals.RemoteConfigRepository;
import com.ctrip.framework.apollo.internals.XmlConfigFile;
import com.ctrip.framework.apollo.internals.YamlConfigFile;
import com.ctrip.framework.apollo.internals.YmlConfigFile;
import com.ctrip.framework.apollo.util.ConfigUtil;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactory implements ConfigFactory {
  private static final Logger logger = LoggerFactory.getLogger(DefaultConfigFactory.class);
  @Inject
  private ConfigUtil m_configUtil;

  @Inject
  private Injector injector;

  @Override
  public Config create(String namespace) {
    DefaultConfig defaultConfig =
        new DefaultConfig(namespace, createLocalConfigRepository(namespace));
    return defaultConfig;
  }

  @Override
  public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
    ConfigRepository configRepository = createLocalConfigRepository(namespace);
    switch (configFileFormat) {
      case Properties:
        return new PropertiesConfigFile(namespace, configRepository);
      case XML:
        return new XmlConfigFile(namespace, configRepository);
      case JSON:
        return new JsonConfigFile(namespace, configRepository);
      case YAML:
        return new YamlConfigFile(namespace, configRepository);
      case YML:
        return new YmlConfigFile(namespace, configRepository);
    }

    return null;
  }

  LocalFileConfigRepository createLocalConfigRepository(String namespace) {
    LocalFileConfigRepository repository;
    if (m_configUtil.isInLocalMode()) {
      logger.warn(
          "==== Apollo is in local mode! Won't pull configs from remote server for namespace {} ! ====",
          namespace);
      repository = new LocalFileConfigRepository(namespace);
    } else {
      repository = new LocalFileConfigRepository(namespace, createRemoteConfigRepository(namespace));
    }

    injector.injectMembers(repository);
  }

  RemoteConfigRepository createRemoteConfigRepository(String namespace) {
    RemoteConfigRepository repository = new RemoteConfigRepository(namespace);

    injector.injectMembers(repository);
  }
}
