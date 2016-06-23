
iftttclone.controller('ModifyRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window',
 function ($scope, $rootScope, $routeParams, $location, $http, $window) {
   var self = this;
   $scope.recipe = {};

   self.updateRecipe = function(){

     var sentRecipe =  JSON.parse(JSON.stringify($scope.recipe));

     for(var property in sentRecipe.recipeTriggerFields){
       delete(sentRecipe.recipeTriggerFields[property].title);
     }
     for(var property in sentRecipe.recipeActionFields){
       delete(sentRecipe.recipeActionFields[property].title);
     }

     $http({
       method : 'PUT',
       url : 'api/myrecipes/' + $routeParams.recipeId,
       data : JSON.stringify(sentRecipe)
     }).then(function successCallback(){
       $location.path('/myRecipes');
     }, function errorCallback(){});

   };

   // now i need to populate the recipe object

   $http({
     method : 'GET',
     url : 'api/myrecipes/' + $routeParams.recipeId
   }).then(function successCallback(response){
     $scope.recipe = response.data;
     $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
     $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';
     for(var arg in $scope.recipe.recipeTriggerFields){
       $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
     }
     for(arg in $scope.recipe.recipeActionFields){
       $scope.recipe.recipeActionFields[arg].title = $scope.recipe.action.actionFields[arg].name;
     }
   }, function errorCallback(){})
 }]);
