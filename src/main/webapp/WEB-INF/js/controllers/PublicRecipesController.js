iftttclone.controller('PublicRecipesController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        var self = this;
        $scope.publicRecipes = [];
        $scope.hasNextPage = false;
        $scope.loaded = false;

        if ($routeParams.pageId === undefined || $routeParams.pageId < 0) {
            $scope.currentPage = 0;
        } else {
            $scope.currentPage = parseInt($routeParams.pageId, 10);
        }

        if ($routeParams.search === undefined) {
            $scope.prevPageLink = "#publicRecipes/" + ($scope.currentPage - 1);
            $scope.currPageLink = "#publicRecipes/" + $scope.currentPage;
            $scope.nextPageLink = "#publicRecipes/" + ($scope.currentPage + 1);
        } else {
            $scope.prevPageLink = "#publicRecipes/" + ($scope.currentPage - 1) + "/" + $routeParams.search;
            $scope.currPageLink = "#publicRecipes/" + $scope.currentPage + "/" + $routeParams.search;
            $scope.nextPageLink = "#publicRecipes/" + ($scope.currentPage + 1) + "/" + $routeParams.search;
        }

        function checkNextPage() {
            var recipesPromise;
            if ($routeParams.search === undefined) {
                recipesPromise = $http({
                    method: 'GET',
                    url: 'api/publicrecipes?page=' + ($scope.currentPage + 1)
                });
            } else {
                recipesPromise = $http({
                    method: 'GET',
                    url: 'api/publicrecipes?search=' + $routeParams.search + '&page=' + ($scope.currentPage + 1)
                });
            }
            recipesPromise.then(function successCallback(response) {
                $scope.hasNextPage = (response.data.length !== 0);
            });
        }

        function downloadPublicRecipes() {
            var recipesPromise;
            checkNextPage();
            if ($routeParams.search === undefined) {
                recipesPromise = $http({
                    method: 'GET',
                    url: 'api/publicrecipes?page=' + $scope.currentPage
                });
            } else {
                recipesPromise = $http({
                    method: 'GET',
                    url: 'api/publicrecipes?search=' + $routeParams.search + "&page=" + $scope.currentPage
                });
            }
            recipesPromise.then(function successCallback(response) {
                response.data.forEach(function (element) {
                    $scope.publicRecipes.push(element);
                });
                if ($scope.publicRecipes.length === 0) {
                    $scope.info = true;
                    $scope.infoMessage = "There are no public recipes to show.";
                }
                $scope.loaded = true;
            }, function errorCallback(response) {
                console.error(response);
                $scope.error = true;
                $scope.errorMessage = "There was a problem loading the public recipes.";
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
                for (i = 0; i < $scope.publicRecipes.length; i++) {
                    if ($scope.publicRecipes[i] === recipe) {
                        $scope.publicRecipes.splice(i, 1);
                        break;
                    }
                }
            }, function errorCallback(response) {
                console.error(response);
            });
        };

        downloadPublicRecipes();
    }]);
