package com.ctrip.framework.apollo.spring.auto;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring @Value method info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2018/2/6.
 */
public class SpringMethodValue extends SpringValue {
  private static final Logger logger = LoggerFactory.getLogger(SpringMethodValue.class);

  private Method method;
  private Object bean;
  private String className;
  private String fieldName;
  private String valKey;

  private SpringMethodValue(String key, Object bean, Method method) {
    this.bean = bean;
    this.method = method;
    this.className = bean.getClass().getName();
    this.fieldName = method.getName() + "(*)";
    Class<?>[] paramTps = method.getParameterTypes();
    this.parser = findParser(paramTps[0]);
    this.valKey = key;
  }

  public static SpringMethodValue create(String key, Object bean, Method method) {
    if (method.getParameterTypes().length != 1) {
      logger.error("ignore @Value setter {}.{}, expecting one parameter, actual {} parameters",
          bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
      return null;
    }
    return new SpringMethodValue(key, bean, method);
  }

  @Override
  public void updateVal(String newVal) {
    try {
      method.invoke(bean, parseVal(newVal));
      logger.info("auto update apollo changed value successfully, key={}, newVal={} for field {}.{}",
          valKey, newVal, className, fieldName);
    } catch (Throwable ex) {
      logger.error("auto update apollo changed value failed, key={}, newVal={} for field {}.{}",
          valKey, newVal, className, fieldName, ex);
    }
  }
}
