iftttclone.controller('RecipeLogController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        $scope.recipe = {};
        $scope.logs = [];

        $http.get('api/myrecipes/' + $routeParams.recipeId).then(function successCallback(response) {
            $scope.recipe = response.data;
        });
        $http.get('api/myrecipes/' + $routeParams.recipeId + '/logs').then(function successCallback(response) {
            response.data.forEach(function (element) {
                $scope.logs.push({
                    event: element.event,
                    timestamp: moment(element.timestamp).calendar()
                });
            });
        });
    }]);
