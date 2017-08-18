package com.ctrip.framework.apollo.configservice.service.config;

import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.service.ReleaseService;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigService extends AbstractConfigService {

  @Autowired
  private ReleaseService releaseService;

  @Override
  protected Release findActiveOne(Long id) {
    return releaseService.findActiveOne(id);
  }

  @Override
  protected Release findLatestActiveRelease(String configAppId, String configClusterName, String configNamespace) {
    return releaseService.findLatestActiveRelease(configAppId, configClusterName,
        configNamespace);
  }

  @Override
  public void handleMessage(ReleaseMessage message, String channel) {
    // since there is no cache, so do nothing
  }
}
