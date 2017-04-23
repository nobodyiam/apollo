package com.ctrip.framework.apollo.spi;

import com.google.common.collect.Maps;

import com.google.inject.Inject;

import com.ctrip.framework.apollo.build.ApolloInjector;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryManager implements ConfigFactoryManager {
  private ConfigRegistry m_registry;

  private Map<String, ConfigFactory> m_factories = Maps.newConcurrentMap();

  public DefaultConfigFactoryManager() {
    m_registry = ApolloInjector.getInstance(ConfigRegistry.class);
  }

  @Override
  public ConfigFactory getFactory(String namespace) {
    // step 1: check hacked factory
    ConfigFactory factory = m_registry.getFactory(namespace);

    if (factory != null) {
      return factory;
    }

    // step 2: check cache
    factory = m_factories.get(namespace);

    if (factory != null) {
      return factory;
    }

    // step 3: check declared config factory
//    try {
//      factory = lookup(ConfigFactory.class, namespace);
//    } catch (LookupException ex) {
//      // ignore it
//    }

    // step 4: check default config factory
    // FIXME: 21/04/2017
    if (factory == null) {
      factory = ApolloInjector.getInstance(ConfigFactory.class);
    }

    m_factories.put(namespace, factory);

    // factory should not be null
    return factory;
  }
}
