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
        $scope.error = true;
        $scope.errorMessage = result.data.message;
    });

    self.importRecipe = function (recipeId) {
        $location.path('/importPublicRecipe/' + recipeId);
    };
    
    self.favoriteRecipe = function (recipe, $event) {
		console.log(recipe.id);
        var promise;
        if (recipe.favorite == true)
            promise = $http.post('api/publicrecipes/' + recipe.id + '/remove');
        else
            promise = $http.post('api/publicrecipes/' + recipe.id + '/add');

        promise.then(function successCallback() {
            recipe.favorite = !recipe.favorite;
        }, function errorCallback(response) {
            console.error(response);
        })
        
        $event.stopPropagation();
    }

}]);
