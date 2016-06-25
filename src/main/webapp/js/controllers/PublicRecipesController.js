iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', function($scope, $rootScope, $http, $location){
  $scope.publicRecipes = [];
  var self = this;
  $http({
    method : 'GET',
    url : 'api/publicrecipes'
  }).then(function successCallback(response){
    $scope.publicRecipes = response.data;


    for(var i in $scope.publicRecipes){
      (function downloadRecipeInfo(position){
        $http({
            url : 'api/publicrecipes/' + $scope.publicRecipes[position].id,
            method : 'GET'
          }).then(function successCallback(res){
            $scope.publicRecipes[position].triggerChannel = 'img/' + res.data.trigger.channel + '.png';
            $scope.publicRecipes[position].actionChannel = 'img/' + res.data.action.channel + '.png';
          });
      })(i);
    }

  }, function errorCallback(result){
    console.error(result);
  });

  self.importRecipe = function(recipeId){
    $location.path('/importPublicRecipe/'+ recipeId);
  }

}]);
