iftttclone.controller('ChannelsController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
    function ($scope, $rootScope, $routeParams, $location, $http, $window) {
        var self = this;
        $scope.channels = [];
        $http({
            method: 'GET',
            url: 'api/channels'
        }).then(function successCallback(response) {
            var i;

            for (i = 0; i < (response.data.length / 4) + 1; i++) {
                $scope.channels[i] = [];
            }

            for (i = 0; i < response.data.length; i++) {
                $scope.channels[Math.floor(i / 2)][i % 2] = {
                    id: response.data[i].id,
                    title: response.data[i].name,
                    description: response.data[i].description,
                    link: "img/" + response.data[i].id + ".png"
                };
            }
        }, function errorCallback(response) {
            console.log("error: " + response);
        });

        self.selectChannel = function (channelID) {
            console.log("selectChannel" + channelID);
            $location.path('/channel/' + channelID);
        };
        $scope.connectToService = function (postUrl) {
            console.log(postUrl);
            $http({
                method: 'POST',
                url: postUrl
            }).then(function successCallback(response) {
                $window.open(response.data.url, '_blank');
            }, function errorCallback(response) {
                console.log(response);
            });
        };
    }]
    );