
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
     // now i need the fields name for the triggers
     return $http({
       method : 'GET',
       url : 'api/channels/' + $scope.recipe.trigger.channel
     });
   }, function errorCallback(){})
   .then(function successCallback(response){
     var fields = response.data.triggers[ $scope.recipe.trigger.method].triggerFields;
     for(var property in fields){
       $scope.recipe.recipeTriggerFields[property].title = fields[property].name;
     }

     // now i need to do the same job for the actions
     return $http({
       method : 'GET',
       url : 'api/channels/' + $scope.recipe.action.channel
     });
   })
   .then(function successCallback(response){
     var fields = response.data.actions[ $scope.recipe.action.method].actionFields;
     for(var property in fields){
       $scope.recipe.recipeActionFields[property].title = fields[property].name;
     }
   })

 }]);
