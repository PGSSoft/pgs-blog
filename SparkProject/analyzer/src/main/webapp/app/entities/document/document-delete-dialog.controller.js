(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('DocumentDeleteController',DocumentDeleteController);

    DocumentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Document'];

    function DocumentDeleteController($uibModalInstance, entity, Document) {
        var vm = this;
        vm.document = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Document.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
