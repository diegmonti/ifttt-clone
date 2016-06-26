
iftttclone.controller('ModifyRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', '$compile', 'fieldInputFactory',
 function ($scope, $rootScope, $routeParams, $location, $http, $window, $compile, fieldInputFactory) {
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

   function createInputType(type, field, model){
     return fieldInputFactory.createInput(type, field, model);
   }

   $http({
     method : 'GET',
     url : 'api/myrecipes/' + $routeParams.recipeId
   }).then(function successCallback(response){
     $scope.recipe = response.data;
     $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
     $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';

     for(var arg in $scope.recipe.recipeTriggerFields){
    	 (function(){
    		 var inputGroup = $('<div>').attr({
    	         class : 'input-group'
    	       });
    	       var span =  ($('<span>').attr({class : 'input-group-addon'}).text($scope.recipe.trigger.triggerFields[arg].name));
             var input = createInputType($scope.recipe.trigger.triggerFields[arg].type, $scope.recipe.recipeTriggerFields[arg], 'recipe.recipeTriggerFields.'+ arg +'.value');
    	       inputGroup.append(span).append(input);
    	       $('#triggersDiv').append(inputGroup);
    	       $compile(input)($scope);

    	 })();
    	 $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
     }

     for(arg in $scope.recipe.recipeActionFields){
  		 var inputGroup = $('<div>').attr({
  	         class : 'input-group'
  	       });
  	        var span =  ($('<span>').attr({class : 'input-group-addon'}).text($scope.recipe.action.actionFields[arg].name));
            var  input = createInputType($scope.recipe.action.actionFields[arg].type, $scope.recipe.recipeActionFields[arg], 'recipe.recipeActionFields.'+ arg +'.value');
            inputGroup.append(span).append(input);
    	      $('#actionsDiv').append(inputGroup);
    	      $compile(input)($scope);
     }
   }, function errorCallback(){})
 }]);
