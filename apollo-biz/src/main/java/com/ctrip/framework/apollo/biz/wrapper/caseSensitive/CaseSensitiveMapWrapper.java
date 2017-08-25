package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import com.ctrip.framework.apollo.biz.wrapper.MapWrapper;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseSensitiveMapWrapper<T> implements MapWrapper<T> {

  private final Map<String, T> delegate;

  public CaseSensitiveMapWrapper(Map<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T get(String key) {
    return delegate.get(key);
  }

  @Override
  public T put(String key, T value) {
    return delegate.put(key, value);
  }

  @Override
  public T remove(String key) {
    return delegate.remove(key);
  }
}
