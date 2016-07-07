iftttclone.controller('FavoriteRecipesController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    if ($rootScope.authenticated === false) {
        $location.path("/login");
    }
    $scope.favoriteRecipes = [];
    var self = this;
    self.currentPage = -1;
    self.hasNextPage = false;

    function checkMorePages(){
      $http.get('api/publicrecipes/favorite?page='+self.currentPage+1).then(function successCallback(response) {
        console.log(response);
        self.hasNextPage = (response.data.length != 0);
        console.log(self.hasNextPage);
      });
    }

    self.downloadFavoriteRecipes = function(){
      self.currentPage++;
      console.log('self.currentPage=' + self.currentPage);
      checkMorePages();
      $http.get('api/publicrecipes/favorite?page='+self.currentPage).then(function successCallback(response) {
          response.data.forEach(function (recipe) {
            $scope.favoriteRecipes.push(recipe);
          });
          if ($scope.favoriteRecipes.length === 0) {
              $scope.info = true;
              $scope.infoMessage = "You don't have any favorite recipe.";
              return;
          }
      }, function errorCallback(result) {
          console.error(result);
          $scope.error = true;
          $scope.errorMessage = "There was an error loading your favorite recipes.";
      });
    }

    self.importRecipe = function (recipeId) {
        $location.path('/importPublicRecipe/' + recipeId);
    };

    self.favoriteRecipe = function (recipe, $event) {
        function errorCallback(response) {
            console.error(response);
        }

        if (recipe.favorite === true) {
            $http.post('api/publicrecipes/' + recipe.id + '/remove').then(function successCallback() {
                recipe.favorite = false;
                recipe.favorites--;
            }, errorCallback);
        } else {
            $http.post('api/publicrecipes/' + recipe.id + '/add').then(function successCallback() {
                recipe.favorite = true;
                recipe.favorites++;
            }, errorCallback);
        }

        $event.stopPropagation();
    };

    self.downloadFavoriteRecipes();
}]);
