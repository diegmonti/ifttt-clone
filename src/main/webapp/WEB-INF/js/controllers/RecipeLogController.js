iftttclone.controller('RecipeLogController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        var self = this;
        $scope.recipe = {};
        $scope.logs = [];

        if ($routeParams.pageId === undefined || $routeParams.pageId < 0) {
            self.pageId = 0;
        } else {
            self.pageId = $routeParams.pageId;
        }

        $http.get('api/myrecipes/' + $routeParams.recipeId).then(function successCallback(response) {
            $scope.recipe = response.data;
        });

        $http.get('api/myrecipes/' + $routeParams.recipeId + '/logs?page=' + self.pageId).then(function successCallback(response) {
            if (response.data.length === 0) {
                self.error = true;
                $scope.errorMessage = "There are no log entries to show.";
                return;
            }
            response.data.forEach(function (element) {
                $scope.logs.push({
                    event: element.event,
                    timestamp: moment(element.timestamp).calendar()
                });
            });
        }, function errorCallback() {
            self.error = true;
            $scope.errorMessage = "There was an error loading the log.";
        });
    }]);
