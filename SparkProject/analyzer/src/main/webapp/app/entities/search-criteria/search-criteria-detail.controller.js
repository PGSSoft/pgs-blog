(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchCriteriaDetailController', SearchCriteriaDetailController);

    SearchCriteriaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SearchCriteria', 'SearchProfile'];

    function SearchCriteriaDetailController($scope, $rootScope, $stateParams, entity, SearchCriteria, SearchProfile) {
        var vm = this;
        vm.searchCriteria = entity;
        
        var unsubscribe = $rootScope.$on('analyzerApp:searchCriteriaUpdate', function(event, result) {
            vm.searchCriteria = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
