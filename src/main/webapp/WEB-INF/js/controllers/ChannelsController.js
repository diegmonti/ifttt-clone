iftttclone.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http',
    function ($scope, $rootScope, $routeParams, $location, $http) {
        var self = this;
        $scope.channels = [];
        $http({
            method: 'GET',
            url: 'api/channels'
        }).then(function successCallback(response) {
            var i;
            for (i = 0; i < response.data.length; i++) {
                $scope.channels.push({
                    id: response.data[i].id,
                    title: response.data[i].name,
                    description: response.data[i].description
                });
            }
        }, function errorCallback(response) {
            console.error(response);
        });

        self.selectChannel = function (channelID) {
            $location.path('/channel/' + channelID);
        };
    }]);
