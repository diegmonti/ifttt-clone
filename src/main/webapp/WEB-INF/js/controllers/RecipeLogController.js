iftttclone.controller('RecipeLogController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        if ($rootScope.authenticated === false) {
            $location.path('/login');
        }

        var self = this;
        $scope.recipe = {};
        $scope.logs = [];
        $scope.hasNextPage = false;
        $scope.loaded = false;

        if ($routeParams.pageId === undefined || $routeParams.pageId < 0) {
            $scope.currentPage = 0;
        } else {
            $scope.currentPage = parseInt($routeParams.pageId, 10);
        }

        function checkNextPage() {
            $http.get('api/myrecipes/' + $routeParams.recipeId + '/logs?page=' + ($scope.currentPage + 1))
                .then(function successCallback(response) {
                    $scope.hasNextPage = (response.data.length !== 0);
                });
        }

        self.downloadLogs = function () {
            $http.get('api/myrecipes/' + $routeParams.recipeId)
                .then(function successCallback(response) {
                    $scope.recipe = response.data;
                }, function errorCallback(response) {
                    console.error(response);
                });

            checkNextPage();

            $http.get('api/myrecipes/' + $routeParams.recipeId + '/logs?page=' + $scope.currentPage)
                .then(function successCallback(response) {
                    response.data.forEach(function (element) {
                        $scope.logs.push({
                            event: element.event,
                            timestamp: moment(element.timestamp).calendar()
                        });
                    });
                    if (response.data.length === 0) {
                        self.error = true;
                        $scope.errorMessage = "There are no log entries to show.";
                    }
                    $scope.loaded = true;
                }, function errorCallback(response) {
                    console.error(response);
                    self.error = true;
                    $scope.errorMessage = "There was an error loading the log.";
                });
        };

        self.downloadLogs();
    }]);
