(function(){
    'use strict';

    angular.module('analyzerApp').controller('ChartsController', ChartsController);

    ChartsController.$inject = ['$scope', '$state', 'Result', 'SearchProfile'];

    function ChartsController($scope, $state, Result, SearchProfile){
        var vm = this;
        vm.dateFrom = new Date();
        vm.dateUntil =  new Date();
        vm.dateFrom.setMonth(vm.dateFrom.getMonth() - 24);
        vm.scale = "WEEKS";

        SearchProfile.query(function(result){
            vm.searchProfiles = result;
            vm.selectedProfile = result[0].id;
            fetchData().then(function(){
                setUpWatchers();
            });
        });

        function fetchData(){
            return Result.chart(
                {
                    searchProfile: vm.selectedProfile,
                    from: vm.dateFrom.toISOString().substring(0,10),
                    until: vm.dateUntil.toISOString().substring(0,10),
                    scale: vm.scale
                },
                function(result){
                    vm.labels = result.labels;
                    vm.series = result.series;
                    vm.data = result.data;
                    vm.onClick = function (points, evt) {};
                },
                function(){
                    console.log("Failed to retrieve data for chart")
                }).$promise;
        }

        function setUpWatchers(){
            $scope.$watch('vm.dateFrom', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.dateFrom = newValue;
                    fetchData();
                }
            });

            $scope.$watch('vm.dateUntil', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.dateUntil = newValue;
                    fetchData();
                }
            });

            $scope.$watch('vm.scale', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.scale = newValue;
                    fetchData();
                }
            });

            $scope.$watch('vm.selectedProfile', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.selectedProfile = newValue;
                    fetchData();
                }
            });
        }
    }
})();
