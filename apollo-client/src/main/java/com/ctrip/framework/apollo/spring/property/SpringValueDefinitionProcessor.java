package com.ctrip.framework.apollo.spring.property;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * To process xml config placeholders, e.g.
 * <pre>
 *  &lt;bean class=&quot;com.ctrip.framework.apollo.demo.spring.xmlConfigDemo.bean.XmlBean&quot;&gt;
 *    &lt;property name=&quot;timeout&quot; value=&quot;${timeout:200}&quot;/&gt;
 *    &lt;property name=&quot;batch&quot; value=&quot;${batch:100}&quot;/&gt;
 *  &lt;/bean&gt;
 * </pre>
 */
public class SpringValueDefinitionProcessor implements BeanDefinitionRegistryPostProcessor {
  private static final Multimap<String, SpringValueDefinition> beanName2SpringValueDefinitions =
      LinkedListMultimap.create();

  private final ConfigUtil configUtil;
  private final PlaceholderHelper placeholderHelper;

  public SpringValueDefinitionProcessor() {
    configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    placeholderHelper = ApolloInjector.getInstance(PlaceholderHelper.class);
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    if (configUtil.isAutoUpdateInjectedSpringPropertiesEnabled()) {
      processPropertyValues(registry);
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

  }

  public static Multimap<String, SpringValueDefinition> getBeanName2SpringValueDefinitions() {
    return beanName2SpringValueDefinitions;
  }

  private void processPropertyValues(BeanDefinitionRegistry beanRegistry) {
    String[] beanNames = beanRegistry.getBeanDefinitionNames();
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(beanName);
      MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
      List<PropertyValue> propertyValues = mutablePropertyValues.getPropertyValueList();
      for (PropertyValue propertyValue : propertyValues) {
        Object value = propertyValue.getValue();
        if (!(value instanceof TypedStringValue)) {
          continue;
        }
        String placeholder = ((TypedStringValue) value).getValue();
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(placeholder);

        if (keys.isEmpty()) {
          continue;
        }

        for (String key : keys) {
          beanName2SpringValueDefinitions
              .put(beanName, new SpringValueDefinition(key, placeholder, propertyValue.getName()));
        }
      }
    }
  }
}
