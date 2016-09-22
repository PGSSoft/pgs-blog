(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('ResultDialogController', ResultDialogController);

    ResultDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Result', 'Document', 'SearchProfile'];

    function ResultDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Result, Document, SearchProfile) {
        var vm = this;
        vm.result = entity;
        vm.documents = Document.query();
        vm.searchprofiles = SearchProfile.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('analyzerApp:resultUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.result.id !== null) {
                Result.update(vm.result, onSaveSuccess, onSaveError);
            } else {
                Result.save(vm.result, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
