(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('document', {
            parent: 'entity',
            url: '/document?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.document.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/documents.html',
                    controller: 'DocumentController',
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
                    $translatePartialLoader.addPart('document');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('document-detail', {
            parent: 'entity',
            url: '/document/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'analyzerApp.document.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/document-detail.html',
                    controller: 'DocumentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('document');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Document', function($stateParams, Document) {
                    return Document.get({id : $stateParams.id});
                }]
            }
        })
        .state('document.new', {
            parent: 'document',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                url: null,
                                content: null,
                                creationDate: null,
                                updateDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('document');
                });
            }]
        })
        .state('document.edit', {
            parent: 'document',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('document.delete', {
            parent: 'document',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-delete-dialog.html',
                    controller: 'DocumentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
