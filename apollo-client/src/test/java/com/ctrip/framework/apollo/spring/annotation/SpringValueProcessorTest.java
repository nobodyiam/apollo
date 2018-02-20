package com.ctrip.framework.apollo.spring.annotation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SpringValueProcessorTest {

  private SpringValueProcessor springValueProcessor;

  @Before
  public void setUp() throws Exception {
    springValueProcessor = new SpringValueProcessor();
  }

  @Test
  public void testExtractPropertyKey() throws Exception {
    assertEquals("some.key", springValueProcessor.extractPropertyKey("${some.key:100}"));
  }
}
