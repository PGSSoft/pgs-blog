(function() {
    'use strict';
    angular
        .module('analyzerApp')
        .factory('Document', Document);

    Document.$inject = ['$resource', 'DateUtils'];

    function Document ($resource, DateUtils) {
        var resourceUrl =  'api/documents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.creationDate = DateUtils.convertLocalDateFromServer(data.creationDate);
                    data.updateDate = DateUtils.convertLocalDateFromServer(data.updateDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.creationDate = DateUtils.convertLocalDateToServer(data.creationDate);
                    data.updateDate = DateUtils.convertLocalDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.creationDate = DateUtils.convertLocalDateToServer(data.creationDate);
                    data.updateDate = DateUtils.convertLocalDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            },
            'shuffleDates': {
                method: 'GET',
                url: 'api/documents/shuffleDates'
            },
            'listByIds': {
                method: 'GET',
                url: 'api/documents/list'
            }
        });
    }
})();
