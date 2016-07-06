iftttclone.controller('PublishedRecipesController', ['$scope', '$rootScope', '$http', '$rootScope',
    function ($scope, $rootScope, $http, $rootScope) {
      if ($rootScope.authenticated === false) {
          $location.path("/login");
      }

      var self = this;
      self.currentPage = -1;
      self.morePages = false;

      $scope.publishedRecipes = [];
      $scope.info = false;
      $scope.error = false;
      $scope.infoMessage = "You don't have any published recipe.";
      $scope.errorMessage = '';

      function checkMorePages(){
        $http.get('api/publicrecipes/published?page='+self.currentPage+1).then(function successCallback(response) {
          self.morePages = (response.data.length != 0);
          console.log(response.data)
        });
      }

      self.downloadPublishedRecipes = function(){
        self.currentPage++;
        checkMorePages();
        $http.get('api/publicrecipes/published?page='+self.currentPage)
          .then(function successCallback(response){
            response.data.forEach(function(element){
              $scope.publishedRecipes.push(element);
            });
            if($scope.publishedRecipes == 0) $scope.info = true;
            else {
              //$('card').matchHeigth();
            }
          });
      }

      self.downloadPublishedRecipes();
  }]);
