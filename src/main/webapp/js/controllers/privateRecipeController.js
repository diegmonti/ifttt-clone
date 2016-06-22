iftttclone.controller('PrivateRecipeController', ['$scope', '$rootScope', '$http', '$location', function($scope, $rootScope, $http, $location){

	var self = this;
	// first, i need to download all the recepies of this guy
    $scope.recipes = [];
    $http({
        method: 'GET',
        url: 'api/myrecipes'
    }).then(function successCallback(response) {
        $scope.error = false;
        response.data.forEach(function(element){
          // calling this for each element of the array response.data

          var recipe = {
            id : element.id,
            title : element.title,
            created : moment(element.creationTime).calendar(),
            lastRun : moment(element.lastRun).calendar(),
            timesTun : element.runs,
            active : element.active
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

    self.turnRecipeOnOff = function(recipeID){

      $scope.recipes.forEach(function (element){
        if(element.id === recipeID){
          if(element.active == true){
            $http({
              method: 'POST',
              url: 'api/myrecipes/' + element.id + '/off'
            }).then(function successCallback(){
              element.active = false;
            }, function errorCallback(){});
          }
          else { // if active == false
            $http({
              method: 'POST',
              url: 'api/myrecipes/' + element.id + '/on'
            }).then(function successCallback(){
              element.active = true;
            }, function errorCallback(){});
          }
        }
      })

    }

    self.deleteRecipe = function(recipeID){
      $http({
        method : 'DELETE',
        url : 'api/myrecipes/' + recipeID
      }).then(function successCallback(){
        // if the function works, i need to remove it from the array
        console.log('successCallback');
        for(var i = 0; i < $scope.recipes.length; i++){
          if($scope.recipes[i].id === recipeID){
            console.log('removing element ' + i + 'from array');
            $scope.recipes.splice(i, 1);
            console.log($scope.recipes);
            break;
          }
        }

      }, function errorCallback(response){console.log(response);});
    }
    self.modifyRecipe= function(recipeID){
			console.log('modufy recipe');
			$location.path('/modifyRecipe/'+ recipeID);
    }
}]);
