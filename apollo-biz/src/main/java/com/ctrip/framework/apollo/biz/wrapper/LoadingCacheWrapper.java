package com.ctrip.framework.apollo.biz.wrapper;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface LoadingCacheWrapper<T> {

  T getUnchecked(String key);

  void invalidate(String key);
}
