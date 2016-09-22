(function() {
    'use strict';
    angular
        .module('analyzerApp')
        .factory('Result', Result);

    Result.$inject = ['$resource'];

    function Result ($resource) {
        var resourceUrl =  'api/results/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'chart': {
                url: 'api/results/chartData',
                method: 'GET'
            }
        });
    }
})();
