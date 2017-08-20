package com.ctrip.framework.apollo.core.dto;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ApolloConfigNotification {
  private String namespaceName;
  private long notificationId;
  private Map<String, Long> changedNotifications = Maps.newHashMap();

  //for json converter
  public ApolloConfigNotification() {
  }

  public ApolloConfigNotification(String namespaceName, long notificationId) {
    this.namespaceName = namespaceName;
    this.notificationId = notificationId;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public long getNotificationId() {
    return notificationId;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public void setNotificationId(long notificationId) {
    this.notificationId = notificationId;
  }

  public Map<String, Long> getChangedNotifications() {
    return changedNotifications;
  }

  public void setChangedNotifications(Map<String, Long> changedNotifications) {
    this.changedNotifications = changedNotifications;
  }

  public void addChangedNotification(String key, Long notificationId) {
    changedNotifications.put(key, notificationId);
  }

  @Override
  public String toString() {
    return "ApolloConfigNotification{" +
        "namespaceName='" + namespaceName + '\'' +
        ", notificationId=" + notificationId +
        '}';
  }
}
