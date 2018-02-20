package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import com.ctrip.framework.apollo.spring.property.SpringValue;
import com.ctrip.framework.apollo.spring.property.SpringValueFactory;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class SpringValueProcessor implements BeanPostProcessor, PriorityOrdered, EnvironmentAware,
    BeanFactoryAware {

  private static final Logger logger = LoggerFactory.getLogger(SpringValueProcessor.class);
  private static final Pattern pattern = Pattern.compile("\\$\\{([^:]*)}:?(.*)");

  private final Multimap<String, SpringValue> monitor = LinkedListMultimap.create();
  private ConfigurableEnvironment environment;
  private ConfigurableBeanFactory beanFactory;
  private final boolean autoUpdateInjectedSpringProperties;

  public SpringValueProcessor() {
    autoUpdateInjectedSpringProperties = ApolloInjector.getInstance(ConfigUtil.class)
        .isAutoUpdateInjectedSpringProperties();
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (autoUpdateInjectedSpringProperties) {
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
      String key = extractPropertyKey(value.value());

      if (Strings.isNullOrEmpty(key)) {
        continue;
      }

      SpringValue springValue = SpringValueFactory.create(key, bean, field);
      monitor.put(key, springValue);
      logger.debug("Monitoring {}", springValue);
    }
  }

  private void processMethods(final Object bean, List<Method> declaredMethods) {
    for (final Method method : declaredMethods) {
      //register @Value on method
      Value value = method.getAnnotation(Value.class);
      if (value == null) {
        continue;
      }
      if (method.getParameterTypes().length != 1) {
        logger.error("Ignore @Value setter {}.{}, expecting one parameter, actual {} parameters",
            bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
        continue;
      }

      String key = extractPropertyKey(value.value());

      if (Strings.isNullOrEmpty(key)) {
        continue;
      }

      SpringValue springValue = SpringValueFactory.create(key, bean, method);
      monitor.put(key, springValue);
      logger.debug("Monitoring {}", springValue);
    }
  }

  String extractPropertyKey(String propertyString) {
    Matcher matcher = pattern.matcher(propertyString);

    if (matcher.matches()) {
      return matcher.group(1);
    }

    return null;
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
    if (autoUpdateInjectedSpringProperties) {
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
        for (String key : keys) {
          // 1. check whether the changed key is relevant
          Collection<SpringValue> targetValues = monitor.get(key);
          if (targetValues == null || targetValues.isEmpty()) {
            continue;
          }

          // 2. check whether the value is really changed or not (since spring property sources have hierarchies)
          ConfigChange configChange = changeEvent.getChange(key);
          if (!Objects.equals(environment.getProperty(key), configChange.getNewValue())) {
            continue;
          }

          // 3. update the value
          for (SpringValue val : targetValues) {
            val.updateVal(environment.getProperty(key));
          }
        }
      }
    };

    for (PropertySource<?> propertySource : environment.getPropertySources()) {
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

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (ConfigurableBeanFactory) beanFactory;
  }
}
