(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchProfileDeleteController',SearchProfileDeleteController);

    SearchProfileDeleteController.$inject = ['$uibModalInstance', 'entity', 'SearchProfile'];

    function SearchProfileDeleteController($uibModalInstance, entity, SearchProfile) {
        var vm = this;
        vm.searchProfile = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            SearchProfile.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
