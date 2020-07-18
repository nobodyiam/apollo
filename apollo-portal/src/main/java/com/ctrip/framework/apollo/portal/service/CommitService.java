package com.ctrip.framework.apollo.portal.service;

import com.ctrip.framework.apollo.common.dto.CommitDTO;
import com.ctrip.framework.apollo.portal.api.AdminServiceAPI;
import com.ctrip.framework.apollo.portal.environment.Env;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CommitService {


  private final AdminServiceAPI.CommitAPI commitAPI;

  public CommitService(final AdminServiceAPI.CommitAPI commitAPI) {
    this.commitAPI = commitAPI;
  }

  public List<CommitDTO> find(String appId, Env env, String clusterName, String namespaceName,
      int page, int size) {
    return commitAPI.find(appId, env, clusterName, namespaceName, page, size);
  }

}
