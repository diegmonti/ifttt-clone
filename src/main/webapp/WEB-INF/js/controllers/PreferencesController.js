iftttclone.controller('PreferencesController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location) {
        if ($rootScope.authenticated === false) {
            $location.path('/login');
        }

        var self = this;
        $scope.user = {};
        $scope.timezones = [];

        $http.get('api/user').then(function successCallback(response) {
            $scope.user = response.data;
        });
        $http.get('api/user/timezones').then(function successCallback(response) {
            $scope.timezones = response.data;
        });

        self.updateProfile = function () {
            var updateUser = {};
            if ($scope.user.password !== undefined) {
                if ($scope.user.password !== $scope.user.confirm) {
                    self.success = false;
                    self.error = true;
                    $scope.errorMessage = "The provided passwords are not equal.";
                    return;
                }
                updateUser.password = $scope.user.password;
            }
            updateUser.email = $scope.user.email;
            updateUser.timezone = $scope.user.timezone;
            $http({
                method: 'PUT',
                url: 'api/user',
                data: JSON.stringify(updateUser)
            }).then(function () {
                self.success = true;
                self.error = false;
                $scope.successMessage = "The profile was successfully updated.";
            }, function (response) {
                self.success = false;
                self.error = true;
                $scope.errorMessage = response.data.message;
            });
        };

        self.deactivateChannel = function (channel) {
            console.log('api/channels/' + channel + '/deactivate');
            $http.post('api/channels/' + channel + '/deactivate')
                .then(function successCallback() {
                    $http.get('api/user').then(function successCallback(response) {
                        $scope.user = response.data;
                    });
                });
        };
    }]);
