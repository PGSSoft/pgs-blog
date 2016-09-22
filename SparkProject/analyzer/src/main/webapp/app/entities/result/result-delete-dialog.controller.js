(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('ResultDeleteController',ResultDeleteController);

    ResultDeleteController.$inject = ['$uibModalInstance', 'entity', 'Result'];

    function ResultDeleteController($uibModalInstance, entity, Result) {
        var vm = this;
        vm.result = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Result.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
