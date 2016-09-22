(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider){
        $stateProvider.state('algorithmsComparison', {
            parent: 'app',
            url: '/algorithmComparison',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/algorithmsComparison/algorithmsComparison.html',
                    controller: 'AlgorithmComparisonController',
                    controllerAs: 'vm'
                }
            }
        })
    }
})();
