iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', '$routeParams', function ($scope, $rootScope, $http, $location, $routeParams) {
    $scope.publicRecipes = [];
    var self = this;
    self.currentPage = -1;
    self.hasNextPage = true;

    function downloadPublicRecipes(){
      var recipesPromise;
      self.currentPage++;
      checkNextPage();
      if($routeParams.search == null){
        recipesPromise = $http({
          method: 'GET',
          url: 'api/publicrecipes?page='+self.currentPage
        });
      } else{
          recipesPromise = $http({
            method: 'GET',
            url: 'api/publicrecipes?search='+$routeParams.search+"&page="+self.currentPage
          });
        }
      recipesPromise.then(function successCallback(response) {
        response.data.forEach(function(element){
          $scope.publicRecipes.push(element);
        });
        if ($scope.publicRecipes.length === 0) {
            $scope.info = true;
            $scope.infoMessage = "There are no public recipes to show.";
            return;
        }
          if ($scope.publicRecipes.length === 0) {
              $scope.info = true;
              $scope.infoMessage = "There are no public recipes to show.";
              return;
          }
      }, function errorCallback(result) {
          $scope.error = true;
          $scope.errorMessage = result.data.message;
      });
    }

    function checkNextPage(){
      var recipesPromise;
      if($routeParams.search == null){
        recipesPromise = $http({
          method: 'GET',
          url: 'api/publicrecipes?page='+(self.currentPage+1)
        });
      } else{
          recipesPromise = $http({
            method: 'GET',
            url: 'api/publicrecipes?search='+$routeParams.search + '&page='+(self.currentPage+1)
          });
        }
        recipesPromise.then(function successCallback(response){
          self.hasNextPage = (response.data.length != 0)
        })

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

    self.deleteRecipe = function (recipe, $event) {
        $http({
            method: 'DELETE',
            url: 'api/publicrecipes/' + recipe.id
        }).then(function successCallback() {
            var i;
            for (i = 0; i < $scope.publicRecipes.length; i++) {
                if ($scope.publicRecipes[i] === recipe) {
                    $scope.publicRecipes.splice(i, 1);
                    break;
                }
            }
        }, function errorCallback(response) {
            console.error(response);
        });

        $event.stopPropagation();
    };

    self.showMore = function(){
      downloadPublicRecipes();
    }

    downloadPublicRecipes();


}]);
