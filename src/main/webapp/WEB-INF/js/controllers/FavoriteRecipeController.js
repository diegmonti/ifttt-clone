iftttclone.controller('FavoriteRecipesController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    $scope.favoriteRecipes = [];
    var self = this;
    $http.get('api/publicrecipes/favorite').then(function successCallback(response) {
        $scope.favoriteRecipes = response.data;
        $scope.favoriteRecipes.forEach(function (recipe) {
            recipe.triggerChannel = 'img/' + recipe.trigger.channel + '.png';
            recipe.actionChannel = 'img/' + recipe.action.channel + '.png';
        });
    }, function errorCallback(result) {
        console.error(result);
    });

    self.importRecipe = function (recipeId) {
        $location.path('/importPublicRecipe/' + recipeId);
    };

}]);
