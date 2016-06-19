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

  var self = this;
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

          if(response.data.connectionTime === null && response.data.withConnection === true){
            $scope.toConnect = true;
            $scope.channel.activate = 'api/channels/'+ $routeParams.channelID + '/activate';
          }
          else
            $scope.toConnect = false;
        },
        function(response){ // error callback
          $scope.error = true;
        }
    );

    self.activateChannel = function(url){
      $http({
        method : 'POST',
        url : url
      }).then(function successCallback(response){
        console.log(response);

    }, function errorCallback(response) {
      console.error(response);
    })

    }
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
          recipe.triggerChannelImage = 'img/'+response.data.trigger.channel + '.png';
          recipe.actionChannelImage = 'img/'+response.data.action.channel + '.png';
        }, function errorCallback(response){
        });

        })
        }, function errorCallback(response) {
        $scope.error = true;
    });
}]);

iftttclone.controller('CreateRecipeController', ['$scope', '$rootScope', '$http', '$timeout', '$compile', '$location', function($scope, $rootScope, $http, $timeout, $compile, $location){

  var self = this;
  self.currentSelected = "";

  $scope.recipe = {};
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
      url : 'api/channels/'+  $scope.recipe.trigger.channel
    }).then(
      function successCallback(result){

        $scope.triggers = [];

        for(var element in result.data.triggers)
          $scope.triggers.push({
            title: element,
            description : result.data.triggers[element].description
          });
      },
      function errorCallback(result){

      });
  }

  function downloadTriggerFields(){
    $scope.triggers = [];
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.trigger.channel
    }).then(
      function successCallback(result){
          var index;
          for(index in result.data.triggers[ $scope.recipe.trigger.method].triggerFields){
            var element = result.data.triggers[$scope.recipe.trigger.method].triggerFields[index];
            var div = $('<div>').attr({class : 'form-group row'});
            var label = $('<label>').attr({class : 'form-control-label'}).text(element.name);
            var input;
            (function (model){
                input = $('<input>').attr({
                class : 'form-control',
                type : 'text',
                placeholder : element.description,
                'data-ng-model' : model
              });
              console.log(model);
            })('recipe.recipeTriggerFields.' + index + '.value');
            $compile(input)($scope);

            div.append(label).append(input);
            $('#triggerFieldsDiv').append(div);
          }
          div = $('<div>').attr({class : 'form-group row'});

          var button = $('<button>').attr({
            class : 'btn btn-primary col-lg-4 col-lg-offset-3',
          }).text("Accetta");
          div.append(button);
          $('#triggerFieldsDiv').append(div);
          button.on('click', self.acceptTriggerFields);

      },
      function errorCallback(result){});
  }

  function downloadActions(){
    console.log('asking to ' + 'api/channels/'+  $scope.recipe.action.channel);
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.action.channel
    }).then(
      function successCallback(result){

        $scope.actions = [];
        console.log();
        for(var element in result.data.actions)
          $scope.actions.push({
            title: element,
            description : result.data.actions[element].description
          });
      },
      function errorCallback(result){

      });
  }

  function downloadActionFields(){
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.action.channel
    }).then(
      function successCallback(result){

        var index;
        for(index in result.data.actions[  $scope.recipe.action.method].actionFields){
          var element = result.data.actions[  $scope.recipe.action.method].actionFields[index];
          var div = $('<div>').attr({class : 'form-group row'});
          var label = $('<label>').attr({class : 'form-control-label'}).text(element.name);
          var input;
          (function(model){
              input = $('<input>').attr({
                class : 'form-control',
                type : 'text',
                placeholder : element.description,
                'data-ng-model' : model
              });
          })('recipe.recipeActionFields.' + index + '.value');
          $compile(input)($scope);
          div.append(label).append(input);
          $('#actionFieldsDiv').append(div);
        }
        div = $('<div>').attr({class : 'form-group row'});
        var button = $('<button>').attr({
          class : 'btn btn-primary col-lg-4 col-lg-offset-3',
        }).text("Accetta");
        div.append(button);
        $('#actionFieldsDiv').append(div);
        button.on('click', self.acceptActionsFields);

      },
      function errorCallback(result){});
  }

  self.selectTriggerClicked = function(){
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
    	$scope.recipe.trigger = {};
        $scope.recipe.trigger.channel = id;
      $("#triggerDiv").html(image);
      dowloadTriggers();
    }
    else if(self.currentSelected === "action"){
    	$scope.recipe.action = {};
        $scope.recipe.action.channel = id;
      $("#actionDiv").html(image);
      downloadActions();
    }
  }

  self.triggerSelected = function(id){
      $scope.recipe.trigger.method = id;
    downloadTriggerFields();
  }

  self.acceptTriggerFields = function(){

    $('#triggerFieldsDiv').hide();

    var link = $('<a>').attr({
      class : 'btn btn-link'
    }).text('that').on('click', self.selectActionClicked);
    $('#actionDiv').html(link)
  }

  self.actionSelected = function(id){
    $scope.recipe.action.method = id;
    $scope.actions = [];
    downloadActionFields();
  }

  self.acceptActionsFields = function(){
    $('#actionFieldsDiv').hide();
    console.log(  JSON.stringify($scope.recipe));

    var button = $('<button>').attr({
      class : 'btn btn-primary col-lg-4 col-lg-offset-3'
    }).text('Conferma');
    button.on('click', createRecipe);
    $('#confirmDiv').append(button);
  }

  function createRecipe(){
    $http({
      method : 'POST',
      url : 'api/myrecipes',
      data : JSON.stringify($scope.recipe),
      headers : {
        'Content-Type': 'application/json'
      }
    }).then(function successCallback(result){
      $location.path('myRecipes');

    }, function errorCallback(result){
      $scope.error = true;
      $scope.errorMessage = result.data.message;
    });
  }
}]);
