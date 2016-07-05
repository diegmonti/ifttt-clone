iftttclone.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
    function ($scope, $rootScope, $routeParams, $location, $http, $window) {
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
                    description: response.data[i].description,
                });
            }
        }, function errorCallback(response) {
            console.log("error: " + response);
        });

        self.selectChannel = function (channelID) {
            console.log("selectChannel" + channelID);
            $location.path('/channel/' + channelID);
        };
    }]);
