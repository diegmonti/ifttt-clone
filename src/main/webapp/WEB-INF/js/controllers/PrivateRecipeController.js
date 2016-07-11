iftttclone.controller('PrivateRecipeController', ['$scope', '$rootScope', '$http', '$location', function ($scope, $rootScope, $http, $location) {
    if ($rootScope.authenticated === false) {
        $location.path('/login');
    }

    var self = this;
    // First, I need to download all the recipes of this guy
    $scope.recipes = [];

    $http({
        method: 'GET',
        url: 'api/myrecipes'
    }).then(function successCallback(response) {
        $scope.error = false;

        // Calling this for each element of the array response.data
        response.data.forEach(function (element) {
            // Now I need to update the recipe so it also contains the name of the trigger and action channels
            var recipe = {
                id: element.id,
                title: element.title,
                created: moment(element.creationTime).calendar(),
                lastRun: moment(element.lastRun).calendar(),
                timesRun: element.runs,
                active: element.active,
                triggerChannelImage: 'img/' + element.trigger.channel + '.png',
                actionChannelImage: 'img/' + element.action.channel + '.png'
            };
            $scope.recipes.push(recipe);
        });
    }, function errorCallback(response) {
        console.error(response);
        $scope.error = true;
    });

    self.turnRecipeOnOff = function (recipeID) {
        $scope.recipes.forEach(function (element) {
            if (element.id === recipeID) {
                if (element.active === true) {
                    $http({
                        method: 'POST',
                        url: 'api/myrecipes/' + element.id + '/off'
                    }).then(function successCallback() {
                        element.active = false;
                    });
                } else { // if active === false
                    $http({
                        method: 'POST',
                        url: 'api/myrecipes/' + element.id + '/on'
                    }).then(function successCallback() {
                        element.active = true;
                    }, function errorCallback(result) {
                        $scope.error = true;
                        $scope.errorMessage = result.data.message;
                    });
                }
            }
        });
    };

    self.deleteRecipe = function (recipeID) {
        $http({
            method: 'DELETE',
            url: 'api/myrecipes/' + recipeID
        }).then(function successCallback() {
            var i;
            // If the function works, I need to remove it from the array
            for (i = 0; i < $scope.recipes.length; i++) {
                if ($scope.recipes[i].id === recipeID) {
                    $scope.recipes.splice(i, 1);
                    break;
                }
            }
        }, function errorCallback(result) {
            console.error(result);
            $scope.error = true;
            $scope.errorMessage = "There was an error.";
        });
    };

    self.modifyRecipe = function (recipeID) {
        $location.path('/modifyRecipe/' + recipeID);
    };

    self.publishRecipe = function (recipeID) {
        $location.path('/publishRecipe/' + recipeID);
    };

    self.showLog = function (recipeId) {
        $location.path('/recipeLog/' + recipeId);
    };

    self.selectRecipe = function (recipeId) {
        $scope.selectedRecipe = recipeId;
        angular.element('#deleteRecipeModalShower').trigger('click');
    };
}]);
