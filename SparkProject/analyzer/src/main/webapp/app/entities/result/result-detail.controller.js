(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('ResultDetailController', ResultDetailController);

    ResultDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Result', 'Document', 'SearchProfile'];

    function ResultDetailController($scope, $rootScope, $stateParams, entity, Result, Document, SearchProfile) {
        var vm = this;
        vm.result = entity;
        
        var unsubscribe = $rootScope.$on('analyzerApp:resultUpdate', function(event, result) {
            vm.result = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
