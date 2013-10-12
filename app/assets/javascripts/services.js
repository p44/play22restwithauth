'use strict';

// http://docs.angularjs.org/api/ng.$http

/** imAlive service, provides customers (could also be loaded from server) */
angular.module('simulator.services', []).
service('consumerModel', function () {
    var getSimulatedConsumer = function ($scope, $http) {
    	$http({method: 'GET', url: '/consumer/simulated'}). // , headers: {'foo': 'bar'}}).
    	  success(function(data, status, headers, config) {
    		console.log('GET /consumer/simulated')
    		console.log(data);
    		$scope.consumer_simulated = data;
    	  }).
    	  error(function(data, status, headers, config) {
    		console.log('GET /consumer/simulated ERROR ' + status)
    		$scope.consumer_simulated = [{id: 0, name: "None"}];
    	  });
    };
    return { getSimulatedConsumer: getSimulatedConsumer };
});