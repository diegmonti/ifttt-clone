iftttclone.controller('LoginController', ['$rootScope', '$scope', '$http', '$location', '$routeParams',
    function ($rootScope, $scope, $http, $location, $routeParams) {

        var self = this;
        self.credentials = {};
        $scope.searchText = '';

        if ($routeParams.registered !== undefined) {
            self.registered = true;
        }

        function authenticate(credentials, callback) {
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
                if (callback !== undefined) {
                    callback();
                }
            }, function () {
                $rootScope.authenticated = false;
                if (callback !== undefined) {
                    callback();
                }
            });

        }
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

        /*
        This function is called by the navbar, which see this as its controller
        */
        self.searchRecipe = function(){
          $location.path('/publicRecipes/' + $scope.searchText);
        }
    }]);
