(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('result', {
            parent: 'entity',
            url: '/result?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.result.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/result/results.html',
                    controller: 'ResultController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('result');
                    $translatePartialLoader.addPart('classification');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('result-detail', {
            parent: 'entity',
            url: '/result/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.result.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/result/result-detail.html',
                    controller: 'ResultDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('result');
                    $translatePartialLoader.addPart('classification');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Result', function($stateParams, Result) {
                    return Result.get({id : $stateParams.id});
                }]
            }
        })
        .state('result.new', {
            parent: 'result',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/result/result-dialog.html',
                    controller: 'ResultDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                classification: null,
                                isTrainingData: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('result', null, { reload: true });
                }, function() {
                    $state.go('result');
                });
            }]
        })
        .state('result.edit', {
            parent: 'result',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/result/result-dialog.html',
                    controller: 'ResultDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Result', function(Result) {
                            return Result.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('result', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('result.delete', {
            parent: 'result',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/result/result-delete-dialog.html',
                    controller: 'ResultDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Result', function(Result) {
                            return Result.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('result', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
