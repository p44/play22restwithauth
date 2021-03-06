'use strict';

/** Controllers */
angular.module('simulator.controllers', ['simulator.services']).
controller('ConsumerCtrl', function ($scope, $http, consumerModel) {  
	$scope.consumer_simulated = consumerModel.getSimulatedConsumer($scope, $http);
	$scope.consumer_simulated_fetched = JSON.parse('{"id":0,"name":"none"}');
	$scope.consumer_simulated_token = JSON.parse('{"token_type":"bearer","access_token":"none"}');
	$scope.consumer_simulated_cred = 'Basic vcncb4TTuPPTEGLOSKIex:L9yyTYyQg9ieKLOPhWolMNVvKUUw8iE7777777yOg';
	$scope.consumer_simulated_callback_url = 'none';
	$scope.simulator_callback_url = 'http://localhost:9000/simulator/callback/alarm';
	$scope.callback_msgs = [];
	
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
	
	$scope.getCallbackRegistration = function() { 
		var gUrl = '/consumer/'+ $scope.consumer_simulated.id + '/callbackregistration';
		$http({method: 'GET', url: gUrl,
		    headers: {'Authorization': 'bearer ' + $scope.consumer_simulated_token.access_token, 'Content-Type': 'application/json'}}).
		success(function(data, status, headers, config) {
			console.log(gUrl);
			console.log(data);
			$scope.consumer_simulated_callback_url = data.url;
	    }).
		error(function(data, status, headers, config) {
			console.log('GET ' + gUrl + ' ERROR ' + status)
			$scope.consumer_simulated_callback_url = 'none';
		});
	}
	
	$scope.putCallbackRegistration = function() { // {"consumerId":99,"url":"https://notaurl99.net/mycallback/response"}
		var pUrl = '/consumer/'+ $scope.consumer_simulated.id + '/callbackregistration';
		$http({method: 'PUT', url: pUrl,
			headers: {'Authorization': 'bearer ' + $scope.consumer_simulated_token.access_token, 'Content-Type': 'application/json'},
			data: {'consumerId': $scope.consumer_simulated.id, 'url': $scope.simulator_callback_url}}).
		success(function(data, status, headers, config) {
			console.log(pUrl);
			console.log(data);
			$scope.consumer_simulated_callback_url = data.url;
		}).
		error(function(data, status, headers, config) {
			console.log('PUT ' + pUrl + ' ERROR ' + status)
		});	
	}
	
	$scope.putAlarm = function() {
		var pUrl = '/consumer/'+ $scope.consumer_simulated.id + '/alarm';
		$http({method: 'PUT', url: pUrl,
			headers: {'Authorization': 'bearer ' + $scope.consumer_simulated_token.access_token, 'Content-Type': 'application/json'},
			data: {'consumerId': $scope.consumer_simulated.id, 'level': 'emergency'}}).
		success(function(data, status, headers, config) {
			console.log(pUrl);
			console.log(data);
		}).
		error(function(data, status, headers, config) {
			console.log('PUT ' + pUrl + ' ERROR ' + status)
		});
	}
	
	
	/** handle incoming callback alarms: add to callback_msgs array */
    $scope.addMsg = function (msg) { 
    	var msgobj = JSON.parse(msg.data);
    	console.log(msgobj);
        $scope.$apply(function () { $scope.callback_msgs.push(msgobj); });
    };
	
	/** start listening on callback_msgs (alarms) */
    $scope.listen = function () {
    	$scope.callbackfeed = new EventSource("/simulator/feed/livestatus"); 
        $scope.callbackfeed.addEventListener("message", $scope.addMsg, false);
    };

    $scope.listen();
	
});