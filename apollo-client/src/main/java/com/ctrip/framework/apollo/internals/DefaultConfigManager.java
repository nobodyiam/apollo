package com.ctrip.framework.apollo.internals;

import com.google.common.collect.Maps;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.spi.ConfigFactory;
import com.ctrip.framework.apollo.spi.ConfigFactoryManager;

import com.google.inject.Inject;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigManager implements ConfigManager {
  @Inject
  private ConfigFactoryManager m_factoryManager;

  private Map<String, Config> m_configs = Maps.newConcurrentMap();
  private Map<String, ConfigFile> m_configFiles = Maps.newConcurrentMap();

  @Override
  public Config getConfig(String namespace) {
    Config config = m_configs.get(namespace);

    if (config == null) {
      synchronized (this) {
        config = m_configs.get(namespace);

        if (config == null) {
          ConfigFactory factory = m_factoryManager.getFactory(namespace);

          config = factory.create(namespace);
          m_configs.put(namespace, config);
        }
      }
    }

    return config;
  }

  @Override
  public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
    String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
    ConfigFile configFile = m_configFiles.get(namespaceFileName);

    if (configFile == null) {
      synchronized (this) {
        configFile = m_configFiles.get(namespaceFileName);

        if (configFile == null) {
          ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);

          configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
          m_configFiles.put(namespaceFileName, configFile);
        }
      }
    }

    return configFile;
  }
}
