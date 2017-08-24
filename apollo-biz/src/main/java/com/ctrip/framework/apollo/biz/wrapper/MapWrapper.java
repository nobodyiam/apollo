package com.ctrip.framework.apollo.biz.wrapper;

/**
 * Wrapper for map
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public interface MapWrapper<T> {
  T get(String key);

  T put(String key, T value);

  T remove(String key);
}
