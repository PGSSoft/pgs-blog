(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider){
        $stateProvider.state('jobs', {
            parent: 'app',
            url: '/jobs',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/jobs/jobs.html',
                    controller: 'JobsController',
                    controllerAs: 'vm'
                }
            }
        })
    }
})();
