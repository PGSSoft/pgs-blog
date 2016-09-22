(function() {
    angular
        .module('analyzerApp')
        .controller('AlgorithmComparisonController', AlgorithmComparisonController);

    AlgorithmComparisonController.$inject = ['$scope', '$interval', 'ComparisonService', 'SearchProfile'];

    function AlgorithmComparisonController($scope, $interval, ComparisonService, SearchProfile){
        var vm = this;
        vm.dateFrom = new Date();
        vm.dateUntil =  new Date();
        vm.dateFrom.setMonth(vm.dateFrom.getMonth() - 24);
        vm.scale = "MONTHS";

        vm.performComparison = performComparison;
        vm.loadComparisonData = loadComparisonData;
        vm.loadSearchProfiles = loadSearchProfiles;
        vm.loadSearchProfiles().$promise.then(function(){
            vm.loadComparisonData();
            setUpWatchers();
        });

        var promise = $interval(function(){
            vm.loadComparisonData();
        }, 30000);

        $scope.$on('destroy', function(){
            $interval.cancel(promise);
        });

        function performComparison(){
            ComparisonService.performComparison({searchProfile: vm.selectedProfile, trainingRatio: vm.trainingRatio})
                .$promise.then(function(){
                vm.loadComparisonData();
            });
        }

        function loadComparisonData(){
            if(_.isEmpty(vm.selectedProfile)){
                return;
            }
            ComparisonService.getComparisonData({
                    searchProfile: vm.selectedProfile,
                    from: vm.dateFrom.toISOString().substring(0,10),
                    until: vm.dateUntil.toISOString().substring(0,10),
                    scale: vm.scale
                },
                function(result){
                    console.log(result);
                    var chartMaxValue = getYAxisChartScale(result);
                    var numberOfSteps = 10;
                    vm.chartOptions = {
                        scaleShowVerticalLines: false,
                        scaleOverride: true,
                        scaleSteps: numberOfSteps,
                        scaleStepWidth: Math.ceil(chartMaxValue/numberOfSteps),
                        scaleStartValue: 0
                    };
                    vm.comparisonData = result;
                    // $scope.$applyAsync();
                    vm.onClick = function (points, evt) {};
                },
                function(){
                    console.log("Failed to retrieve data for chart")
                });
        }

        function getYAxisChartScale(chartData){
            var maxValue = 0;
            for(var attr in chartData){
                if(chartData.hasOwnProperty(attr)) {
                    var algorithmResults = chartData[attr].data;
                    if(algorithmResults) {
                        algorithmResults.forEach(function (valueGroup) {
                            var currentMax = Math.max.apply(null, valueGroup);
                            if(currentMax > maxValue){
                                maxValue = currentMax;
                            }
                        })
                    }
                }
            }
            return maxValue;
        }

        function loadSearchProfiles(){
            return SearchProfile.query(function(result){
                vm.searchProfiles = result;
                if(!_.isEmpty(vm.searchProfiles)) {
                    vm.selectedProfile = result[0].id;
                }
            });
        }

        function setUpWatchers(){
            $scope.$watch('vm.dateFrom', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.dateFrom = newValue;
                    loadComparisonData();
                }
            });

            $scope.$watch('vm.dateUntil', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.dateUntil = newValue;
                    loadComparisonData();
                }
            });

            $scope.$watch('vm.scale', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.scale = newValue;
                    loadComparisonData();
                }
            });

            $scope.$watch('vm.selectedProfile', function(newValue, oldValue){
                if(newValue !== oldValue) {
                    vm.selectedProfile = newValue;
                    loadComparisonData();
                }
            });
        }
    }
})();
