(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('DocumentDialogController', DocumentDialogController);

    DocumentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Document'];

    function DocumentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Document) {
        var vm = this;
        vm.document = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('analyzerApp:documentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.document.id !== null) {
                Document.update(vm.document, onSaveSuccess, onSaveError);
            } else {
                Document.save(vm.document, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.creationDate = false;
        vm.datePickerOpenStatus.updateDate = false;

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
