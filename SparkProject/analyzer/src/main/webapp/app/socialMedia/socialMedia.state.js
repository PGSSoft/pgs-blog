(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider){
        $stateProvider.state('socialMedia', {
            parent: 'app',
            url: '/socialMedia',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/socialMedia/socialMedia.html',
                    controller: 'socialMediaCtrl',
                    controllerAs: 'vm'
                }
            }
        })
    }
})();
