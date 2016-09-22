'use strict';

describe('Controller Tests', function() {

    describe('SearchCriteria Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSearchCriteria, MockSearchProfile;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSearchCriteria = jasmine.createSpy('MockSearchCriteria');
            MockSearchProfile = jasmine.createSpy('MockSearchProfile');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SearchCriteria': MockSearchCriteria,
                'SearchProfile': MockSearchProfile
            };
            createController = function() {
                $injector.get('$controller')("SearchCriteriaDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'analyzerApp:searchCriteriaUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
