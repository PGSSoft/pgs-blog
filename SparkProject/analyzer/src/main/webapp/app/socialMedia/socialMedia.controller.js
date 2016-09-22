(function(){
    angular
        .module('analyzerApp')
        .controller('socialMediaCtrl', socialMediaCtrl);

    socialMediaCtrl.$inject = ['socialMediaService', 'SearchProfile'];

    function socialMediaCtrl(socialMediaService, SearchProfile){
        var vm = this;
        vm.performCrawling = performCrawling;

        loadSearchProfiles();

        socialMediaService.getSites().$promise
            .then(function(result){
                vm.socialMedias = result;
                vm.socialMedia = vm.socialMedias[0];
            });

        function performCrawling(){
            if(!_.isNull(vm.selectedProfile) && !_.isEmpty(vm.socialMedia)) {
                socialMediaService.performJob({jobName: vm.socialMedia, id: vm.selectedProfile}).$promise
                    .then(function (result) {
                    });
            }
        }

        function loadSearchProfiles(){
            return SearchProfile.query(function(result){
                if(_.isEmpty(result)){
                    return;
                }
                vm.searchProfiles = result;
                vm.selectedProfile = result[0].id;
            });
        }
    }

})();
