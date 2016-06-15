var iftttclone = angular.module('schedulerApp', ['ngRoute']);

iftttclone.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider.when('/', {
        templateUrl: 'partials/channels.html',
        controller: 'ChannelsController',
        controllerAs : 'controller'
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
    .when('/channel/:channelID', {
      templateUrl: 'partials/channel.html',
      controller: 'ChannelController',
      controllerAs: 'controller'
    })
    .otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

iftttclone.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', '$location',
    function ($scope, $rootScope, $routeParams, $location, $http, $window, $location) {
      var self = this;
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
                    id: response.data[i].id,
                    title: response.data[i].name,
                    description: response.data[i].description,
                    link : "img/" +  response.data[i].id + ".png"
                };
            }
        }, function errorCallback(response) {
            console.log("error: " + response);
        });

        self.selectChannel = function(channelID){
          console.log("selectChannel" + channelID);

          $location.path('/channel/'+channelID);
        }
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

iftttclone.controller('LoginController', ['$rootScope', '$http', '$location',
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

iftttclone.controller('SignInController', ['$scope', '$rootScope', '$http', '$location',
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

iftttclone.controller('ChannelController', ['$scope', '$rootScope', '$http', '$location','$routeParams', function($scope, $rootScope, $http, $location, $routeParams){




  $http({
    method:'GET',
    url : 'api/channels/'+ $routeParams.channelID,
  }).then(
    function (response){ // successCallback
      $scope.channel = {};
      $scope.channel.title = response.data.name;
      $scope.channel.link = "img/"+response.data.id + ".png";
      $scope.channel.description = response.data.description;
      $scope.error = false;
      // TODO: add toConnect field!
    },
    function(response){ // error callback
      $scope.error = true;
    }
  );

}]);
