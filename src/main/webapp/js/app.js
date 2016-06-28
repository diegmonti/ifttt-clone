var iftttclone = angular.module('iftttcloneApp', ['ngRoute']);

iftttclone.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider.when('/', {
        templateUrl: 'partials/channels.html',
        controller: 'ChannelsController',
        controllerAs : 'controller',

    }).when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController',
        controllerAs: 'controller',
    })
    .when('/signIn', {
      templateUrl: 'partials/signIn.html',
      controller: 'SignInController',
      controllerAs: 'controller',
    })
    .when('/channel/:channelID', {
      templateUrl: 'partials/channel.html',
      controller: 'ChannelController',
      controllerAs: 'controller',
    })
    .when('/myRecipes', {
      templateUrl: 'partials/recipes.html',
      controller: 'PrivateRecipeController',
      controllerAs: 'controller',
    })
    .when('/modifyRecipe/:recipeId', {
      templateUrl: 'partials/modifyRecipe.html',
      controller: 'ModifyRecipeController',
      controllerAs: 'controller',
    })
    .when('/createRecipe', {
      templateUrl: 'partials/createRecipe.html',
      controller: 'CreateRecipeController',
      controllerAs: 'controller',
    })
    .when('/publishRecipe/:recipeID', {
      templateUrl: 'partials/publishRecipe.html',
      controller: 'PublishRecipeController',
      controllerAs: 'controller',
    })
    .when('/importPublicRecipe/:publicRecipeId', {
      templateUrl: 'partials/importPublicRecipe.html',
      controller: 'ImportPublicRecipeController',
      controllerAs: 'controller',
    })
    .when('/publicRecipes', {
      templateUrl: 'partials/publicRecipes.html',
      controller: 'PublicRecipesController',
      controllerAs: 'controller',
    })
    .otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

iftttclone.factory('fieldInputFactory',  function(){
  var factory = {};
  factory.createInput = function(type, field, model){
    var input;
    if(type === 'TEMPERATURE'){
      input = $('<select>')
       .append($('<option>').val('C').text('Celsius'))
       .append($('<option>').val('F').text('Fahrenheit'));
    }
    else if (type === 'EMAIL') {
      input = $('<input>').attr('type', 'email');
    }
    else if(type === 'LONGTEXT'){
      input = $('<textarea>').attr({
        rows:"5"
      });
    }
    else if(type === 'NULLABLEINTEGER' || type === 'INTEGER'){
      input = $('<input>').attr('type', 'number');
      // now i need to transform the value in the corrispondent number
      field.value = Number(field.value);
    }
    else if (type === 'TIME') {
      input = $('<input>').attr({
       'type' : 'text',
       'placeholder' : 'hh:mm',
       'pattern' : '([0-1][0-9]:[0-5][0-9])|(2[0-3]:[0-5][0-9])'
      });
    }
    else if (type === 'TIMESTAMP') {
      input = $('<input>').attr({
       'type' : 'text',
       'placeholder' : 'dd/MM/yyyy HH:mm',
       pattern : '(0[1-9] | [1-2][0-9] | 3[0-1] )/(0[0-9] | 1[0-2])/([0-9][0-9][0-9][0-9]) ([0-1][0-9]:[0-5][0-9])|(2[0-3]:[0-5][0-9])'
      });
    }
    else {
      input = $('<input>').attr('type', 'text');
    }

    input.attr({
      class:"form-control",
      'aria-describedby':"basic-addon3" ,
      'data-ng-model': model
    });

    return input;
  }
  return factory;
});

iftttclone.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
    function ($scope, $rootScope, $routeParams, $location, $http, $window) {
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
        url: 'api/user/timezones'
      }).then(function successCallback(data){

        $scope.timezones = data.data;

      }, function errorCallback(data){
        console.error(data);
      });

      self.signIn = function (){
        $http({
          method: 'POST',
          url: 'api/user',
          data: {
                username : self.credentials.username,
                password : self.credentials.password,
                email : self.credentials.email,
                timezone : self.credentials.timezone
        },
        headers : {
          'Content-Type': 'application/json'
        }
      }).then(function () {
            $rootScope.authenticated = false;
            $location.path("/");
        });
      }

    }]);
