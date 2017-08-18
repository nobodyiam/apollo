package com.ctrip.framework.apollo.configservice.service.config;

import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.service.ReleaseService;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigServiceWithCache extends AbstractConfigService {

  @Autowired
  private ReleaseService releaseService;

  @Override
  protected Release findActiveOne(Long id) {
    return null;
  }

  @Override
  protected Release findLatestActiveRelease(String appId, String clusterName, String namespaceName) {
    return null;
  }

  @Override
  public void handleMessage(ReleaseMessage message, String channel) {

  }
}
