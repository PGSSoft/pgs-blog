(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchProfileDialogController', SearchProfileDialogController);

    SearchProfileDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SearchProfile', 'SearchCriteria'];

    function SearchProfileDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SearchProfile, SearchCriteria) {
        var vm = this;
        vm.searchProfile = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('analyzerApp:searchProfileUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.searchProfile.id !== null) {
                SearchProfile.update(vm.searchProfile, onSaveSuccess, onSaveError);
            } else {
                SearchProfile.save(vm.searchProfile, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
