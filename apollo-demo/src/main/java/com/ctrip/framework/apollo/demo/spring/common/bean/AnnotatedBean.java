package com.ctrip.framework.apollo.demo.spring.common.bean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component("annotatedBean")
public class AnnotatedBean {
  private static final Logger logger = LoggerFactory.getLogger(AnnotatedBean.class);

  @Value("${timeout:200}")
  private int timeout;
  private int batch;

  @PostConstruct
  void initialize() {
    logger.info("timeout is initialized as {}", timeout);
    logger.info("batch is initialized as {}", batch);
  }

  @Value("${batch:100}")
  public void setBatch(int batch) {
    this.batch = batch;
  }


  @Override
  public String toString() {
    return String.format("[AnnotatedBean] timeout: %d, batch: %d", timeout, batch);
  }
}
