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
package com.ctrip.framework.apollo.openapi.v1.controller;

import com.ctrip.framework.apollo.audit.annotation.ApolloAuditLog;
import com.ctrip.framework.apollo.audit.annotation.OpType;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.portal.component.PermissionValidator;
import com.ctrip.framework.apollo.portal.constant.RoleType;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.dto.BatchUserRequestDTO;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Controller for batch permission management operations
 */
@RestController("openapiPermissionController")
@RequestMapping("/openapi/v1")
public class PermissionController {

    private final UserInfoHolder userInfoHolder;
    private final RolePermissionService rolePermissionService;
    private final UserService userService;
    private final PermissionValidator userPermissionValidator;

    public PermissionController(
            final UserInfoHolder userInfoHolder,
            final RolePermissionService rolePermissionService,
            final UserService userService,
            final PermissionValidator userPermissionValidator) {
        this.userInfoHolder = userInfoHolder;
        this.rolePermissionService = rolePermissionService;
        this.userService = userService;
        this.userPermissionValidator = userPermissionValidator;
    }

    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @PostMapping("/batch/apps/{appId}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.CREATE, name = "Auth.batchAssignAppRoleToUsers")
    public ResponseEntity<Set<String>> batchAssignAppRoleToUsers(
            @PathVariable String appId,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        Set<String> assignedUsers = rolePermissionService.assignRoleToUsers(
            RoleUtils.buildAppRoleName(appId, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok(assignedUsers);
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @DeleteMapping("/batch/apps/{appId}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.DELETE, name = "Auth.batchRemoveAppRoleFromUsers")
    public ResponseEntity<Void> batchRemoveAppRoleFromUsers(
            @PathVariable String appId,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        rolePermissionService.removeRoleFromUsers(
            RoleUtils.buildAppRoleName(appId, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @PostMapping("/batch/apps/{appId}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.CREATE, name = "Auth.batchAssignNamespaceRoleToUsers")
    public ResponseEntity<Set<String>> batchAssignNamespaceRoleToUsers(
            @PathVariable String appId,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        Set<String> assignedUsers = rolePermissionService.assignRoleToUsers(
            RoleUtils.buildNamespaceRoleName(appId, namespaceName, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok(assignedUsers);
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @DeleteMapping("/batch/apps/{appId}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.DELETE, name = "Auth.batchRemoveNamespaceRoleFromUsers")
    public ResponseEntity<Void> batchRemoveNamespaceRoleFromUsers(
            @PathVariable String appId,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        rolePermissionService.removeRoleFromUsers(
            RoleUtils.buildNamespaceRoleName(appId, namespaceName, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @PostMapping("/batch/apps/{appId}/envs/{env}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.CREATE, name = "Auth.batchAssignNamespaceEnvRoleToUsers")
    public ResponseEntity<Set<String>> batchAssignNamespaceEnvRoleToUsers(
            @PathVariable String appId,
            @PathVariable String env,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        Set<String> assignedUsers = rolePermissionService.assignRoleToUsers(
            RoleUtils.buildNamespaceRoleName(appId, namespaceName, roleType, env),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok(assignedUsers);
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @DeleteMapping("/batch/apps/{appId}/envs/{env}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.DELETE, name = "Auth.batchRemoveNamespaceEnvRoleFromUsers")
    public ResponseEntity<Void> batchRemoveNamespaceEnvRoleFromUsers(
            @PathVariable String appId,
            @PathVariable String env,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        rolePermissionService.removeRoleFromUsers(
            RoleUtils.buildNamespaceRoleName(appId, namespaceName, roleType, env),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @PostMapping("/batch/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.CREATE, name = "Auth.batchAssignNamespaceClusterEnvRoleToUsers")
    public ResponseEntity<Set<String>> batchAssignNamespaceClusterEnvRoleToUsers(
            @PathVariable String appId,
            @PathVariable String env,
            @PathVariable String clusterName,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        Set<String> assignedUsers = rolePermissionService.assignRoleToUsers(
            RoleUtils.buildClusterRoleName(appId, env, clusterName, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok(assignedUsers);
    }
    
    @PreAuthorize(value = "@userPermissionValidator.hasAssignRolePermission(#appId)")
    @DeleteMapping("/batch/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/roles/{roleType}")
    @ApolloAuditLog(type = OpType.DELETE, name = "Auth.batchRemoveNamespaceClusterEnvRoleFromUsers")
    public ResponseEntity<Void> batchRemoveNamespaceClusterEnvRoleFromUsers(
            @PathVariable String appId,
            @PathVariable String env,
            @PathVariable String clusterName,
            @PathVariable String namespaceName,
            @PathVariable String roleType,
            @RequestBody BatchUserRequestDTO batchUserRequest) {
        
        if (!RoleType.isValidRoleType(roleType)) {
            throw new BadRequestException("Invalid role type: " + roleType);
        }
        
        Set<String> userIds = batchUserRequest.getUserIds();
        validateUsers(userIds);
        
        rolePermissionService.removeRoleFromUsers(
            RoleUtils.buildClusterRoleName(appId, env, clusterName, roleType),
            userIds,
            userInfoHolder.getUser().getUserId()
        );
        
        return ResponseEntity.ok().build();
    }
    
    private void validateUsers(Set<String> userIds) {
        for (String userId : userIds) {
            UserInfo userInfo = userService.findByUserId(userId);
            if (userInfo == null) {
                throw new BadRequestException("User not found: " + userId);
            }
        }
    }
}
