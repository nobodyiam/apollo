directive_module.directive('apollonspanel',
                           function ($compile, $window, toastr, AppUtil, PermissionService, NamespaceLockService,
                                     UserService, CommitService, ReleaseService, InstanceService) {
                               return {
                                   restrict: 'E',
                                   templateUrl: '../../views/component/namespace-panel.html',
                                   transclude: true,
                                   replace: true,
                                   scope: {
                                       namespace: '=',
                                       appId: '=',
                                       env: '=',
                                       cluster: '=',
                                       preReleaseNs: '=',
                                       preRollback: '=',
                                       createItem: '=',
                                       editItem: '=',
                                       preDeleteItem: '=',
                                       commitChange: '=',
                                       showText: '='
                                   },
                                   link: function (scope, element, attrs) {

                                       //constants
                                       var namespace_view_type = {
                                           TEXT: 'text',
                                           TABLE: 'table',
                                           HISTORY: 'history',
                                           INSTANCE: 'instance'
                                       };

                                       var namespace_instance_view_type = {
                                           LATEST_RELEASE: 'latest_release',
                                           NOT_LATEST_RELEASE: 'not_latest_release',
                                           ALL: 'all'
                                       };

                                       var MIN_ROW_SIZE = 10;

                                       scope.switchView = switchView;

                                       scope.toggleItemSearchInput = toggleItemSearchInput;

                                       scope.searchItems = searchItems;

                                       scope.loadCommitHistory = loadCommitHistory;

                                       scope.toggleTextEditStatus = toggleTextEditStatus;

                                       scope.goToSyncPage = goToSyncPage;

                                       scope.modifyByText = modifyByText;

                                       scope.goToParentAppConfigPage = goToParentAppConfigPage;

                                       scope.switchInstanceViewType = switchInstanceViewType;

                                       scope.loadInstanceInfo = loadInstanceInfo;

                                       scope.refreshInstancesInfo = refreshInstancesInfo;

                                       initNamespace(scope.namespace);

                                       //init method

                                       function initNamespace(namespace, viewType) {

                                           namespace.showSearchInput = false;
                                           namespace.viewItems = namespace.items;
                                           namespace.isPropertiesFormat = namespace.format == 'properties';
                                           namespace.isTextEditing = false;
                                           namespace.instanceViewType = namespace_instance_view_type.LATEST_RELEASE;
                                           namespace.latestReleaseInstancesPage = 0;
                                           namespace.allInstances = [];
                                           namespace.allInstancesPage = 0;

                                           //namespace view name hide suffix
                                           namespace.viewName =
                                               namespace.baseInfo.namespaceName.replace(".xml", "").replace(
                                                   ".properties", "");

                                           if (!viewType) {
                                               if (namespace.isPropertiesFormat) {
                                                   switchView(namespace, namespace_view_type.TABLE);
                                               } else {
                                                   switchView(namespace, namespace_view_type.TEXT);
                                               }
                                           } else if (viewType == namespace_view_type.TABLE) {
                                               namespace.viewType = namespace_view_type.TABLE;
                                           }

                                           //permission
                                           PermissionService.has_modify_namespace_permission(
                                               scope.appId,
                                               namespace.baseInfo.namespaceName)
                                               .then(function (result) {
                                                   namespace.hasModifyPermission = result.hasPermission;
                                               }, function (result) {

                                               });

                                           PermissionService.has_release_namespace_permission(
                                               scope.appId,
                                               namespace.baseInfo.namespaceName)
                                               .then(function (result) {
                                                   namespace.hasReleasePermission = result.hasPermission;
                                               }, function (result) {

                                               });

                                           //lock
                                           NamespaceLockService.get_namespace_lock(
                                               scope.appId, scope.env,
                                               scope.cluster,
                                               namespace.baseInfo.namespaceName)
                                               .then(function (result) {
                                                   if (result.dataChangeCreatedBy) {
                                                       namespace.lockOwner = result.dataChangeCreatedBy;
                                                   } else {
                                                       namespace.lockOwner = "";
                                                   }
                                               });

                                           //instance
                                           getInstanceCountByNamespace(namespace);
                                       }

                                       function getInstanceCountByNamespace(namespace) {
                                           InstanceService.getInstanceCountByNamespace(scope.appId,
                                                                                       scope.env,
                                                                                       scope.cluster,
                                                                                       namespace.baseInfo.namespaceName)
                                               .then(function (result) {
                                                   namespace.instancesCount = result.num;
                                               })
                                       }

                                       UserService.load_user().then(function (result) {
                                           scope.currentUser = result.userId;
                                       });

                                       PermissionService.has_assign_user_permission(scope.appId)
                                           .then(function (result) {
                                               scope.hasAssignUserPermission = result.hasPermission;
                                           }, function (result) {

                                           });

                                       //controller method
                                       function switchView(namespace, viewType) {
                                           namespace.viewType = viewType;
                                           if (namespace_view_type.TEXT == viewType) {
                                               namespace.text = parseModel2Text(namespace);
                                           } else if (namespace_view_type.TABLE == viewType) {

                                           } else if (namespace_view_type.HISTORY == viewType) {
                                               loadCommitHistory(namespace);
                                           } else {
                                               loadInstanceInfo(namespace);
                                           }
                                       }

                                       function loadCommitHistory(namespace) {
                                           if (!namespace.commits) {
                                               namespace.commits = [];
                                               namespace.commitPage = 0;
                                           }
                                           CommitService.find_commits(scope.appId,
                                                                      scope.env,
                                                                      scope.cluster,
                                                                      namespace.baseInfo.namespaceName,
                                                                      namespace.commitPage)
                                               .then(function (result) {
                                                   if (result.length == 0) {
                                                       namespace.hasLoadAllCommit = true;
                                                   }
                                                   for (var i = 0; i < result.length; i++) {
                                                       //to json
                                                       result[i].changeSets = JSON.parse(result[i].changeSets);
                                                       namespace.commits.push(result[i]);
                                                   }
                                                   namespace.commitPage += 1;
                                               }, function (result) {
                                                   toastr.error(AppUtil.errorMsg(result), "加载修改历史记录出错");
                                               });
                                       }

                                       function switchInstanceViewType(namespace, type) {
                                           namespace.instanceViewType = type;
                                           loadInstanceInfo(namespace);
                                       }

                                       function loadInstanceInfo(namespace) {

                                           var type = namespace.instanceViewType;

                                           if (namespace_instance_view_type.LATEST_RELEASE == type) {
                                               if (!namespace.latestRelease) {
                                                   ReleaseService.findActiveReleases(scope.appId,
                                                                                     scope.env,
                                                                                     scope.cluster,
                                                                                     namespace.baseInfo.namespaceName,
                                                                                     0, 1).then(function (result) {

                                                       var latestRelease = result[0];
                                                       if (!latestRelease) {
                                                           namespace.latestReleaseInstances = {};
                                                           namespace.latestReleaseInstances.total = 0;
                                                           return;
                                                       }
                                                       namespace.latestRelease = latestRelease;
                                                       InstanceService.findInstancesByRelease(scope.env,
                                                                                              latestRelease.id,
                                                                                              namespace.latestReleaseInstancesPage)
                                                           .then(function (result) {
                                                               namespace.latestReleaseInstances = result;
                                                               namespace.latestReleaseInstancesPage++;
                                                           })
                                                   });
                                               } else {
                                                   InstanceService.findInstancesByRelease(scope.env,
                                                                                          namespace.latestRelease.id,
                                                                                          namespace.latestReleaseInstancesPage)
                                                       .then(function (result) {
                                                           if (result && result.content.length) {
                                                               namespace.latestReleaseInstancesPage++;
                                                               result.content.forEach(function (instance) {
                                                                   namespace.latestReleaseInstances.content.push(
                                                                       instance);
                                                               })
                                                           }

                                                       })
                                               }

                                           } else if (namespace_instance_view_type.NOT_LATEST_RELEASE == type) {
                                               if (!namespace.latestRelease) {
                                                   return;
                                               }
                                               InstanceService.findByReleasesNotIn(scope.appId,
                                                                                   scope.env,
                                                                                   scope.cluster,
                                                                                   namespace.baseInfo.namespaceName,
                                                                                   namespace.latestRelease.id)
                                                   .then(function (result) {
                                                       if (!result || result.length == 0) {
                                                           return
                                                       }

                                                       var groupedInstances = {},
                                                           notLatestReleases = [];

                                                       result.forEach(function (instance) {
                                                           var configs = instance.configs;
                                                           if (configs.length > 0) {
                                                               configs.forEach(function (instanceConfig) {
                                                                   var release = instanceConfig.release;
                                                                   if (!groupedInstances[release.id]) {
                                                                       groupedInstances[release.id] = [];
                                                                       notLatestReleases.push(release);
                                                                   }
                                                                   groupedInstances[release.id].push(instance);
                                                               })
                                                           }
                                                       });

                                                       namespace.notLatestReleases = notLatestReleases;
                                                       namespace.notLatestReleaseInstances = groupedInstances;
                                                   })

                                           } else {
                                               InstanceService.findInstancesByNamespace(scope.appId,
                                                                                        scope.env,
                                                                                        scope.cluster,
                                                                                        namespace.baseInfo.namespaceName,
                                                                                        namespace.allInstancesPage)
                                                   .then(function (result) {
                                                       if (result && result.content.length) {
                                                           namespace.allInstancesPage++;
                                                           result.content.forEach(function (instance) {
                                                               namespace.allInstances.push(instance);
                                                           })
                                                       }
                                                   });
                                           }

                                       }

                                       function refreshInstancesInfo(namespace) {

                                           namespace.instanceViewType = namespace_instance_view_type.LATEST_RELEASE;

                                           namespace.latestReleaseInstancesPage = 0;
                                           namespace.latestReleaseInstances = [];
                                           namespace.latestRelease = undefined;

                                           namespace.notLatestReleaseNames = [];
                                           namespace.notLatestReleaseInstances = {};

                                           namespace.allInstancesPage = 0;
                                           namespace.allInstances = [];

                                           getInstanceCountByNamespace(namespace);
                                           loadInstanceInfo(namespace);

                                       }

                                       function toggleTextEditStatus(namespace) {
                                           if (!lockCheck(namespace)) {
                                               return;
                                           }
                                           namespace.isTextEditing = !namespace.isTextEditing;
                                           if (namespace.isTextEditing) {//切换为编辑状态
                                               namespace.commited = false;
                                               namespace.backupText = namespace.text;
                                               namespace.editText = parseModel2Text(namespace);

                                           } else {
                                               if (!namespace.commited) {//取消编辑,则复原
                                                   namespace.text = namespace.backupText;
                                               }
                                           }
                                       }

                                       function goToSyncPage(namespace) {
                                           if (!lockCheck(namespace)) {
                                               return false;
                                           }
                                           $window.location.href =
                                               "config/sync.html?#/appid=" + scope.appId + "&env="
                                               + scope.env + "&clusterName="
                                               + scope.cluster
                                               + "&namespaceName=" + namespace.baseInfo.namespaceName;
                                       }

                                       function modifyByText(namespace) {
                                           if (scope.commitChange(namespace)) {
                                               namespace.commited = true;
                                               toggleTextEditStatus(namespace);
                                           }
                                       }

                                       function goToParentAppConfigPage(namespace) {
                                           $window.location.href = "/config.html?#/appid=" + namespace.parentAppId;
                                           $window.location.reload();
                                       }

                                       function parseModel2Text(namespace) {

                                           if (namespace.items.length == 0) {
                                               namespace.itemCnt = MIN_ROW_SIZE;
                                               return "";
                                           }

                                           //文件模式
                                           if (!namespace.isPropertiesFormat) {
                                               return parseNotPropertiesText(namespace);
                                           } else {
                                               return parsePropertiesText(namespace);
                                           }

                                       }

                                       function parseNotPropertiesText(namespace) {
                                           var text = namespace.items[0].item.value;
                                           var lineNum = text.split("\n").length;
                                           namespace.itemCnt = lineNum < MIN_ROW_SIZE ? MIN_ROW_SIZE : lineNum;
                                           return text;
                                       }

                                       function parsePropertiesText(namespace) {
                                           var result = "";
                                           var itemCnt = 0;
                                           namespace.items.forEach(function (item) {
                                               //deleted key
                                               if (!item.item.dataChangeLastModifiedBy) {
                                                   return;
                                               }
                                               if (item.item.key) {
                                                   //use string \n to display as new line
                                                   var itemValue = item.item.value.replace(/\n/g, "\\n");

                                                   result +=
                                                       item.item.key + " = " + itemValue + "\n";
                                               } else {
                                                   result += item.item.comment + "\n";
                                               }
                                               itemCnt++;
                                           });

                                           namespace.itemCnt = itemCnt < MIN_ROW_SIZE ? MIN_ROW_SIZE : itemCnt;
                                           return result;
                                       }

                                       function toggleItemSearchInput(namespace) {
                                           namespace.showSearchInput = !namespace.showSearchInput;
                                       }

                                       function searchItems(namespace) {
                                           var searchKey = namespace.searchKey.toLowerCase();
                                           var items = [];
                                           namespace.items.forEach(function (item) {
                                               var key = item.item.key;
                                               if (key && key.toLowerCase().indexOf(searchKey) >= 0) {
                                                   items.push(item);
                                               }
                                           });
                                           namespace.viewItems = items;
                                       }

                                       function lockCheck(namespace) {
                                           if (namespace.lockOwner && scope.currentUser != namespace.lockOwner) {
                                               scope.lockOwner = namespace.lockOwner;
                                               $('#namespaceLockedDialog').modal('show');
                                               return false;
                                           }
                                           return true;
                                       }


                                   }
                               }
                           });
