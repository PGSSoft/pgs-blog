(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('search-profile', {
            parent: 'entity',
            url: '/search-profile',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.searchProfile.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-profile/search-profiles.html',
                    controller: 'SearchProfileController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('searchProfile');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('search-profile-detail', {
            parent: 'entity',
            url: '/search-profile/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.searchProfile.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-profile/search-profile-detail.html',
                    controller: 'SearchProfileDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('searchProfile');
                    $translatePartialLoader.addPart('searchCriteria');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SearchProfile', function($stateParams, SearchProfile) {
                    return SearchProfile.get({id : $stateParams.id});
                }]
            }
        })
        .state('search-profile.new', {
            parent: 'search-profile',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-profile/search-profile-dialog.html',
                    controller: 'SearchProfileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('search-profile', null, { reload: true });
                }, function() {
                    $state.go('search-profile');
                });
            }]
        })
        .state('search-profile.edit', {
            parent: 'search-profile',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-profile/search-profile-dialog.html',
                    controller: 'SearchProfileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SearchProfile', function(SearchProfile) {
                            return SearchProfile.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-profile', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('search-profile.delete', {
            parent: 'search-profile',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-profile/search-profile-delete-dialog.html',
                    controller: 'SearchProfileDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SearchProfile', function(SearchProfile) {
                            return SearchProfile.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-profile', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
