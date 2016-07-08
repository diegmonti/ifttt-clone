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

        self.downloadPublishedRecipes();
    }]);
