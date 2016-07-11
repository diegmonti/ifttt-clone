
iftttclone.controller('ModifyRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', '$compile', 'fieldInputFactory',
 function ($scope, $rootScope, $routeParams, $location, $http, $window, $compile, fieldInputFactory) {
   if ($rootScope.authenticated !== true) {
     $location.path('/login');
   }

   var self = this;
   var fieldsErrorsNumber = 0;
   $scope.recipe = {};
   $scope.error = false;
   $scope.errorMessage = '';


   self.updateRecipe = function(){

     if(fieldsErrorsNumber === 0){
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
         }, function errorCallback(response){
           $scope.error = true;
           $scope.errorMessage  = response.data.message;
         });
     }
     else {
       console.error('there are still ' + fieldsErrorsNumber + 'errors');
     }
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
  		 var inputGroup = $('<div>').attr({
  	         class : 'input-group'
  	       });
       var span =  ($('<span>').attr({class : 'input-group-addon'}).text($scope.recipe.trigger.triggerFields[arg].name));
       var input = createInputType($scope.recipe.trigger.triggerFields[arg].type, $scope.recipe.recipeTriggerFields[arg], 'recipe.recipeTriggerFields.'+ arg +'.value');
       $(input).change(function(){
         if($(input).hasClass('ng-invalid')){
           if($(input).hasClass('alert-danger') == false){
             $(input).addClass('alert-danger');
             fieldsErrorsNumber++;
           }
         }
         else {
           if($(input).hasClass('alert-danger')){
             $(input).removeClass('alert-danger');
             fieldsErrorsNumber--;
           }
         }
       });
       inputGroup.append(span).append(input);
       $('#triggersDiv').append(inputGroup);
       $compile(input)($scope);

       if ($scope.recipe.trigger.triggerFields[arg].type != 'NULLABLETEXT' && ($scope.recipe.recipeTriggerFields[arg].value == null ||$scope.recipe.recipeTriggerFields[arg].value == '')){
         $(input).addClass('alert-danger');
         fieldsErrorsNumber++;
       }

    	 $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
     }

     for(arg in $scope.recipe.recipeActionFields){
       (function(arg){
         var inputGroup = $('<div>').attr({
    	         class : 'input-group'
    	       });
    	        var span =  ($('<span>').attr({class : 'input-group-addon'}).text($scope.recipe.action.actionFields[arg].name));
              var  input = createInputType($scope.recipe.action.actionFields[arg].type, $scope.recipe.recipeActionFields[arg], 'recipe.recipeActionFields.'+ arg +'.value');

              var button = ($('<div>').attr({class : 'input-group-addon', 'data-toggle' : 'modal', 'data-target' : '#ingredientsModal'}));
              button.append($('<i>').attr({'class' : 'fa fa-flask'}));
              button.on('click', function(){
                $scope.inputSelected = input;
                $scope.model = arg;
              });

              $(input).change(function(){
                if($(input).hasClass('ng-invalid')){
                  if($(input).hasClass('alert-danger') == false){
                    $(input).addClass('alert-danger');
                    fieldsErrorsNumber++;
                  }
                }
                else {
                  if($(input).hasClass('alert-danger')){
                    $(input).removeClass('alert-danger');
                    fieldsErrorsNumber--;
                  }
                }
              });
              inputGroup.append(span).append(input);
              if($scope.recipe.action.actionFields[arg].type == 'TEXT' ||
      				 	$scope.recipe.action.actionFields[arg].type == 'LONGTEXT' ||
      					$scope.recipe.action.actionFields[arg].type == 'NULLABLETEXT') inputGroup.append(button);

      	      $('#actionsDiv').append(inputGroup);
      	      $compile(input)($scope);
              if ($scope.recipe.action.actionFields[arg].type != 'NULLABLETEXT' && ($scope.recipe.recipeActionFields[arg].value == null ||$scope.recipe.recipeActionFields[arg].value == '')){
                $(input).addClass('alert-danger');
                fieldsErrorsNumber++;
              }
       })(arg);

     }
   }, function errorCallback(){})

   self.insertIngredient = function(){
     // in $scope.inputSelected i have the input where i should place the new element
     // in $scope.selectedIngredient i have the ingredient that that user wants to insert
     // $scope.model contains the selected action field
     var $txt = $($scope.inputSelected);
     var caretPos = $txt[0].selectionStart;
     var textAreaTxt = $txt.val();
     var txtToAdd = "{{"+  $scope.selectedIngredient + "}}";
     $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos) );
   }
 }]);
