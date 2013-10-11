'use strict';

// http://docs.angularjs.org/api/ng.$http

/** imAlive service, provides customers (could also be loaded from server) */
angular.module('simulator.services', []).
service('consumerModel', function () {
    var getConsumer = function ($scope, $http) {
    	$http({method: 'GET', url: '/consumer/simulated'}). // , headers: {'foo': 'bar'}}).
    	  success(function(data, status, headers, config) {
    	    // this callback will be called asynchronously when the response is available
    		// data: [object Object],[object Object] from [{"id":1,"name":"Supreme ATMs"},{"id":2,"name":"Terminal Specialists"}]
    		console.log('GET /consumer/simulated')
    		for (var i = 0; i < data.length; i++) {
    		  console.log(data[i]);
    		}
    		$scope.consumer = data;
    	  }).
    	  error(function(data, status, headers, config) {
    		console.log('GET /consumer/simulated ERROR ' + status)
    		$scope.consumer = [{id: 0, name: "None"}];
    	  });
    };
    return { getConsumer: getConsumer };
});