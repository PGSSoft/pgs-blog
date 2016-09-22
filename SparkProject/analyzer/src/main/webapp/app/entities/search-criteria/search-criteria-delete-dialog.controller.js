(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchCriteriaDeleteController',SearchCriteriaDeleteController);

    SearchCriteriaDeleteController.$inject = ['$uibModalInstance', 'entity', 'SearchCriteria'];

    function SearchCriteriaDeleteController($uibModalInstance, entity, SearchCriteria) {
        var vm = this;
        vm.searchCriteria = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            SearchCriteria.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
