iftttclone.controller('CreateRecipeController', ['$scope', '$rootScope', '$http', '$timeout', '$compile', '$location', 'fieldInputFactory',
function($scope, $rootScope, $http, $timeout, $compile, $location, fieldInputFactory){

  var self = this;
  self.currentSelected = "";
  var fieldsErrorsNumber = 0;
  $scope.recipe = {};
  $scope.channels = [];

  function downloadChannels() {
    $scope.channels = [];
    $http({
      method: 'GET',
      url: 'api/channels'
    }).then(
      function successCallback(result){
          result.data.forEach(function(element) {
            if(self.currentSelected == "trigger" && element.triggers){
              $scope.channels.push({
                id : element.id,
                title : element.name,
                description : element.description,
                link : 'img/'+element.id + '.png'
              });
            }
            else if (self.currentSelected == "action" && element.actions) {
              $scope.channels.push({
                id : element.id,
                title : element.name,
                description : element.description,
                link : 'img/'+element.id + '.png'
              });
            }
        });
      },
      function errorCallback(result){
        console.log(result);
      }
    );
  };

  function dowloadTriggers(){
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.trigger.channel
    }).then(
      function successCallback(result){

        $scope.triggers = [];

        for(var element in result.data.triggers)
          $scope.triggers.push({
            title: result.data.triggers[element].name,
            description : result.data.triggers[element].description,
            method : element
          });
      },
      function errorCallback(result){
      });
  }

  function downloadTriggerFields(){
    $scope.triggers = [];
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.trigger.channel
    }).then(
      function successCallback(result){
          var index;
          // first i need to save the ingredients
          $scope.recipe.trigger.ingredients = result.data.triggers[$scope.recipe.trigger.method].ingredients;

          for(index in result.data.triggers[$scope.recipe.trigger.method].triggerFields) {
            var element = result.data.triggers[$scope.recipe.trigger.method].triggerFields[index];

            (function(index){
              var div = $('<div>').attr({class : 'input-group row'});
              var label = $('<span>').attr({class : 'input-group-addon'}).text(element.name);
              $scope.recipe.recipeTriggerFields = {};
              $scope.recipe.recipeTriggerFields[index] = {value : ''};
              var input = fieldInputFactory.createInput(element.type, $scope.recipe.recipeTriggerFields[index], 'recipe.recipeTriggerFields.'+ index +'.value');

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
              $compile(input)($scope);

              div.append(label).append(input);
              $('#triggerFieldsDiv').append(div);
            })(index)


          }
          var div = $('<div>').attr({class : 'row', id : 'acceptTriggerButton'});
          var button = $('<button>').attr({
            class : 'btn btn-primary col-lg-4 col-lg-offset-3',
          }).text("Accetta");
          div.append($('<br>')).append(button);
          $('#triggerFieldsDiv').append(div);
          button.on('click', self.acceptTriggerFields);

      },
      function errorCallback(result){});
  }

  function downloadActions(){
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.action.channel
    }).then(
      function successCallback(result){

        $scope.actions = [];
        console.log();
        for(var element in result.data.actions)
          $scope.actions.push({
            title: result.data.actions[element].name,
            description : result.data.actions[element].description,
            method : element
          });
      },
      function errorCallback(result){

      });
  }

  function downloadActionFields(){
    $http({
      method : 'GET',
      url : 'api/channels/'+  $scope.recipe.action.channel
    }).then(
      function successCallback(result){

        var index;
        for(index in result.data.actions[  $scope.recipe.action.method].actionFields){
          var element = result.data.actions[  $scope.recipe.action.method].actionFields[index];

          (function(index){
            var div = $('<div>').attr({class : 'input-group row'});
            var label = $('<span>').attr({class : 'input-group-addon'}).text(element.name);

            $scope.recipe.recipeActionFields = {};
            $scope.recipe.recipeActionFields[index] = {value : ''};

            var input = fieldInputFactory.createInput(element.type, $scope.recipe.recipeActionFields[index], 'recipe.recipeActionFields.'+ index +'.value');

            var button = ($('<div>').attr({class : 'input-group-addon', 'data-toggle' : 'modal', 'data-target' : '#ingredientsModal'}));
            button.append($('<i>').attr({'class' : 'fa fa-flask'}));
            button.on('click', function(){ $scope.inputSelected = input});

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

            $compile(input)($scope);
            div.append(label).append(input).append(button);
            $('#actionFieldsDiv').append(div);
          })(index);

        }
        var div = $('<div>').attr({class : 'row', id : 'acceptActionButton'});
        var button = $('<button>').attr({
          class : 'btn btn-primary col-lg-4 col-lg-offset-3',
        }).text("Accetta");
        div.append($('<br>')).append(button);
        $('#actionFieldsDiv').append(div);
        button.on('click', self.acceptActionsFields);

      },
      function errorCallback(result){});
  }

  function createRecipe(){
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

      console.log($scope.recipe);
      (function(){
        var ol = $('<ol>').attr('class', 'breadcrumb');
        ol.append(
          $('<li>').attr('class', 'active').text('Trigger: ' + $scope.recipe.trigger.name)
        );
        $('#triggerFieldsHeaderDiv').empty().append(ol);
      })();

      $('#triggerFieldsDiv').show();

      var ol = $('<ol>').attr('class', 'breadcrumb');
      ol.append(
        $('<li>').attr('class', 'active').text('Action: ' + $scope.recipe.action.name)
      );
      $('#actionFieldsHeaderDiv').empty().append(ol);
      $('#actionFieldsDiv').show();


      $('#acceptTriggerButton').empty();
      $('#acceptActionButton').empty();

    });
  }

  self.selectTriggerClicked = function(){
    self.currentSelected = "trigger";
    downloadChannels();
  };

  self.selectActionClicked = function(){
    self.currentSelected = "action";
    downloadChannels();
  };

  self.channelSelected = function (id){
    $scope.channels = [];
    var image = $('<img>');
    $(image).attr("src","img/"+id+".png");
    $(image).attr("width", "110px");

    if(self.currentSelected === "trigger"){
    	$scope.recipe.trigger = {};
        $scope.recipe.trigger.channel = id;
      $("#triggerDiv").html(image);
      dowloadTriggers();
    }
    else if(self.currentSelected === "action"){
    	$scope.recipe.action = {};
        $scope.recipe.action.channel = id;
      $("#actionDiv").html(image);
      downloadActions();
    }
  }

  self.triggerSelected = function(id, name){
      $scope.recipe.trigger.method = id;
      $scope.recipe.trigger.name = name;
      downloadTriggerFields();
  }

  self.acceptTriggerFields = function(){
    if(fieldsErrorsNumber != 0) return;
    $('#triggerFieldsDiv').hide();

    var link = $('<a>').attr({
      class : 'btn btn-link'
    }).text('that').on('click', self.selectActionClicked);
    $('#actionDiv').html(link)
  }

  self.actionSelected = function(id, name){
    $scope.recipe.action.method = id;
    $scope.recipe.action.name = name;
    $scope.actions = [];
    downloadActionFields();
  }

  self.acceptActionsFields = function(){
    if(fieldsErrorsNumber != 0) return;
    $('#actionFieldsDiv').hide();


    var button = $('<button>').attr({
      class : 'btn btn-primary col-lg-4 col-lg-offset-3'
    }).text('Conferma');
    button.on('click', createRecipe);
    $('#confirmDiv').append(button);
  }

  self.insertIngredient = function(){
    // in $scope.inputSelected i have the input where i should place the new element
    // in $scope.selectedIngredient i have the ingredient that that user wants to insert
    var $txt = $($scope.inputSelected);
    var caretPos = $txt[0].selectionStart;
    var textAreaTxt = $txt.val();
    var txtToAdd = "{{"+  $scope.selectedIngredient + "}}";
    $txt.val(textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos) );
  }

}]);
