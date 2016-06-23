iftttclone.controller('PublishRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
    function ($scope, $rootScope, $routeParams, $location, $http, $window) {

      var self = this;
      $scope.recipe = {};

      self.publishRecipe = function(){

        var sentRecipe =  JSON.parse(JSON.stringify($scope.recipe));

        for(var property in sentRecipe.recipeTriggerFields){
          delete(sentRecipe.recipeTriggerFields[property].title);
        }
        for(var property in sentRecipe.recipeActionFields){
          delete(sentRecipe.recipeActionFields[property].title);
        }

        sentRecipe.publicRecipeTriggerFields = JSON.parse(JSON.stringify(sentRecipe.recipeTriggerFields));
        sentRecipe.publicRecipeActionFields = JSON.parse(JSON.stringify(sentRecipe.recipeActionFields));
        delete(sentRecipe.recipeActionFields);
        delete(sentRecipe.recipeTriggerFields);


        console.log(JSON.stringify(sentRecipe));
        $http({
          method : 'POST',
          url : 'api/publicrecipes/',
          data : JSON.stringify(sentRecipe)
        }).then(function successCallback(){
          $location.path('/myRecipes');
        }, function errorCallback(){});

      };

      // now i need to populate the recipe object

      $http({
        method : 'GET',
        url : 'api/myrecipes/' + $routeParams.recipeID
      }).then(function successCallback(response){
        $scope.recipe = response.data;
        $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
        $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';
        for(var arg in $scope.recipe.recipeTriggerFields){
          if($scope.recipe.trigger.triggerFields[arg].publishable === true)
            $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
          else
            delete($scope.recipe.recipeTriggerFields[arg]);
        }
        for(arg in $scope.recipe.recipeActionFields){
          if($scope.recipe.action.actionFields[arg].publishable === true)
            $scope.recipe.recipeActionFields[arg].title = $scope.recipe.action.actionFields[arg].name;
          else
            delete($scope.recipe.recipeActionFields[arg]);
        }
      }, function errorCallback(){})

    }]);
