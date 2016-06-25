iftttclone.controller('ImportPublicRecipeController', ['$scope', '$rootScope', '$http', '$location', '$routeParams', function($scope, $rootScope, $http, $location, $routeParams){

var self = this;
	if($rootScope.authenticated != true){
		$location.path('/login');
	}

  $scope.recipe = {};


  $http({
    method: 'GET',
    url : 'api/publicrecipes/'+$routeParams.publicRecipeId
  }).then(function successCallback(response){
    // response.data contains the public recipe
    $scope.recipe = response.data;
    console.log($scope.recipe);
    delete($scope.recipe.description); // it is not what i need in the private recipe

    // now i need to create the private recipeTriggerFields and recipeActionField
    $scope.recipe.recipeTriggerFields = {};
    $scope.recipe.recipeActionFields =  {};
    // populating them
    for(var property in $scope.recipe.trigger.triggerFields)
      $scope.recipe.recipeTriggerFields[property] = {title : $scope.recipe.trigger.triggerFields[property].name, value : ''};
    for(property in $scope.recipe.action.actionFields)
      $scope.recipe.recipeActionFields[property] = {title : $scope.recipe.action.actionFields[property].name, value : ''};

    for(property in $scope.recipe.publicRecipeTriggerFields){
      $scope.recipe.recipeTriggerFields[property].value = $scope.recipe.publicRecipeTriggerFields[property].value;
    }
    for(property in $scope.recipe.publicRecipeActionFields){
      $scope.recipe.recipeActionFields[property].value = $scope.recipe.publicRecipeActionFields[property].value;
    }
    for(property in $scope.recipe.publicRecipeActionFields){
      $scope.recipe.recipeActionFields[property].value = $scope.recipe.publicRecipeActionFields[property].value;
    }
    delete($scope.recipe.publicRecipeActionFields);
    delete($scope.recipe.publicRecipeTriggerFields);

    console.log($scope.recipe);
		$scope.triggerChannelImage = 'img/'+ $scope.recipe.trigger.channel + '.png';
		$scope.actionChannelImage = 'img/'+ $scope.recipe.action.channel + '.png';

  }, function errorCallback(response){
	  console.log(response);
  });

	self.importRecipe = function(){
		console.log('into importPublicRecipe function')
		$http({
      method : 'POST',
      url : 'api/myrecipes',
      data : JSON.stringify($scope.recipe),
      headers : {
        'Content-Type': 'application/json'
      }
    }).then(function successCallback(result){
      $location.path('myRecipes');

    }, function errorCallback(result){
      $scope.error = true;
      $scope.errorMessage = result.data.message;
    });
	}

} ]);
