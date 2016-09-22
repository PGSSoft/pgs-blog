(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchProfileController', SearchProfileController);

    SearchProfileController.$inject = ['$scope', '$state', 'SearchProfile'];

    function SearchProfileController ($scope, $state, SearchProfile) {
        var vm = this;
        vm.searchProfiles = [];
        vm.loadAll = function() {
            SearchProfile.query(function(result) {
                vm.searchProfiles = result;
            });
        };

        vm.loadAll();
        
    }
})();
