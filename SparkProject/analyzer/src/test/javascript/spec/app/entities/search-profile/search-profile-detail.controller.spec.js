'use strict';

describe('Controller Tests', function() {

    describe('SearchProfile Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSearchProfile, MockSearchCriteria;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSearchProfile = jasmine.createSpy('MockSearchProfile');
            MockSearchCriteria = jasmine.createSpy('MockSearchCriteria');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SearchProfile': MockSearchProfile,
                'SearchCriteria': MockSearchCriteria
            };
            createController = function() {
                $injector.get('$controller')("SearchProfileDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'analyzerApp:searchProfileUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
