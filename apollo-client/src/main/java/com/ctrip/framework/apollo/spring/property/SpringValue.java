package com.ctrip.framework.apollo.spring.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;

/**
 * Spring @Value method info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2018/2/6.
 */
public class SpringValue {

  private MethodParameter methodParameter;
  private Field field;
  private Object bean;
  private String key;
  private String placeholder;
  private Class<?> targetType;

  public SpringValue(String key, String placeholder, Object bean, Field field) {
    this.bean = bean;
    this.field = field;
    this.key = key;
    this.placeholder = placeholder;
    this.targetType = field.getType();
  }

  public SpringValue(String key, String placeholder, Object bean, Method method) {
    this.bean = bean;
    this.methodParameter = new MethodParameter(method, 0);
    this.key = key;
    this.placeholder = placeholder;
    Class<?>[] paramTps = method.getParameterTypes();
    this.targetType = paramTps[0];
  }

  public void update(Object newVal) throws IllegalAccessException, InvocationTargetException {
    if (isField()) {
      injectField(newVal);
    } else {
      injectMethod(newVal);
    }
  }

  private void injectField(Object newVal) throws IllegalAccessException {
    boolean accessible = field.isAccessible();
    field.setAccessible(true);
    field.set(bean, newVal);
    field.setAccessible(accessible);
  }

  private void injectMethod(Object newVal)
      throws InvocationTargetException, IllegalAccessException {
    methodParameter.getMethod().invoke(bean, newVal);
  }

  public Class<?> getTargetType() {
    return targetType;
  }

  public String getPlaceholder() {
    return this.placeholder;
  }

  public MethodParameter getMethodParameter() {
    return methodParameter;
  }

  public boolean isField() {
    return this.field != null;
  }

  public Field getField() {
    return field;
  }

  @Override
  public String toString() {
    if (isField()) {
      return String
          .format("key: %s, field: %s.%s", key, bean.getClass().getName(), field.getName());
    }
    return String.format("key: %s, method: %s.%s", key, bean.getClass().getName(),
        methodParameter.getMember().getName());
  }
}
