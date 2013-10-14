'use strict';

/** Controllers */
angular.module('simulator.controllers', ['simulator.services']).
controller('ConsumerCtrl', function ($scope, $http, consumerModel) {  
	$scope.consumer_simulated = consumerModel.getSimulatedConsumer($scope, $http);
	$scope.consumer_simulated_fetched = JSON.parse('{"id":0,"name":"none"}');
	$scope.consumer_simulated_token = JSON.parse('{"token_type":"bearer","access_token":"none"}');
	$scope.consumer_simulated_cred = 'Basic vcncb4TTuPPTEGLOSKIex:L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg';
	
	$scope.getConsumerSimulated = function() {
       var url = '/consumer/simulated';
	   $http({method: 'GET', url: url}).
	      success(function(data, status, headers, config) {
		     console.log(url);
		     console.log(data);
		     $scope.consumer_simulated_fetched = data;
	   }).
	      error(function(data, status, headers, config) {
		     console.log('GET ' + url + ' ERROR ' + status)
		     $scope.consumer_simulated_fetched = JSON.parse('{"id":0,"name":"none"}');
	   });
	}
	
	$scope.postToken = function() {
	   var pUrl = '/token';
	   $http({method: 'POST', url: pUrl,
		      headers: {'Authorization': $scope.consumer_simulated_cred, 'Content-Type': 'application/json'},
			  data: {}}).
	      success(function(data, status, headers, config) {
	  		 console.log(pUrl);
	  		 console.log(data);
	  		 $scope.consumer_simulated_token = data;
	   }).
	      error(function(data, status, headers, config) {
	  	     console.log('POST ' + pUrl + ' ERROR ' + status)
	  		 $scope.consumer_simulated_token = JSON.parse('{"token_type":"bearer","access_token":"none"}');
	   });
	}
	
	$scope.invalidateToken = function() {
	   var pUrl = '/invalidate_token';
	   $http({method: 'POST', url: pUrl,
			  headers: {'Authorization': $scope.consumer_simulated_cred, 'Content-Type': 'application/json'},
			  data: {'access_token': $scope.consumer_simulated_token.access_token}}).
		success(function(data, status, headers, config) {
		  	  console.log(pUrl);
		  	  console.log(data);
		  	  $scope.consumer_simulated_token = JSON.parse('{"token_type":"bearer","access_token":"none"}');
		}).
		error(function(data, status, headers, config) {
		  	  console.log('POST ' + pUrl + ' ERROR ' + status)
		  	  // no change to the local bearer token on failed invalidate
	   });
	}
});