iftttclone.controller('FavoriteRecipesController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    if ($rootScope.authenticated === false) {
        $location.path("/login");
    }

    $scope.favoriteRecipes = [];
    var self = this;

    $http.get('api/publicrecipes/favorite').then(function successCallback(response) {
        $scope.favoriteRecipes = response.data;
        if ($scope.favoriteRecipes.length === 0) {
            $scope.info = true;
            $scope.infoMessage = "You don't have any favorite recipe.";
            return;
        }
        $scope.favoriteRecipes.forEach(function (recipe) {
            recipe.triggerChannel = 'img/' + recipe.trigger.channel + '.png';
            recipe.actionChannel = 'img/' + recipe.action.channel + '.png';
        });
    }, function errorCallback(result) {
        console.error(result);
        $scope.error = true;
        $scope.errorMessage = "There was an error loading your favorite recipes.";
    });

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

}]);
