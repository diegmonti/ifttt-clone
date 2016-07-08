iftttclone.controller('PublishRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', 'fieldInputFactory', '$compile',
    function ($scope, $rootScope, $routeParams, $location, $http, $window, fieldInputFactory, $compile) {
        if ($rootScope.authenticated === false) {
            $location.path('/login');
        }

        var self = this;
        $scope.recipe = {};

        self.publishRecipe = function () {
            var sentRecipe, element;
            sentRecipe = JSON.parse(JSON.stringify($scope.recipe));

            function deleteElementTitle(element) {
                delete (element.title);
            }

            for (element in sentRecipe.recipeTriggerFields) {
                if (sentRecipe.recipeTriggerFields.hasOwnProperty(element)) {
                    deleteElementTitle(element);
                }
            }
            for (element in sentRecipe.recipeActionFields) {
                if (sentRecipe.recipeActionFields.hasOwnProperty(element)) {
                    deleteElementTitle(element);
                }
            }

            sentRecipe.publicRecipeTriggerFields = JSON.parse(JSON.stringify(sentRecipe.recipeTriggerFields));
            sentRecipe.publicRecipeActionFields = JSON.parse(JSON.stringify(sentRecipe.recipeActionFields));
            delete (sentRecipe.recipeActionFields);
            delete (sentRecipe.recipeTriggerFields);

            $http({
                method: 'POST',
                url: 'api/publicrecipes/',
                data: JSON.stringify(sentRecipe)
            }).then(function successCallback() {
                $location.path('/myRecipes');
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
            txtToAdd = "{{" + $scope.selectedIngredient + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

        // now i need to populate the recipe object
        $http({
            method: 'GET',
            url: 'api/myrecipes/' + $routeParams.recipeID
        }).then(function successCallback(response) {
            var arg, input;
            $scope.recipe = response.data;
            $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
            $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';

            function invertErrorClass() {
                if ($(input).hasClass('ng-invalid')) {
                    if ($(input).hasClass('alert-danger') === false) {
                        $(input).addClass('alert-danger');
                    }
                } else {
                    if ($(input).hasClass('alert-danger')) {
                        $(input).removeClass('alert-danger');
                    }
                }
            }

            function createTriggerField(recipe, arg) {
                var inputGroup, span;
                inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                span = ($('<span>').attr({class: 'input-group-addon'}).text(recipe.trigger.triggerFields[arg].name));
                input = fieldInputFactory.createInput(recipe.trigger.triggerFields[arg].type, recipe.recipeTriggerFields[arg], 'recipe.recipeTriggerFields.' + arg + '.value');
                $(input).change(invertErrorClass);
                inputGroup.append(span).append(input);
                $('#triggerFieldsDiv').append(inputGroup);
                $compile(input)($scope);
            }

            for (arg in $scope.recipe.recipeTriggerFields) {
                if ($scope.recipe.recipeTriggerFields.hasOwnProperty(arg)) {
                    if ($scope.recipe.trigger.triggerFields[arg].publishable === true) {
                        $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;
                        createTriggerField($scope.recipe, arg);
                    } else {
                        delete ($scope.recipe.recipeTriggerFields[arg]);
                    }
                }
            }

            function createActionField(recipe, arg) {
                var inputGroup, span, button;
                inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                span = ($('<span>').attr({class: 'input-group-addon'}).text(recipe.action.actionFields[arg].name));
                input = fieldInputFactory.createInput(recipe.action.actionFields[arg].type, recipe.recipeActionFields[arg], 'recipe.recipeActionFields.' + arg + '.value');
                button = ($('<div>').attr({
                    class: 'input-group-addon',
                    'data-toggle': 'modal',
                    'data-target': '#ingredientsModal'
                }));
                button.append($('<i>').attr({'class': 'fa fa-flask'}));
                button.on('click', function () {
                    $scope.inputSelected = input;
                    $scope.model = arg;
                });
                $(input).change(invertErrorClass);
                inputGroup.append(span).append(input);
                if ($scope.recipe.action.actionFields[arg].type === 'TEXT' ||
                        $scope.recipe.action.actionFields[arg].type === 'LONGTEXT' ||
                        $scope.recipe.action.actionFields[arg].type === 'NULLABLETEXT') {
                    inputGroup.append(button);
                }

                $('#actionFieldsDiv').append(inputGroup);
                $compile(input)($scope);
            }

            for (arg in $scope.recipe.recipeActionFields) {
                if ($scope.recipe.recipeActionFields.hasOwnProperty(arg)) {
                    if ($scope.recipe.action.actionFields[arg].publishable === true) {
                        createActionField($scope.recipe, arg);
                        $scope.recipe.recipeActionFields[arg].title = $scope.recipe.action.actionFields[arg].name;
                    } else {
                        delete ($scope.recipe.recipeActionFields[arg]);
                    }
                }
            }
        }, function errorCallback(response) {
            console.error(response);
        });

    }]);
