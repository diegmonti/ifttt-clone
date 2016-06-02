var schedulerApp = angular.module('schedulerApp', [ 'ngRoute' ]);

schedulerApp.config(['$routeProvider', function($routeProvider){
    $routeProvider.when('/index.html', {
        templateUrl: 'partials/channels.html',
        controller: 'ChannelsController'
    });
    $routeProvider.otherwise({redirectTo: '/index.html'});
}]);


schedulerApp.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location' , '$http',
    function ($scope, $rootScope, $routeParams, $location, $http) {
      // i need to download the channels. TODO: In future limit this number to 25 or something like that

      // Simple GET request example:
      $http({
      method: 'GET',
      url: '/ifttt-clone/api/channels'
      }).then(function successCallback(response) {


        console.log(response.data);

        $scope.channels = [];
        console.log(response.data.length/4+1);
        for(var i = 0; i < (response.data.length/4)+1; i++){
          $scope.channels[i] = [];
        }

        for(i = 0; i < response.data.length; i++){
          
          $scope.channels[Math.floor(i/3)][i%3] = {title:response.data[i].name, description:response.data[i].description};
        }
        console.log($scope.channels);

      }, function errorCallback(response) {
        console.log("errror: "+ response);
      });
    }]
  );
