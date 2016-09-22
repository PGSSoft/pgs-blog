(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('DocumentDetailController', DocumentDetailController);

    DocumentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Document'];

    function DocumentDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Document) {
        var vm = this;
        vm.document = entity;
        
        var unsubscribe = $rootScope.$on('analyzerApp:documentUpdate', function(event, result) {
            vm.document = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();
