package com.ctrip.framework.apollo.demo.spring.common.bean;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * To refresh the config bean when config is changed
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class AnnotatedBeanMonitor {
  private static final Logger logger = LoggerFactory.getLogger(AnnotatedBeanMonitor.class);

  @Autowired
  private AnnotatedBean annotatedBean;

  @ApolloConfigChangeListener({"application", "TEST1.apollo"})
  private void onChange(ConfigChangeEvent changeEvent) {
    if (changeEvent.isChanged("timeout") || changeEvent.isChanged("batch")) {
      try {
        // wait 50 ms for apollo auto updating the injected value in annotated bean
        TimeUnit.MILLISECONDS.sleep(50);
      } catch (InterruptedException e) {
        //ignore
      }

      logger.info("Detected timeout/batch change, AnnotatedBean should have been updated by Apollo: {}", annotatedBean.toString());
    }
  }
}
