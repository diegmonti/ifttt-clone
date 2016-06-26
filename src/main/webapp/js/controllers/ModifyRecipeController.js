
iftttclone.controller('ModifyRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', '$compile',
 function ($scope, $rootScope, $routeParams, $location, $http, $window, $compile) {
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

   function createInputType(type, field){
     var input;
     if(type === 'TEMPERATURE'){
       input = $('<select>')
        .append($('<option>').val('C').text('C'))
        .append($('<option>').val('F').text('F'));
     }
     else if (type === 'EMAIL') {
       console.log();

       input = $('<input>').attr('type', 'email');
     }
     else if(type === 'LONGTEXT'){
       input = $('<textarea>');
     }
     else if(type === 'NULLABLEINTEGER' || type === 'INTEGER'){
       input = $('<input>').attr('type', 'number');
       // now i need to transform the value in the corrispondent number
       field.value = Number(field.value);
     }
     else if (type === 'TIME') {
       input = $('<input>').attr({
        'type' : 'time',
        'placeholder' : 'hh:mm',
        'data-ng-min' : '00:00:00',
        'data-ng-max' : '23:59:00'
       });
     }
     else if (type === 'TIMESTAMP') {
       input = $('<input>').attr({
        'type' : 'text',
        'placeholder' : 'dd/MM/yyyy HH:mm'
       });
     }
     else {
       input = $('<input>').attr('type', 'text');
     }
     return input;
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
             var input = createInputType($scope.recipe.trigger.triggerFields[arg].type, $scope.recipe.recipeTriggerFields[arg]);
    	       input.attr({
    	         class:"form-control",
    	         'aria-describedby':"basic-addon3" ,
    	         'data-ng-model': 'recipe.recipeTriggerFields.'+ arg +'.value'
    	       });
    	       inputGroup.append(span).append(input);

    	       $('#triggersDiv').append(inputGroup);

    	       $compile(input)($scope);

    	 })();
    	 $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
     }

     for(arg in $scope.recipe.recipeActionFields){

    	 (function(){
    		 var inputGroup = $('<div>').attr({
    	         class : 'input-group'
    	       });
    	        var span =  ($('<span>').attr({class : 'input-group-addon'}).text($scope.recipe.action.actionFields[arg].name));


             var  input = createInputType($scope.recipe.action.actionFields[arg].type, $scope.recipe.recipeActionFields[arg]);

             input.attr({
    	         class:"form-control",
    	         'aria-describedby':"basic-addon3" ,
    	         'data-ng-model': 'recipe.recipeActionFields.'+ arg +'.value'
    	       });
    	       inputGroup.append(span).append(input);

    	       $('#actionsDiv').append(inputGroup);
    	       input.val($scope.recipe.recipeActionFields[arg].value);

    	       $compile(input)($scope);

    	 })();


       $scope.recipe.recipeActionFields[arg].title = $scope.recipe.action.actionFields[arg].name;
     }
   }, function errorCallback(){})
 }]);
