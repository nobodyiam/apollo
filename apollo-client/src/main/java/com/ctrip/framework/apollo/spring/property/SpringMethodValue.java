package com.ctrip.framework.apollo.spring.property;

import java.lang.reflect.Method;

/**
 * Spring @Value method info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2018/2/6.
 */
public class SpringMethodValue extends SpringValue {
  private Method method;
  private Object bean;
  private String valKey;
  private Class<?> targetType;

  SpringMethodValue(String key, Object bean, Method method) {
    this.bean = bean;
    this.method = method;
    this.valKey = key;
    Class<?>[] paramTps = method.getParameterTypes();
    this.targetType = paramTps[0];
  }

  @Override
  public void doUpdateVal(Object newVal) throws Exception{
    method.invoke(bean, newVal);
  }

  @Override
  protected Class<?> getTargetType() {
    return targetType;
  }

  @Override
  public String toString() {
    return String
        .format("key: %s for method: %s.%s", valKey, bean.getClass().getName(), method.getName());
  }
}
