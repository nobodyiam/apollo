package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import com.ctrip.framework.apollo.biz.wrapper.MapWrapper;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveMapWrapper<T> implements MapWrapper<T>{
  private final Map<String, T> delegate;

  public CaseInsensitiveMapWrapper(Map<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T get(String key) {
    return delegate.get(key.toLowerCase());
  }

  @Override
  public T put(String key, T value) {
    return delegate.put(key.toLowerCase(), value);
  }

  @Override
  public T remove(String key) {
    return delegate.remove(key.toLowerCase());
  }
}
