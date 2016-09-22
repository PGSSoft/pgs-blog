(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('DataTrainingCtrl', DataTrainingCtrl);

    DataTrainingCtrl.$inject = ['$scope', '$state', 'Result', 'ParseLinks', 'SearchProfile', 'AlertService', 'pagingParams', 'paginationConstants', 'Document', 'fileUploadService'];

    function DataTrainingCtrl($scope, $state, Result, ParseLinks, SearchProfile, AlertService, pagingParams, paginationConstants, Document, fileUploadService){
        var vm = this;
        vm.loadAll = loadAll;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.performRating = performRating;
        vm.loadAll();

        $scope.trainingUploader = fileUploadService.getUploader('http://localhost:8283/api/crawler/fileUpload/training');
        $scope.validationUploader = fileUploadService.getUploader('http://localhost:8283/api/crawler/fileUpload/validation');

        function loadAll () {
            SearchProfile.query(function(result) {
                var searchProfiles = result;
                if (!_.isEmpty(searchProfiles)) {
                    Result.query({
                        page: pagingParams.page - 1,
                        size: paginationConstants.itemsPerPage,
                        sort: sort(),
                        searchProfileIds: _.map(searchProfiles, function (p) {
                            return p.id
                        })
                    }, onSuccess, onError);
                }
            });
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.results = data;
                vm.page = pagingParams.page;
                requestDocumentsFromResults(vm.results);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function requestDocumentsFromResults(results){
            var documentsIds = _.map(results, function(result){
                return result.documentId;
            });
            if(!_.isEmpty(documentsIds)) {
                Document.listByIds({documentsIds: documentsIds}).$promise
                    .then(function (result) {
                        vm.documents = result;
                    }, function (err) {
                    });
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function performRating(result, rating){
            result.classification = rating;
            result.isTrainingData = true;
            Result.update(result).$promise.then(function(updatedResult){
                result = updatedResult;
            }, function(err){});
        }
    }
})();
