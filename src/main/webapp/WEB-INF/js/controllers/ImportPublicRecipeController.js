iftttclone.controller('ImportPublicRecipeController', ['$scope', '$rootScope', '$http', '$location', '$routeParams', '$compile', 'fieldInputFactory',
    function ($scope, $rootScope, $http, $location, $routeParams, $compile, fieldInputFactory) {
        if ($rootScope.authenticated === false) {
            $location.path('/login');
        }

        var self = this, fieldsErrorsNumber = 0;
        $scope.recipe = {};

        $http({
            method: 'GET',
            url: 'api/publicrecipes/' + $routeParams.publicRecipeId
        }).then(function successCallback(response) {
            var property, input;

            // response.data contains the public recipe
            $scope.recipe = response.data;

            // now i need to create the private recipeTriggerFields and recipeActionField
            $scope.recipe.recipeTriggerFields = {};
            $scope.recipe.recipeActionFields = {};

            // populating them
            for (property in $scope.recipe.trigger.triggerFields) {
                if ($scope.recipe.trigger.triggerFields.hasOwnProperty(property)) {
                    $scope.recipe.recipeTriggerFields[property] = {value: ''};
                }
            }

            for (property in $scope.recipe.action.actionFields) {
                if ($scope.recipe.action.actionFields.hasOwnProperty(property)) {
                    $scope.recipe.recipeActionFields[property] = {value: ''};
                }
            }

            for (property in $scope.recipe.publicRecipeTriggerFields) {
                if ($scope.recipe.publicRecipeTriggerFields.hasOwnProperty(property)) {
                    $scope.recipe.recipeTriggerFields[property].value = $scope.recipe.publicRecipeTriggerFields[property].value;
                }
            }

            for (property in $scope.recipe.publicRecipeActionFields) {
                if ($scope.recipe.publicRecipeActionFields.hasOwnProperty(property)) {
                    $scope.recipe.recipeActionFields[property].value = $scope.recipe.publicRecipeActionFields[property].value;
                }
            }

            delete ($scope.recipe.publicRecipeActionFields);
            delete ($scope.recipe.publicRecipeTriggerFields);

            $scope.triggerChannelImage = 'img/' + $scope.recipe.trigger.channel + '.png';
            $scope.actionChannelImage = 'img/' + $scope.recipe.action.channel + '.png';

            function invertErrorClass() {
                if ($(input).hasClass('ng-invalid')) {
                    if ($(input).hasClass('alert-danger') === false) {
                        $(input).addClass('alert-danger');
                        fieldsErrorsNumber++;
                    }
                } else {
                    if ($(input).hasClass('alert-danger')) {
                        $(input).removeClass('alert-danger');
                        fieldsErrorsNumber--;
                    }
                }
            }

            function createTriggerField(property) {
                var inputGroup, span;
                inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                span = ($('<span>').attr({class: 'input-group-addon'}).text($scope.recipe.trigger.triggerFields[property].name));
                input = fieldInputFactory.createInput($scope.recipe.trigger.triggerFields[property].type, $scope.recipe.recipeTriggerFields[property], 'recipe.recipeTriggerFields.' + property + '.value');
                $(input).change(invertErrorClass);
                inputGroup.append(span).append(input);
                $('#triggersDiv').append(inputGroup);
                $compile(input)($scope);
            }

            // now i need to populate the divs
            for (property in $scope.recipe.recipeTriggerFields) {
                if ($scope.recipe.recipeTriggerFields.hasOwnProperty(property)) {
                    createTriggerField(property);
                }
            }

            function createActionField(property) {
                var inputGroup, span, button;
                inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                span = ($('<span>').attr({class: 'input-group-addon'}).text($scope.recipe.action.actionFields[property].name));
                input = fieldInputFactory.createInput($scope.recipe.action.actionFields[property].type, $scope.recipe.recipeActionFields[property], 'recipe.recipeActionFields.' + property + '.value');

                button = ($('<div>').attr({
                    class: 'input-group-addon',
                    'data-toggle': 'modal',
                    'data-target': '#ingredientsModal'
                }));
                button.append($('<i>').attr({'class': 'fa fa-flask'}));
                button.on('click', function () {
                    $scope.inputSelected = input;
                    $scope.model = property;
                });

                $(input).change(invertErrorClass);
                inputGroup.append(span).append(input);
                if ($scope.recipe.action.actionFields[property].type === 'TEXT' ||
                        $scope.recipe.action.actionFields[property].type === 'LONGTEXT' ||
                        $scope.recipe.action.actionFields[property].type === 'NULLABLETEXT') {
                    inputGroup.append(button);
                }
                $('#actionFieldsDiv').append(inputGroup);

                $('#actionsDiv').append(inputGroup);
                $compile(input)($scope);
            }

            for (property in $scope.recipe.recipeActionFields) {
                if ($scope.recipe.recipeActionFields.hasOwnProperty(property)) {
                    createActionField(property);
                }
            }
        }, function errorCallback(response) {
            console.log(response);
        });

        self.importRecipe = function () {
            if (fieldsErrorsNumber > 0) {
                console.error('there are still ' + fieldsErrorsNumber + ' errors');
                return;
            }
            $http({
                method: 'POST',
                url: 'api/myrecipes',
                data: JSON.stringify($scope.recipe),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function successCallback() {
                $location.path('myRecipes');
            }, function errorCallback(result) {
                $scope.error = true;
                $scope.errorMessage = result.data.message;
            });
        };

        self.insertIngredient = function () {
            // in $scope.inputSelected i have the input where i should place the new element
            // in $scope.selectedIngredient i have the ingredient that that user wants to insert
            // $scope.model contains the selected action field
            var $txt, caretPos, textAreaTxt, txtToAdd;
            $txt = $($scope.inputSelected);
            caretPos = $txt[0].selectionStart;
            textAreaTxt = $txt.val();
            txtToAdd = "{{" + $scope.selectedIngredient.name + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

    }]);
