package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.auto.SpringFieldValue;
import com.ctrip.framework.apollo.spring.auto.SpringMethodValue;
import com.ctrip.framework.apollo.spring.auto.SpringValue;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import com.ctrip.framework.foundation.Foundation;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Spring value processor of field or method which has @Value.
 *
 * @author github.com/zhegexiaohuozi  seimimaster@gmail.com
 * @since 2017/12/20.
 */
public class SpringValueProcessor implements BeanPostProcessor, PriorityOrdered, EnvironmentAware {

  private static final Logger logger = LoggerFactory.getLogger(SpringValueProcessor.class);
  private static final Pattern pattern = Pattern.compile("\\$\\{([^:]*)\\}:?(.*)");

  private final Multimap<String, SpringValue> monitor = LinkedListMultimap.create();
  private ConfigurableEnvironment environment;

  public boolean enable() {
    return Foundation.app().isAutoUpdateEnable();
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (enable()) {
      Class clazz = bean.getClass();
      processFields(bean, findAllField(clazz));
      processMethods(bean, findAllMethod(clazz));
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  private void processFields(Object bean, List<Field> declaredFields) {
    for (Field field : declaredFields) {
      // register @Value on field
      Value value = field.getAnnotation(Value.class);
      if (value == null) {
        continue;
      }
      Matcher matcher = pattern.matcher(value.value());
      if (matcher.matches()) {
        String key = matcher.group(1);
        SpringValue springValue = SpringFieldValue.create(key, bean, field);
        if (springValue == null) {
          continue;
        }
        monitor.put(key, springValue);
        logger.info("Listening apollo key = {}", key);
      }
    }
  }

  private void processMethods(final Object bean, List<Method> declaredMethods) {
    for (final Method method : declaredMethods) {
      //register @Value on method
      Value value = method.getAnnotation(Value.class);
      if (value == null) {
        continue;
      }
      Matcher matcher = pattern.matcher(value.value());
      if (matcher.matches()) {
        String key = matcher.group(1);
        SpringValue springValue = SpringMethodValue.create(key, bean, method);
        if (springValue == null) {
          continue;
        }
        monitor.put(key, springValue);
        logger.info("Listening apollo key = {}", key);
      }
    }
  }

  @Override
  public int getOrder() {
    //make it as late as possible
    return Ordered.LOWEST_PRECEDENCE;
  }

  private List<Field> findAllField(Class clazz) {
    final List<Field> res = new LinkedList<>();
    ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
      @Override
      public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        res.add(field);
      }
    });
    return res;
  }

  private List<Method> findAllMethod(Class clazz) {
    final List<Method> res = new LinkedList<>();
    ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
      @Override
      public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        res.add(method);
      }
    });
    return res;
  }

  @Override
  public void setEnvironment(Environment env) {
    this.environment = (ConfigurableEnvironment) env;
    if (enable()) {
      registerConfigChangeListener();
    }
  }

  private void registerConfigChangeListener() {
    ConfigChangeListener changeListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> keys = changeEvent.changedKeys();
        if (CollectionUtils.isEmpty(keys)) {
          return;
        }
        for (String k : keys) {
          ConfigChange configChange = changeEvent.getChange(k);
          if (!Objects.equals(environment.getProperty(k), configChange.getNewValue())) {
            continue;
          }
          Collection<SpringValue> targetValues = monitor.get(k);
          if (targetValues == null || targetValues.isEmpty()) {
            continue;
          }
          for (SpringValue val : targetValues) {
            val.updateVal(environment.getProperty(k));
          }
        }
      }
    };

    Iterator<PropertySource<?>> propertySourceIterator = environment.getPropertySources()
        .iterator();

    while (propertySourceIterator.hasNext()) {
      PropertySource<?> propertySource = propertySourceIterator.next();
      if (!(propertySource instanceof CompositePropertySource)) {
        continue;
      }
      Collection<PropertySource<?>> compositePropertySources = ((CompositePropertySource) propertySource)
          .getPropertySources();
      for (PropertySource<?> compositePropertySource : compositePropertySources) {
        if (compositePropertySource instanceof ConfigPropertySource) {
          ((ConfigPropertySource) compositePropertySource).addChangeListener(changeListener);
        }
      }
    }
  }
}
