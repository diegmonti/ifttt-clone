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
          $scope.channels.push({
            id : element.id,
            title : element.name,
            description : element.description,
            link : 'img/'+element.id + '.png'
          });
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
          var div = $('<div>').attr({class : 'form-group row', id : 'acceptTriggerButton'});
          var button = $('<button>').attr({
            class : 'btn btn-primary col-lg-4 col-lg-offset-3',
          }).text("Accetta");
          div.append(button);
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
            var div = $('<div>').attr({class : 'form-group row'});
            var label = $('<label>').attr({class : 'form-control-label'}).text(element.name);

            $scope.recipe.recipeActionFields = {};
            $scope.recipe.recipeActionFields[index] = {value : ''};

            var input = fieldInputFactory.createInput(element.type, $scope.recipe.recipeActionFields[index], 'recipe.recipeActionFields.'+ index +'.value');
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
            $('#actionFieldsDiv').append(div);
          })(index);

        }
        var div = $('<div>').attr({class : 'form-group row', id : 'acceptActionButton'});
        var button = $('<button>').attr({
          class : 'btn btn-primary col-lg-4 col-lg-offset-3',
        }).text("Accetta");
        div.append(button);
        $('#actionFieldsDiv').append(div);
        button.on('click', self.acceptActionsFields);

      },
      function errorCallback(result){});
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

  self.triggerSelected = function(id){
      $scope.recipe.trigger.method = id;
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

  self.actionSelected = function(id){
    $scope.recipe.action.method = id;
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

      (function(){
        var ol = $('<ol>').attr('class', 'breadcrumb');
        ol.append(
          $('<li>').attr('class', 'active').text('Trigger: ' + $scope.recipe.trigger.name)
        );
        $('#triggerFieldsHeaderDiv').append(ol);
      })();


      $('#triggerFieldsDiv').show();

      var ol = $('<ol>').attr('class', 'breadcrumb');
      ol.append(
        $('<li>').attr('class', 'active').text('Action: ' + $scope.recipe.action.name)
      );
      $('#actionFieldsHeaderDiv').append(ol);
      $('#actionFieldsDiv').show();


      $('#acceptTriggerButton').empty();
      $('#acceptActionButton').empty();

    });
  }
}]);
