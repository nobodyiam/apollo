package com.ctrip.framework.apollo.spring.property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SpringValueFactory {
  public static SpringMethodValue create(String key, Object bean, Method method) {
    return new SpringMethodValue(key, bean, method);
  }

  public static SpringFieldValue create(String key, Object bean, Field field) {
    return new SpringFieldValue(key, bean, field);
  }
}
