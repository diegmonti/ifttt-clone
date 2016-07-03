iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', function($scope, $rootScope, $http, $location){
  $scope.publicRecipes = [];
  var self = this;
  $http({
    method : 'GET',
    url : 'api/publicrecipes'
  }).then(function successCallback(response){

    $scope.publicRecipes = response.data;
    for(var i in $scope.publicRecipes){
      (function (position){
        $scope.publicRecipes[position].triggerChannel = 'img/' +  $scope.publicRecipes[position].trigger.channel + '.png';
        $scope.publicRecipes[position].actionChannel = 'img/' +  $scope.publicRecipes[position].action.channel + '.png';
      })(i);
    }

  }, function errorCallback(result){
    console.error(result);
  });

  self.importRecipe = function(recipeId){
    $location.path('/importPublicRecipe/'+ recipeId);
  }

}]);
