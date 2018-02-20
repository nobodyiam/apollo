package com.ctrip.framework.apollo.spring.property;

import java.lang.reflect.Field;

/**
 * Spring @Value field info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2018/2/6.
 */
public class SpringFieldValue extends SpringValue {
  private Object bean;
  private String valKey;
  private Field field;
  private Class<?> targetType;

  SpringFieldValue(String key, Object bean, Field field) {
    this.bean = bean;
    this.field = field;
    this.valKey = key;
    this.targetType = field.getType();
  }

  @Override
  public void doUpdateVal(Object newVal) throws Exception {
      boolean accessible = field.isAccessible();
      field.setAccessible(true);
      field.set(bean, newVal);
      field.setAccessible(accessible);
  }

  @Override
  protected Class<?> getTargetType() {
    return targetType;
  }

  @Override
  public String toString() {
    return String
        .format("key: %s for field: %s.%s", valKey, bean.getClass().getName(), field.getName());
  }
}
