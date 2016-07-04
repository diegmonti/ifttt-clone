iftttclone.controller('ChannelController', ['$scope', '$rootScope', '$http', '$location', '$routeParams', function ($scope, $rootScope, $http, $location, $routeParams) {

    var self = this;
    $scope.recipes = [];
    $scope.channel = {};

    $http({
        method: 'GET',
        url: 'api/channels/' + $routeParams.channelID
    }).then(
        function (response) { // successCallback
            $scope.channel.id = response.data.id;
            $scope.channel.title = response.data.name;
            $scope.channel.link = "img/" + response.data.id + ".png";
            $scope.channel.description = response.data.description;
            $scope.channel.withConnection = response.data.withConnection;
            $scope.error = false;

            if (response.data.connectionTime === null && response.data.withConnection === true) {
                $scope.toConnect = true;
                $scope.channel.activate = 'api/channels/' + $routeParams.channelID + '/activate';
            } else {
                $scope.toConnect = false;
            }

            // now i need to download the public recipes that are contained in this channel
            return $http({
                method: 'GET',
                url: 'api/channels/' + $routeParams.channelID + '/publicrecipes'
            });
        },
        function (response) { // error callback
            $scope.error = true;
        }
    )
        .then(function successCallback(response) {
            response.data.forEach(function (element) {
                element.triggerChannel = 'img/' + element.trigger.channel + '.png';
                element.actionChannel = 'img/' + element.action.channel + '.png';
                $scope.recipes.push(element);
            });
        });

    self.deactivateChannel = function () {
        $http.post('api/channels/' + $routeParams.channelID + '/deactivate').then(function successCallback(response) {
            $scope.toConnect = true;
            $scope.channel.activate = 'api/channels/' + $routeParams.channelID + '/activate';
        }, function (response) {
            console.log("error");
        });
    };
}]);
