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
