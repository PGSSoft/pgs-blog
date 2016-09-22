(function() {
    angular
        .module('analyzerApp')
        .controller('JobsController', JobsController);

    JobsController.$inject = ['$scope', 'Processor', 'SearchProfile'];

    function JobsController($scope, Processor, SearchProfile){
        var vm = this;
        vm.loadJobs = loadJobs;
        vm.executeJob = executeJob;
        vm.loadSearchProfiles = loadSearchProfiles;

        vm.loadSearchProfiles();
        vm.loadJobs();

        function loadJobs(){
            Processor.getPossibleJobs({}, onSuccess, onError);

            function onSuccess(data, headers){
                vm.possibleJobs = data;
            }

            function onError(error) {
                console.log(error);
            }
        }

        function loadSearchProfiles(){
            SearchProfile.query(function(result){
               if(_.isEmpty(result)){
                   return;
               }
               vm.searchProfiles = result;
               vm.selectedProfile = result[0].id;
            });
        }

        function executeJob(job){
            Processor.performJob({jobName: job, searchProfile: vm.selectedProfile})
        }

    }
})();
