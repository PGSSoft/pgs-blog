(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchCriteriaController', SearchCriteriaController);

    SearchCriteriaController.$inject = ['$scope', '$state', 'SearchCriteria', 'SearchProfile'];

    function SearchCriteriaController ($scope, $state, SearchCriteria, SearchProfile) {
        var vm = this;
        vm.searchCriteria = [];
        vm.loadAll = function() {
            SearchProfile.query(function(result) {
                var searchProfiles = result;
                SearchCriteria.query({
                    searchProfileIds: _.map(result, function(r){return r.id;})},
                    function(result) {
                        vm.searchCriteria = result;
                    }
                );
            });
        };

        vm.loadAll();

    }
})();
