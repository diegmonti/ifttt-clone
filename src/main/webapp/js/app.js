var schedulerApp = angular.module('schedulerApp', ['ngRoute']);

schedulerApp.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider.when('/', {
        templateUrl: 'partials/channels.html',
        controller: 'ChannelsController'
    }).when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController',
        controllerAs: 'controller'
    })
    .when('/signIn', {
      templateUrl: 'partials/signIn.html',
      controller: 'SignInController',
      controllerAs: 'controller'
    })
    .otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

schedulerApp.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
    function ($scope, $rootScope, $routeParams, $location, $http, $window) {

        $scope.channels = [];
        $http({
            method: 'GET',
            url: 'api/channels'
        }).then(function successCallback(response) {

            for (var i = 0; i < (response.data.length / 4) + 1; i++) {
                $scope.channels[i] = [];
            }

            for (i = 0; i < response.data.length; i++) {
                $scope.channels[Math.floor(i / 3)][i % 3] = {
                    title: response.data[i].name,
                    description: response.data[i].description,
                    link : "img/" +  response.data[i].id + ".png"
                };
            }
        }, function errorCallback(response) {
            console.log("error: " + response);
        });

        $scope.connectToService = function(postUrl) {
          console.log(postUrl);
          $http({
            method:'POST',
            url : postUrl
          }).then(function successCallback (response){
            console.log(response);
            console.log(response.data.url);
            $window.open(response.data.url, '_blank');

          }, function errorCallback(response){
            console.log(response);
          });
        }


    }]
);

schedulerApp.controller('LoginController', ['$rootScope', '$http', '$location',
    function ($rootScope, $http, $location) {
        var self = this;

        var authenticate = function (credentials, callback) {

            var headers = credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('api/user', {headers: headers}).then(function (response) {
                if (response.data.username) {
                    $rootScope.authenticated = true;
                } else {
                    $rootScope.authenticated = false;
                }
                callback && callback();
            }, function () {
                $rootScope.authenticated = false;
                callback && callback();
            });

        };

        authenticate();
        self.credentials = {};
        self.login = function () {
            authenticate(self.credentials, function () {
                if ($rootScope.authenticated) {
                    $location.path("/");
                    self.error = false;
                } else {
                    $location.path("/login");
                    self.error = true;
                }
            });
        };

        self.logout = function () {
            $http.post('api/user/logout', {}).then(function () {
                $rootScope.authenticated = false;
                $location.path("/");
            });
        }
    }]);

schedulerApp.controller('SignInController', ['$scope', '$rootScope', '$http', '$location',
    function ($scope, $rootScope, $http, $location) {
      var self = this;
      self.credentials = {};

      $http({
        method: 'GET',
        url: 'api/user/timezones',
      }).then(function successCallback(data){
        $scope.timezones = data.data;
      }, function errorCallback(data){
        console.error(data);
      });

      self.signIn = function (){
        $http({
          method: 'POST',
          url: 'api/user',
          data: $.param({
                username : self.credentials.username,
                password : self.credentials.password,
                email : self.credentials.email,
                timezone : self.credentials.timezone
        }),
        headers : {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
            }
      }).then(function () {
            $rootScope.authenticated = false;
            $location.path("/");
        });
      }

    }]);
