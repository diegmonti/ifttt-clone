iftttclone.controller('SignInController', ['$scope', '$rootScope', '$http', '$location',
    function ($scope, $rootScope, $http, $location) {
        var self = this;
        self.credentials = {};

        $http({
            method: 'GET',
            url: 'api/user/timezones'
        }).then(function successCallback(data) {
            $scope.timezones = data.data;
        }, function errorCallback(data) {
            console.error(data);
        });

        self.signIn = function () {
            // check if the passwords are equal
            if (self.credentials.password !== self.credentials.confirm) {
                self.error = true;
                $scope.errorMessage = "The provided passwords are not equal.";
                return;
            }

            $http({
                method: 'POST',
                url: 'api/user',
                data: {
                    username: self.credentials.username,
                    password: self.credentials.password,
                    email: self.credentials.email,
                    timezone: self.credentials.timezone
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function successCallback() {
                $rootScope.authenticated = false;
                var loginPage = "/login/registered";
                $location.path(loginPage);
            }, function errorCallback(response) {
                self.error = true;
                $scope.errorMessage = response.data.message;
            });
        };

    }]);