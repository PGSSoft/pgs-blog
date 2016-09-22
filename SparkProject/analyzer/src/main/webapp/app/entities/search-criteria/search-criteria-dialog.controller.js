(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('SearchCriteriaDialogController', SearchCriteriaDialogController);

    SearchCriteriaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SearchCriteria', 'SearchProfile'];

    function SearchCriteriaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SearchCriteria, SearchProfile) {
        var vm = this;
        vm.searchCriteria = entity;
        vm.searchprofiles = SearchProfile.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('analyzerApp:searchCriteriaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.searchCriteria.id !== null) {
                SearchCriteria.update(vm.searchCriteria, onSaveSuccess, onSaveError);
            } else {
                SearchCriteria.save(vm.searchCriteria, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
