package com.ctrip.framework.apollo.biz.wrapper;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface CacheWrapper<T> {
  T getIfPresent(String key);

  void put(String key, T value);

  void invalidate(String key);
}
