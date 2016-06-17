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
    .when('/myRecipes', {
      templateUrl: 'partials/recipes.html',
      controller: 'PrivateRecipeController',
      controllerAs: 'controller'
    })
    .when('/createRecipe', {
      templateUrl: 'partials/createRecipe.html',
      controller: 'CreateRecipeController',
      controllerAs: 'controller'
    })
    .otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

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

iftttclone.controller('ChannelController', ['$scope', '$rootScope', '$http', '$location','$routeParams', function($scope, $rootScope, $http, $location, $routeParams){

    $http({
        method:'GET',
        url : 'api/channels/'+ $routeParams.channelID
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

iftttclone.controller('PrivateRecipeController', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http){
    // first, i need to download all the recepies of this guy
    $scope.recipes = [];
    $http({
        method: 'GET',
        url: 'api/myrecipes'
    }).then(function successCallback(response) {
        $scope.error = false;
        console.log(response.data);
        response.data.forEach(function(element){
          // calling this for each element of the array response.data

          var recipe = {
            title : element.title,
            created : moment(element.creationTime).calendar(),
            lastRun : moment(element.lastRun).calendar(),
            timesTun : element.runs
          };
          $scope.recipes.push(recipe);
          // now i need to update the recipe so it also contains the name of the trigger and action channels

        $http({
          method: 'GET',
          url: 'api/myrecipes/' + element.id
        }).then(function successCallback(response){
          recipe.triggerChannelImage = 'img/'+response.data.triggerChannelId + '.png';
          recipe.actionChannelImage = 'img/'+response.data.actionChannelId + '.png';
        }, function errorCallback(response){
        });

        })
        }, function errorCallback(response) {
        $scope.error = true;
    });
}]);

iftttclone.controller('CreateRecipeController', ['$scope', '$rootScope', '$http', '$timeout', function($scope, $rootScope, $http, $timeout){

  var self = this;
  self.currentSelected = "";

  self.recipe = {

  };
  $scope.channels = [];

  function downloadChannels() {
    $scope.channels = [];
    $http({
      method: 'GET',
      url: 'api/channels'
    }).then(
      function successCallback(result){
          result.data.forEach(function(element) {
          $scope.channels.push({
            id : element.id,
            title : element.name,
            description : element.description,
            link : 'img/'+element.id + '.png'
          });
        });
      },
      function errorCallback(result){
        console.log(result);
      }
    );
  };

  function dowloadTriggers(){
    $http({
      method : 'GET',
      url : 'api/channels/'+self.recipe.triggerId
    }).then(
      function successCallback(result){

        $scope.triggers = [];

        for(var element in result.data.triggers)
          $scope.triggers.push({
            title: element,
            description : result.data.triggers[element].description
          });

        console.log($scope.triggers);


      },
      function errorCallback(result){

      }
    );
  }

  self.selectTriggerClicked = function(){
    console.log("selectTriggerClicked");
    self.currentSelected = "trigger";

    downloadChannels();
  };
  self.selectActionClicked = function(){
    self.currentSelected = "action";

    downloadChannels();
  };

  self.channelSelected = function (id){
    $scope.channels = [];
    var image = $('<img>');
    $(image).attr("src","img/"+id+".png");
    $(image).attr("width", "110px");

    if(self.currentSelected === "trigger"){
      self.recipe.triggerId = id;
      $("#triggerDiv").html(image);
      dowloadTriggers();
    }
    else if(self.currentSelected === "action"){
      self.recipe.triggerId = id;
      $("#actionDiv").html(image);
    }
  }




}]);
