iftttclone.controller('PreferencesController', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
    function ($scope, $rootScope, $http, $location, $routeParams) {
        $scope.user = {};
        $scope.timezones = [];
        var self = this;
        $http.get('api/user').then(function successCallback(response) {
            $scope.user = response.data;
        });
        $http.get('api/user/timezones').then(function successCallback(response) {
            $scope.timezones = response.data;
        });
        self.updateProfile = function () {
            var updateUser = {};
            if ($scope.user.password !== undefined) {
                updateUser.password = $scope.user.password;
            }
            updateUser.email = $scope.user.email;
            updateUser.timezone = $scope.user.timezone;
            console.log(updateUser);
            $http({
                method: 'PUT',
                url: 'api/user',
                data: JSON.stringify(updateUser)
            }).then(function () {
                self.error = false;
            }, function (response) {
                self.error = true;
                $scope.errorMessage = response.data.message;
            });
        };
        self.deactivateChannel = function (channel) {
            console.log('api/channels/' + channel + '/deactivate');

            $http.post('api/channels/' + channel + '/deactivate').then(function successCallback(response) {
                console.log(response);
                $http.get('api/user').then(function successCallback(response) {
                    $scope.user = response.data;
                });
            });
        };
    }]);
