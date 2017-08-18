package com.ctrip.framework.apollo.configservice.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.grayReleaseRule.GrayReleaseRulesHolder;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.InstanceConfigAuditUtil;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RestController
@RequestMapping("/configs")
public class ConfigController {
  private static final Splitter X_FORWARDED_FOR_SPLITTER = Splitter.on(",").omitEmptyStrings()
      .trimResults();
  @Autowired
  private ReleaseService releaseService;
  @Autowired
  private AppNamespaceServiceWithCache appNamespaceService;
  @Autowired
  private NamespaceUtil namespaceUtil;
  @Autowired
  private InstanceConfigAuditUtil instanceConfigAuditUtil;
  @Autowired
  private GrayReleaseRulesHolder grayReleaseRulesHolder;

  private static final Gson gson = new Gson();
  private static final Type configurationTypeReference =
      new TypeToken<Map<java.lang.String, java.lang.String>>() {
      }.getType();
  private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);

  @RequestMapping(value = "/{appId}/{clusterName}/{namespace:.+}", method = RequestMethod.GET)
  public ApolloConfig queryConfig(@PathVariable String appId, @PathVariable String clusterName,
                                  @PathVariable String namespace,
                                  @RequestParam(value = "dataCenter", required = false) String dataCenter,
                                  @RequestParam(value = "releaseKey", defaultValue = "-1") String clientSideReleaseKey,
                                  @RequestParam(value = "ip", required = false) String clientIp,
                                  @RequestParam(value = "notificationId", defaultValue = "-1") long notificationId,
                                  HttpServletRequest request, HttpServletResponse response) throws IOException {
    String originalNamespace = namespace;
    //strip out .properties suffix
    namespace = namespaceUtil.filterNamespaceName(namespace);

    if (Strings.isNullOrEmpty(clientIp)) {
      clientIp = tryToGetClientIp(request);
    }

    List<Release> releases = Lists.newLinkedList();

    String appClusterNameLoaded = clusterName;
    if (!ConfigConsts.NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
      Release currentAppRelease = loadConfig(appId, clientIp, appId, clusterName, namespace,
          dataCenter);

      if (currentAppRelease != null) {
        releases.add(currentAppRelease);
        //we have cluster search process, so the cluster name might be overridden
        appClusterNameLoaded = currentAppRelease.getClusterName();
      }
    }

    //if namespace does not belong to this appId, should check if there is a public configuration
    if (!namespaceBelongsToAppId(appId, namespace)) {
      Release publicRelease = this.findPublicConfig(appId, clientIp, clusterName, namespace,
          dataCenter);
      if (!Objects.isNull(publicRelease)) {
        releases.add(publicRelease);
      }
    }

    if (releases.isEmpty()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND,
          String.format(
              "Could not load configurations with appId: %s, clusterName: %s, namespace: %s",
              appId, clusterName, originalNamespace));
      Tracer.logEvent("Apollo.Config.NotFound",
          assembleKey(appId, clusterName, originalNamespace, dataCenter));
      return null;
    }

    auditReleases(appId, clusterName, dataCenter, clientIp, releases);

    String mergedReleaseKey = FluentIterable.from(releases).transform(
        input -> input.getReleaseKey()).join(STRING_JOINER);

    if (mergedReleaseKey.equals(clientSideReleaseKey)) {
      // Client side configuration is the same with server side, return 304
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      Tracer.logEvent("Apollo.Config.NotModified",
          assembleKey(appId, appClusterNameLoaded, originalNamespace, dataCenter));
      return null;
    }

    ApolloConfig apolloConfig = new ApolloConfig(appId, appClusterNameLoaded, originalNamespace,
        mergedReleaseKey);
    apolloConfig.setConfigurations(mergeReleaseConfigurations(releases));

    Tracer.logEvent("Apollo.Config.Found", assembleKey(appId, appClusterNameLoaded,
        originalNamespace, dataCenter));
    return apolloConfig;
  }

  private boolean namespaceBelongsToAppId(String appId, String namespaceName) {
    //Every app has an 'application' namespace
    if (Objects.equals(ConfigConsts.NAMESPACE_APPLICATION, namespaceName)) {
      return true;
    }

    //if no appId is present, then no other namespace belongs to it
    if (ConfigConsts.NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
      return false;
    }

    AppNamespace appNamespace = appNamespaceService.findByAppIdAndNamespace(appId, namespaceName);

    return appNamespace != null;
  }

  /**
   * @param clientAppId the application which uses public config
   * @param namespace   the namespace
   * @param dataCenter  the datacenter
   */
  private Release findPublicConfig(String clientAppId, String clientIp, String clusterName,
                                   String namespace,
                                   String dataCenter) {
    AppNamespace appNamespace = appNamespaceService.findPublicNamespaceByName(namespace);

    //check whether the namespace's appId equals to current one
    if (Objects.isNull(appNamespace) || Objects.equals(clientAppId, appNamespace.getAppId())) {
      return null;
    }

    String publicConfigAppId = appNamespace.getAppId();

    return loadConfig(clientAppId, clientIp, publicConfigAppId, clusterName, namespace, dataCenter);
  }

  private Release loadConfig(String clientAppId, String clientIp, String configAppId, String
      configClusterName, String configNamespace, String dataCenter) {
    //load from specified cluster fist
    if (!Objects.equals(ConfigConsts.CLUSTER_NAME_DEFAULT, configClusterName)) {
      Release clusterRelease = findRelease(clientAppId, clientIp, configAppId, configClusterName,
          configNamespace);

      if (!Objects.isNull(clusterRelease)) {
        return clusterRelease;
      }
    }

    //try to load via data center
    if (!Strings.isNullOrEmpty(dataCenter) && !Objects.equals(dataCenter, configClusterName)) {
      Release dataCenterRelease = findRelease(clientAppId, clientIp, configAppId, dataCenter,
          configNamespace);
      if (!Objects.isNull(dataCenterRelease)) {
        return dataCenterRelease;
      }
    }

    //fallback to default release
    return findRelease(clientAppId, clientIp, configAppId, ConfigConsts.CLUSTER_NAME_DEFAULT,
        configNamespace);
  }

  private Release findRelease(String clientAppId, String clientIp, String configAppId, String
      configClusterName, String configNamespace) {
    Long grayReleaseId = grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(clientAppId,
        clientIp, configAppId, configClusterName, configNamespace);

    Release release = null;

    if (grayReleaseId != null) {
      release = releaseService.findActiveOne(grayReleaseId);
    }

    if (release == null) {
      release = releaseService.findLatestActiveRelease(configAppId, configClusterName,
          configNamespace);
    }

    return release;
  }

  /**
   * Merge configurations of releases.
   * Release in lower index override those in higher index
   */
  Map<String, String> mergeReleaseConfigurations(List<Release> releases) {
    Map<String, String> result = Maps.newHashMap();
    for (Release release : Lists.reverse(releases)) {
      result.putAll(gson.fromJson(release.getConfigurations(), configurationTypeReference));
    }
    return result;
  }

  private String assembleKey(String appId, String cluster, String namespace, String datacenter) {
    List<String> keyParts = Lists.newArrayList(appId, cluster, namespace);
    if (!Strings.isNullOrEmpty(datacenter)) {
      keyParts.add(datacenter);
    }
    return STRING_JOINER.join(keyParts);
  }

  private void auditReleases(String appId, String cluster, String datacenter, String clientIp,
                             List<Release> releases) {
    if (Strings.isNullOrEmpty(clientIp)) {
      //no need to audit instance config when there is no ip
      return;
    }
    for (Release release : releases) {
      instanceConfigAuditUtil.audit(appId, cluster, datacenter, clientIp, release.getAppId(),
          release.getClusterName(),
          release.getNamespaceName(), release.getReleaseKey());
    }
  }

  private String tryToGetClientIp(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-FORWARDED-FOR");
    if (!Strings.isNullOrEmpty(forwardedFor)) {
      return X_FORWARDED_FOR_SPLITTER.splitToList(forwardedFor).get(0);
    }
    return request.getRemoteAddr();
  }

}
