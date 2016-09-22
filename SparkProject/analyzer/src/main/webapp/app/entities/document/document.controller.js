(function() {
    'use strict';

    angular
        .module('analyzerApp')
        .controller('DocumentController', DocumentController);

    DocumentController.$inject = ['$scope', '$state', 'DataUtils', 'Document', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants'];

    function DocumentController ($scope, $state, DataUtils, Document, ParseLinks, AlertService, pagingParams, paginationConstants) {
        var vm = this;
        vm.loadAll = loadAll;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.shuffleDates = shuffleDates;
        vm.loadAll();

        function loadAll () {
            Document.query({
                page: pagingParams.page - 1,
                size: paginationConstants.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
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
                vm.documents = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
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

        function shuffleDates(){
            var currentDate = new Date();
            var monthAgoDate = new Date();
            monthAgoDate.setMonth(currentDate.getMonth() - 24);
            Document.shuffleDates(
                {
                    from: monthAgoDate.toISOString().substring(0,10),
                    until: currentDate.toISOString().substring(0,10)
                }).$promise.then(function(){
                vm.loadAll();
            });
        }

    }
})();
