package com.ctrip.framework.apollo.spring.property;

import com.ctrip.framework.apollo.util.function.Functions;
import com.google.common.base.Function;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring @Value field and method common info
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2017/12/20.
 */
public abstract class SpringValue {

  private static final Logger logger = LoggerFactory.getLogger(SpringValue.class);

  private volatile Function<String, ?> parser;

  public synchronized void updateVal(String newVal) {
    Object parsedValue;
    try {
      parsedValue = parseVal(newVal);
    } catch (Throwable ex) {
      logger.error("Parse apollo changed value failed, new value: {}, {}", newVal,
          this.toString(), ex);
      return;
    }

    try {
      doUpdateVal(parsedValue);
      logger.debug("Auto update apollo changed value successfully, new value: {}, {}", newVal,
          this.toString());
    } catch (Throwable ex) {
      logger.error("Auto update apollo changed value failed, new value: {}, {}", newVal,
          this.toString(), ex);
    }
  }

  protected abstract void doUpdateVal(Object newVal) throws Exception;

  protected abstract Class<?> getTargetType();

  private Object parseVal(String newVal) {
    // It's OK to initialize parser multiple times, so no need to lock and check
    if (parser == null) {
      parser = findParser(getTargetType());
    }

    return parser.apply(newVal);
  }

  private Function<String, ?> findParser(Class<?> targetType) {
    if (targetType.equals(String.class)) {
      return Functions.NO_OP_FUNCTION;
    }
    if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
      return Functions.TO_INT_FUNCTION;
    }
    if (targetType.equals(long.class) || targetType.equals(Long.class)) {
      return Functions.TO_LONG_FUNCTION;
    }
    if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
      return Functions.TO_BOOLEAN_FUNCTION;
    }
    if (targetType.equals(Date.class)) {
      return Functions.TO_DATE_FUNCTION;
    }
    if (targetType.equals(short.class) || targetType.equals(Short.class)) {
      return Functions.TO_SHORT_FUNCTION;
    }
    if (targetType.equals(double.class) || targetType.equals(Double.class)) {
      return Functions.TO_DOUBLE_FUNCTION;
    }
    if (targetType.equals(float.class) || targetType.equals(Float.class)) {
      return Functions.TO_FLOAT_FUNCTION;
    }
    if (targetType.equals(byte.class) || targetType.equals(Byte.class)) {
      return Functions.TO_BYTE_FUNCTION;
    }
    return Functions.NO_OP_FUNCTION;
  }

}
