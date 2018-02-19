package com.ctrip.framework.apollo.spring.auto;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring @Value field info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2018/2/6.
 */
public class SpringFieldValue extends SpringValue {
  private static final Logger logger = LoggerFactory.getLogger(SpringFieldValue.class);

  private Object bean;
  private String className;
  private String fieldName;
  private String valKey;
  private Field field;

  private SpringFieldValue(String key, Object bean, Field field) {
    this.bean = bean;
    this.className = bean.getClass().getName();
    this.fieldName = field.getName();
    this.field = field;
    this.parser = findParser(field.getType());
    this.valKey = key;
  }

  public static SpringFieldValue create(String key, Object bean, Field field) {
    return new SpringFieldValue(key, bean, field);
  }

  @Override
  public void updateVal(String newVal) {
    try {
      boolean accessible = field.isAccessible();
      field.setAccessible(true);
      field.set(bean, parseVal(newVal));
      field.setAccessible(accessible);
      logger.info("auto update apollo changed value successfully, key={}, newVal={} for field {}.{}",
          valKey, newVal, className, fieldName);
    } catch (Throwable ex) {
      logger.error("auto update apollo changed value failed, key={}, newVal={} for field {}.{}",
          valKey, newVal, className, fieldName, ex);
    }
  }
}
