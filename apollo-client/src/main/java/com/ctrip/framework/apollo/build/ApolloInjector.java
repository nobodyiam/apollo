package com.ctrip.framework.apollo.build;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.tracer.Tracer;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ApolloInjector {
  private static Injector s_injector;
  private static Object lock = new Object();

  private static Injector getInjector() {
    if (s_injector == null) {
      synchronized (lock) {
        if (s_injector == null) {
          try {
            s_injector = Guice.createInjector(new ApolloModule());
          } catch (Throwable ex) {
            ApolloConfigException exception = new ApolloConfigException("Unable to initialize Guice Injector!", ex);
            Tracer.logError(exception);
            throw exception;
          }
        }
      }
    }

    return s_injector;
  }

  public static <T> T getInstance(Class<T> clazz) {
    try {
      return getInjector().getInstance(clazz);
    } catch (Throwable ex) {
      Tracer.logError(ex);
      throw new ApolloConfigException(
          String.format("Unable to load instance for %s!", clazz.getName()), ex);
    }
  }

}
