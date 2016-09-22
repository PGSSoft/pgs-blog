(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('search-criteria', {
            parent: 'entity',
            url: '/search-criteria',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.searchCriteria.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-criteria/search-criteria.html',
                    controller: 'SearchCriteriaController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('searchCriteria');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('search-criteria-detail', {
            parent: 'entity',
            url: '/search-criteria/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.searchCriteria.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-criteria/search-criteria-detail.html',
                    controller: 'SearchCriteriaDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('searchCriteria');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SearchCriteria', function($stateParams, SearchCriteria) {
                    return SearchCriteria.get({id : $stateParams.id});
                }]
            }
        })
        .state('search-criteria.new', {
            parent: 'search-criteria',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-criteria/search-criteria-dialog.html',
                    controller: 'SearchCriteriaDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                keyWord: null,
                                mustHaveWord: null,
                                excludedWord: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('search-criteria', null, { reload: true });
                }, function() {
                    $state.go('search-criteria');
                });
            }]
        })
        .state('search-criteria.edit', {
            parent: 'search-criteria',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-criteria/search-criteria-dialog.html',
                    controller: 'SearchCriteriaDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SearchCriteria', function(SearchCriteria) {
                            return SearchCriteria.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-criteria', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('search-criteria.delete', {
            parent: 'search-criteria',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-criteria/search-criteria-delete-dialog.html',
                    controller: 'SearchCriteriaDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SearchCriteria', function(SearchCriteria) {
                            return SearchCriteria.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-criteria', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
