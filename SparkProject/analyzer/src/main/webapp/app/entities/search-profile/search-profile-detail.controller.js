(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchProfileDetailController', SearchProfileDetailController);

    SearchProfileDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SearchProfile', 'SearchCriteria'];

    function SearchProfileDetailController($scope, $rootScope, $stateParams, entity, SearchProfile, SearchCriteria) {
        var vm = this;
        vm.searchProfile = entity;
        
        var unsubscribe = $rootScope.$on('analyzerApp:searchProfileUpdate', function(event, result) {
            vm.searchProfile = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
