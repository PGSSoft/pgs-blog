(function(){
    'use strict';

    angular.module('analyzerApp').config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider){
        $stateProvider
            .state('charts', {
                parent: 'entity',
                url: '/charts',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Charts'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/result/charts/charts.html',
                        controller: 'ChartsController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {

                }
            })
    }
})();
