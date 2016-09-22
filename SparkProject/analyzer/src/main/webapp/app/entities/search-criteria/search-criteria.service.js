(function() {
    'use strict';
    angular
        .module('analyzerApp')
        .factory('SearchCriteria', SearchCriteria);

    SearchCriteria.$inject = ['$resource'];

    function SearchCriteria ($resource) {
        var resourceUrl =  'api/search-criteria/:id';

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
