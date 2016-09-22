(function(){
    angular
        .module('analyzerApp')
        .factory('socialMediaService', socialMediaService);

    socialMediaService.$inject = ['$resource'];

    function socialMediaService($resource){

        var resourceUrl = 'http://localhost:8283/api/socialMedia';

        return $resource(resourceUrl, {}, {
            'getSites': {
                method: 'GET',
                isArray: true
            },
            'getCurrentJob': {
                method: 'GET',
                url: resourceUrl + '/current'
            },
            'performJob': {
                method: 'GET',
                url: resourceUrl + '/perform'
            }
        });
    }
})();
