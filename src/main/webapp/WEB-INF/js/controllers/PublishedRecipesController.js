iftttclone.controller('PublishedRecipesController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        if ($rootScope.authenticated === false) {
            $location.path("/login");
        }

        var self = this;
        $scope.publishedRecipes = [];
        $scope.hasNextPage = false;
        $scope.loaded = false;

        if ($routeParams.pageId === undefined || $routeParams.pageId < 0) {
            $scope.currentPage = 0;
        } else {
            $scope.currentPage = parseInt($routeParams.pageId, 10);
        }

        function checkNextPage() {
            $http.get('api/publicrecipes/published?page=' + ($scope.currentPage + 1))
                .then(function successCallback(response) {
                    $scope.hasNextPage = (response.data.length !== 0);
                });
        }

        self.downloadPublishedRecipes = function () {
            checkNextPage();
            $http.get('api/publicrecipes/published?page=' + $scope.currentPage)
                .then(function successCallback(response) {
                    response.data.forEach(function (element) {
                        $scope.publishedRecipes.push(element);
                    });
                    if ($scope.publishedRecipes.length === 0) {
                        $scope.info = true;
                        $scope.infoMessage = "You don't have any published recipe.";
                    }
                    $scope.loaded = true;
                }, function errorCallback(response) {
                    console.error(response);
                    $scope.error = true;
                    $scope.errorMessage = "There was an error loading your published recipes.";
                });
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

        self.selectRecipe = function (recipe, $event) {
            $scope.selectedRecipe = recipe;
            angular.element('#deleteRecipeModalShower').trigger('click');
            $event.stopPropagation();
        };

        self.deleteRecipe = function (recipe) {
            $http({
                method: 'DELETE',
                url: 'api/publicrecipes/' + recipe.id
            }).then(function successCallback() {
                var i;
                for (i = 0; i < $scope.publishedRecipes.length; i++) {
                    if ($scope.publishedRecipes[i] === recipe) {
                        $scope.publishedRecipes.splice(i, 1);
                        break;
                    }
                }
            }, function errorCallback(response) {
                console.error(response);
            });
        };

        self.downloadPublishedRecipes();
    }]);
