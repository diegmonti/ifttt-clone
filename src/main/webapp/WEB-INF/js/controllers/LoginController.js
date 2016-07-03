iftttclone.controller('LoginController', ['$rootScope', '$http', '$location', '$routeParams',
    function ($rootScope, $http, $location, $routeParams) {

        var self = this;
        self.credentials = {};
        if ($routeParams.registered != null) {
            self.registered = true;
        }

        var authenticate = function (credentials, callback) {
            var headers = credentials ? {
                authorization: "Basic " + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('api/user', {headers: headers}).then(function (response) {
                if (response.data.username) {
                    $rootScope.authenticated = true;
                    $rootScope.username = response.data.username;
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
        };

    }]);