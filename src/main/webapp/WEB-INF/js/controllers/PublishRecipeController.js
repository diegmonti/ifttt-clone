iftttclone.controller('PublishRecipeController', ['$scope', '$rootScope', '$routeParams', '$location', '$http', '$window', 'fieldInputFactory', '$compile',
    function ($scope, $rootScope, $routeParams, $location, $http, $window, fieldInputFactory, $compile) {

        var self = this;
        $scope.recipe = {};

        self.publishRecipe = function () {

            var sentRecipe = JSON.parse(JSON.stringify($scope.recipe));

            for (var property in sentRecipe.recipeTriggerFields) {
                delete(sentRecipe.recipeTriggerFields[property].title);
            }
            for (var property in sentRecipe.recipeActionFields) {
                delete(sentRecipe.recipeActionFields[property].title);
            }

            sentRecipe.publicRecipeTriggerFields = JSON.parse(JSON.stringify(sentRecipe.recipeTriggerFields));
            sentRecipe.publicRecipeActionFields = JSON.parse(JSON.stringify(sentRecipe.recipeActionFields));
            delete(sentRecipe.recipeActionFields);
            delete(sentRecipe.recipeTriggerFields);

            $http({
                method: 'POST',
                url: 'api/publicrecipes/',
                data: JSON.stringify(sentRecipe)
            }).then(function successCallback() {
                $location.path('/myRecipes');
            }, function errorCallback() {
            });

        };

        self.insertIngredient = function () {
            // in $scope.inputSelected i have the input where i should place the new element
            // in $scope.selectedIngredient i have the ingredient that that user wants to insert
            // $scope.model contains the selected action field
            var $txt = $($scope.inputSelected);
            var caretPos = $txt[0].selectionStart;
            var textAreaTxt = $txt.val();
            var txtToAdd = "{{" + $scope.selectedIngredient + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos) );
        }
        // now i need to populate the recipe object

        $http({
            method: 'GET',
            url: 'api/myrecipes/' + $routeParams.recipeID
        }).then(function successCallback(response) {
            $scope.recipe = response.data;
            $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
            $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';

            for (var arg in $scope.recipe.recipeTriggerFields) {
                if ($scope.recipe.trigger.triggerFields[arg].publishable === true) {
                    $scope.recipe.recipeTriggerFields[arg].title = $scope.recipe.trigger.triggerFields[arg].name;

                    (function (recipe, arg) {
                        var inputGroup = $('<div>').attr({
                            class: 'input-group'
                        });
                        var span = ($('<span>').attr({class: 'input-group-addon'}).text(recipe.trigger.triggerFields[arg].name));
                        var input = fieldInputFactory.createInput(recipe.trigger.triggerFields[arg].type, recipe.recipeTriggerFields[arg], 'recipe.recipeTriggerFields.' + arg + '.value');
                        $(input).change(function () {
                            if ($(input).hasClass('ng-invalid')) {
                                if ($(input).hasClass('alert-danger') == false) {
                                    $(input).addClass('alert-danger');
                                    fieldsErrorsNumber++;
                                }
                            }
                            else {
                                if ($(input).hasClass('alert-danger')) {
                                    $(input).removeClass('alert-danger');
                                    fieldsErrorsNumber--;
                                }
                            }
                        });
                        inputGroup.append(span).append(input);
                        $('#triggerFieldsDiv').append(inputGroup);
                        $compile(input)($scope);
                    })($scope.recipe, arg);
                }

                else
                    delete($scope.recipe.recipeTriggerFields[arg]);
            }

            for (arg in $scope.recipe.recipeActionFields) {
                if ($scope.recipe.action.actionFields[arg].publishable === true) {

                    (function (recipe, arg) {
                        var inputGroup = $('<div>').attr({
                            class: 'input-group'
                        });
                        var span = ($('<span>').attr({class: 'input-group-addon'}).text(recipe.action.actionFields[arg].name));
                        var input = fieldInputFactory.createInput(recipe.action.actionFields[arg].type, recipe.recipeActionFields[arg], 'recipe.recipeActionFields.' + arg + '.value');
                        var button = ($('<div>').attr({
                            class: 'input-group-addon',
                            'data-toggle': 'modal',
                            'data-target': '#ingredientsModal'
                        }));
                        button.append($('<i>').attr({'class': 'fa fa-flask'}));
                        button.on('click', function () {
                            $scope.inputSelected = input
                        });
                        $(input).change(function () {
                            if ($(input).hasClass('ng-invalid')) {
                                if ($(input).hasClass('alert-danger') == false) {
                                    $(input).addClass('alert-danger');
                                    fieldsErrorsNumber++;
                                }
                            }
                            else {
                                if ($(input).hasClass('alert-danger')) {
                                    $(input).removeClass('alert-danger');
                                    fieldsErrorsNumber--;
                                }
                            }
                        });
                        inputGroup.append(span).append(input);
                        if ($scope.recipe.action.actionFields[arg].type == 'TEXT' ||
                            $scope.recipe.action.actionFields[arg].type == 'LONGTEXT' ||
                            $scope.recipe.action.actionFields[arg].type == 'NULLABLETEXT') inputGroup.append(button);

                        $('#actionFieldsDiv').append(inputGroup);
                        $compile(input)($scope);
                    })($scope.recipe, arg);

                    $scope.recipe.recipeActionFields[arg].title = $scope.recipe.action.actionFields[arg].name;
                }
                else
                    delete($scope.recipe.recipeActionFields[arg]);
            }
        }, function errorCallback() {
        })

    }]);
