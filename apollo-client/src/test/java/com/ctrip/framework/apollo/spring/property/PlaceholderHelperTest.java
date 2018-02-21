package com.ctrip.framework.apollo.spring.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

public class PlaceholderHelperTest {

  private PlaceholderHelper placeholderHelper;

  @Before
  public void setUp() throws Exception {
    placeholderHelper = new PlaceholderHelper();
  }

  @Test
  public void testExtractPlaceholderKeys() throws Exception {
    check("${some.key}", "some.key");
    check("${some.key:100}", "some.key");
    check("${some.key:${some.other.key}}", "some.key", "some.other.key");
    check("${some.key:${some.other.key:100}}", "some.key", "some.other.key");
  }

  @Test
  public void testExtractNestedPlaceholderKeys() throws Exception {
    check("${${some.key}}", "some.key");
    check("${${some.key:other.key}}", "some.key");
    check("${${some.key}:100}", "some.key");
    check("${${some.key}:${another.key}}", "some.key", "another.key");
  }

  @Test
  public void testExtractComplexNestedPlaceholderKeys() throws Exception {
    check("${${a}1${b}:3.${c:${d:100}}}", "a", "b", "c", "d");
    check("${1${a}2${b}3:4.${c:5${d:100}6}7}", "a", "b", "c", "d");
  }

  @Test
  public void testExtractInvalidPlaceholderKeys() throws Exception {
    assertTrue(placeholderHelper.extractPlaceholderKeys("some.key").isEmpty());
    assertTrue(placeholderHelper.extractPlaceholderKeys("some.key:100").isEmpty());
  }

  private void check(String propertyString, String... expectedPlaceholders) {
    assertEquals(Sets.newHashSet(expectedPlaceholders), placeholderHelper.extractPlaceholderKeys(propertyString));
  }
}
