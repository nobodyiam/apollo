/*
 * Copyright 2024 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.portal.component.RateLimiter;
import com.ctrip.framework.apollo.portal.component.PermissionValidator;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.dto.BatchUserRequestDTO;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.util.RoleUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BatchPermissionControllerTest {

    private BatchPermissionController batchPermissionController;

    @Mock
    private UserInfoHolder userInfoHolder;

    @Mock
    private RolePermissionService rolePermissionService;

    @Mock
    private UserService userService;

    @Mock
    private PermissionValidator permissionValidator;

    @Mock
    private RateLimiter rateLimiter;

    private String testUserId = "testUser";
    private String testAppId = "testApp";
    private String testRoleType = "Master";
    private String testNamespace = "testNamespace";
    private String testEnv = "DEV";
    private String testCluster = "default";

    @Before
    public void setUp() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(testUserId);
        
        when(userInfoHolder.getUser()).thenReturn(userInfo);
        doNothing().when(rateLimiter).checkRateLimit(anyString());
        
        batchPermissionController = new BatchPermissionController(
                userInfoHolder, rolePermissionService, userService, permissionValidator, rateLimiter);
    }

    @Test
    public void testBatchAssignAppRoleToUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        userIds.add("user2");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId(anyString())).thenReturn(new UserInfo());
        
        when(rolePermissionService.assignRoleToUsers(
                eq(RoleUtils.buildAppRoleName(testAppId, testRoleType)),
                anySet(),
                eq(testUserId)
        )).thenReturn(userIds);
        
        ResponseEntity<Set<String>> response = batchPermissionController.batchAssignAppRoleToUsers(
                testAppId, testRoleType, batchUserRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userIds, response.getBody());
        
        verify(rateLimiter, times(1)).checkRateLimit(testUserId);
        verify(userService, times(2)).findByUserId(anyString());
        verify(rolePermissionService, times(1)).assignRoleToUsers(
                eq(RoleUtils.buildAppRoleName(testAppId, testRoleType)),
                eq(userIds),
                eq(testUserId)
        );
    }

    @Test
    public void testBatchRemoveAppRoleFromUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        userIds.add("user2");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId(anyString())).thenReturn(new UserInfo());
        
        doNothing().when(rolePermissionService).removeRoleFromUsers(
                eq(RoleUtils.buildAppRoleName(testAppId, testRoleType)),
                anySet(),
                eq(testUserId)
        );
        
        ResponseEntity<Void> response = batchPermissionController.batchRemoveAppRoleFromUsers(
                testAppId, testRoleType, batchUserRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(rateLimiter, times(1)).checkRateLimit(testUserId);
        verify(userService, times(2)).findByUserId(anyString());
        verify(rolePermissionService, times(1)).removeRoleFromUsers(
                eq(RoleUtils.buildAppRoleName(testAppId, testRoleType)),
                eq(userIds),
                eq(testUserId)
        );
    }

    @Test
    public void testBatchAssignNamespaceRoleToUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        userIds.add("user2");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId(anyString())).thenReturn(new UserInfo());
        
        when(rolePermissionService.assignRoleToUsers(
                eq(RoleUtils.buildNamespaceRoleName(testAppId, testNamespace, testRoleType)),
                anySet(),
                eq(testUserId)
        )).thenReturn(userIds);
        
        ResponseEntity<Set<String>> response = batchPermissionController.batchAssignNamespaceRoleToUsers(
                testAppId, testNamespace, testRoleType, batchUserRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userIds, response.getBody());
        
        verify(rateLimiter, times(1)).checkRateLimit(testUserId);
        verify(userService, times(2)).findByUserId(anyString());
        verify(rolePermissionService, times(1)).assignRoleToUsers(
                eq(RoleUtils.buildNamespaceRoleName(testAppId, testNamespace, testRoleType)),
                eq(userIds),
                eq(testUserId)
        );
    }

    @Test
    public void testBatchAssignNamespaceEnvRoleToUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        userIds.add("user2");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId(anyString())).thenReturn(new UserInfo());
        
        when(rolePermissionService.assignRoleToUsers(
                eq(RoleUtils.buildNamespaceRoleName(testAppId, testNamespace, testRoleType, testEnv)),
                anySet(),
                eq(testUserId)
        )).thenReturn(userIds);
        
        ResponseEntity<Set<String>> response = batchPermissionController.batchAssignNamespaceEnvRoleToUsers(
                testAppId, testEnv, testNamespace, testRoleType, batchUserRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userIds, response.getBody());
        
        verify(rateLimiter, times(1)).checkRateLimit(testUserId);
        verify(userService, times(2)).findByUserId(anyString());
        verify(rolePermissionService, times(1)).assignRoleToUsers(
                eq(RoleUtils.buildNamespaceRoleName(testAppId, testNamespace, testRoleType, testEnv)),
                eq(userIds),
                eq(testUserId)
        );
    }

    @Test
    public void testBatchAssignNamespaceClusterEnvRoleToUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        userIds.add("user2");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId(anyString())).thenReturn(new UserInfo());
        
        when(rolePermissionService.assignRoleToUsers(
                eq(RoleUtils.buildClusterRoleName(testAppId, testEnv, testCluster, testRoleType)),
                anySet(),
                eq(testUserId)
        )).thenReturn(userIds);
        
        ResponseEntity<Set<String>> response = batchPermissionController.batchAssignNamespaceClusterEnvRoleToUsers(
                testAppId, testEnv, testCluster, testNamespace, testRoleType, batchUserRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userIds, response.getBody());
        
        verify(rateLimiter, times(1)).checkRateLimit(testUserId);
        verify(userService, times(2)).findByUserId(anyString());
        verify(rolePermissionService, times(1)).assignRoleToUsers(
                eq(RoleUtils.buildClusterRoleName(testAppId, testEnv, testCluster, testRoleType)),
                eq(userIds),
                eq(testUserId)
        );
    }

    @Test(expected = BadRequestException.class)
    public void testInvalidRoleType() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        batchPermissionController.batchAssignAppRoleToUsers(
                testAppId, "InvalidRole", batchUserRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testUserNotFound() {
        Set<String> userIds = new HashSet<>();
        userIds.add("nonExistentUser");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        when(userService.findByUserId("nonExistentUser")).thenReturn(null);
        
        batchPermissionController.batchAssignAppRoleToUsers(
                testAppId, testRoleType, batchUserRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testRateLimitExceeded() {
        Set<String> userIds = new HashSet<>();
        userIds.add("user1");
        
        BatchUserRequestDTO batchUserRequest = new BatchUserRequestDTO();
        batchUserRequest.setUserIds(userIds);
        
        doThrow(new BadRequestException("Rate limit exceeded")).when(rateLimiter).checkRateLimit(anyString());
        
        batchPermissionController.batchAssignAppRoleToUsers(
                testAppId, testRoleType, batchUserRequest);
    }
}
