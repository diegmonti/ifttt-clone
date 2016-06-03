var schedulerApp = angular.module('schedulerApp', ['ngRoute']);

schedulerApp.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider.when('/', {
        templateUrl: 'partials/channels.html',
        controller: 'ChannelsController'
    }).when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController',
        controllerAs: 'controller'
    }).otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

schedulerApp.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http',
    function ($scope, $rootScope, $routeParams, $location, $http) {
        // i need to download the channels. TODO: In future limit this number to 25 or something like that

        // Simple GET request example:
        $http({
            method: 'GET',
            url: 'api/channels'
        }).then(function successCallback(response) {

            console.log(response.data);

            $scope.channels = [];
            console.log(response.data.length / 4 + 1);
            for (var i = 0; i < (response.data.length / 4) + 1; i++) {
                $scope.channels[i] = [];
            }

            for (i = 0; i < response.data.length; i++) {
                $scope.channels[Math.floor(i / 3)][i % 3] = {
                    title: response.data[i].name,
                    description: response.data[i].description
                };
            }
            console.log($scope.channels);

        }, function errorCallback(response) {
            console.log("error: " + response);
        });
    }]
);

schedulerApp.controller('LoginController', ['$rootScope', '$http', '$location',
    function ($rootScope, $http, $location) {
        var self = this;

        var authenticate = function (credentials, callback) {

            var headers = credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('api/user', {headers: headers}).then(function (response) {
                if (response.data.name) {
                    $rootScope.authenticated = true;
                } else {
                    $rootScope.authenticated = false;
                }
                callback && callback();
            }, function () {
                $rootScope.authenticated = false;
                callback && callback();
            });

        };

        authenticate();
        self.credentials = {};
        self.login = function () {
            authenticate(self.credentials, function () {
                if ($rootScope.authenticated) {
                    $location.path("/");
                    self.error = false;
                } else {
                    $location.path("/login");
                    self.error = true;
                }
            });
        };

        self.logout = function () {
            $http.post('api/user/logout', {}).then(function () {
                $rootScope.authenticated = false;
                $location.path("/");
            });
        }
    }]);