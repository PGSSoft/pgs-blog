(function() {
    'use strict';

    angular
        .module('analyzerApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'angularFileUpload',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'chart.js'
        ])
        .config(config)
        .run(run);

    config.$inject = ['ChartJsProvider'];

    function config (ChartJsProvider){
        ChartJsProvider.setOptions({
            responsive: true
        });
    }

    run.$inject = ['stateHandler', 'translationHandler'];

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }
})();
