iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    $scope.publicRecipes = [];
    var self = this;
    $http({
        method: 'GET',
        url: 'api/publicrecipes'
    }).then(function successCallback(response) {
        $scope.publicRecipes = response.data;
        $scope.publicRecipes.forEach(function (recipe) {
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
