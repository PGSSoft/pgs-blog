(function() {
    'use strict';
    angular
        .module('analyzerApp')
        .factory('SearchProfile', SearchProfile);

    SearchProfile.$inject = ['$resource'];

    function SearchProfile ($resource) {
        var resourceUrl =  'api/search-profiles/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
