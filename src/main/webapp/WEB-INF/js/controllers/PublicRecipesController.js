iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    $scope.publicRecipes = [];
    var self = this;
    $http({
        method: 'GET',
        url: 'api/publicrecipes'
    }).then(function successCallback(response) {
        $scope.publicRecipes = response.data;
        if ($scope.publicRecipes.length === 0) {
            $scope.info = true;
            $scope.infoMessage = "There are no public recipes to show.";
            return;
        }
        $scope.publicRecipes.forEach(function (recipe) {
            recipe.triggerChannel = 'img/' + recipe.trigger.channel + '.png';
            recipe.actionChannel = 'img/' + recipe.action.channel + '.png';
        });
    }, function errorCallback(result) {
        $scope.error = true;
        $scope.errorMessage = result.data.message;
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
}]);
