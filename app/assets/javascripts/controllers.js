'use strict';

/** Controllers */
angular.module('simulator.controllers', ['simulator.services']).
controller('ConsumerCtrl', function ($scope, $http, consumerModel) {  
	$scope.consumer_simulated = consumerModel.getSimulatedConsumer($scope, $http);
});