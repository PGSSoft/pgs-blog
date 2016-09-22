(function() {
    'use strict';
    angular
        .module('analyzerApp')
        .factory('ComparisonService', ComparisonService);

    ComparisonService.$inject = ['$resource'];

    function ComparisonService($resource){
        var resourceUrl = 'http://localhost:8383/api/algorithmComparator/';

        return $resource(resourceUrl, {}, {
            'performComparison': {
                method: 'GET',
                url: resourceUrl + 'performComparison'
            },

            'getComparisonData': {
                method: 'GET',
                url: resourceUrl + 'getComparisonData'
            }
        })
    }
})();
