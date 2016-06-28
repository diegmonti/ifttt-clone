iftttclone.controller('ChannelController', ['$scope', '$rootScope', '$http', '$location','$routeParams', function($scope, $rootScope, $http, $location, $routeParams){

    var self = this;
    $scope.recipes = [];
    $scope.channel = {};

      $http({
          method:'GET',
          url : 'api/channels/'+ $routeParams.channelID
      }).then(
          function (response){ // successCallback
            $scope.channel.id = response.data.id;
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

            // now i need to download all the publicrecipes that are contained in this channel
            return $http({
              method : 'GET',
              url : 'api/publicrecipes'
            });
          },
          function(response){ // error callback
            $scope.error = true;
          }
      )
      .then(function successCallback(response){
        response.data.forEach(function (element){
          if(element.trigger.channel == $scope.channel.id || element.action.channel == $scope.channel.id){
            element.triggerChannel = 'img/' +  element.trigger.channel + '.png';
            element.actionChannel = 'img/' +  element.action.channel + '.png';
            $scope.recipes.push(element);
          }
        })
      });
  }]);
