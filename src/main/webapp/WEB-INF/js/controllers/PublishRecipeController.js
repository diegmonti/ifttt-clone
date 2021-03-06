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
            }, function errorCallback(response) {
                $scope.error = true;
                if (response.data.hasOwnProperty('message')) {
                    $scope.errorMessage = response.data.message;
                    $('html,body').animate({scrollTop: $('body').offset().top}, 'slow');
                } else {
                    $scope.errorMessage = "There was an error in the " + response.data.context.toLowerCase() + " field " + response.data.field;
                    if (response.data.context === "TRIGGER") {
                        // Now I need to sign as red the wrong field, and remove it from the others.
                        $('#triggerFieldsDiv').children().each(function (index, value) {
                            // I know this are divs that contain a span and an input / textArea
                            var error = false;
                            if ($($(value).children()[0]).text() === response.data.field) {
                                error = true;
                            }
                            if (error === true) {
                                $(value).addClass('has-danger');
                                $('html,body').animate({scrollTop: $(value).offset().top}, 'slow');
                            }
                        });
                    } else {
                        $('#actionFieldsDiv').children().each(function (index, value) {
                            // I know this are divs that contain a span and an input / textArea
                            var error = false;
                            if ($($(value).children()[0]).text() === response.data.field) {
                                error = true;
                            }
                            if (error === true) {
                                $(value).addClass('has-danger');
                                $('html,body').animate({scrollTop: $(value).offset().top}, 'slow');
                            }
                        });
                    }
                }
            });
        };

        self.insertIngredient = function () {
            // In $scope.inputSelected I have the input where I should place the new element.
            // In $scope.selectedIngredient I have the ingredient that that user wants to insert.
            // $scope.model contains the selected action field.
            var $txt, caretPos, textAreaTxt, txtToAdd;
            $txt = $($scope.inputSelected);
            caretPos = $txt[0].selectionStart;
            textAreaTxt = $txt.val();
            txtToAdd = "{{" + $scope.selectedIngredient.name + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

        // Now I need to populate the recipe object
        $http({
            method: 'GET',
            url: 'api/myrecipes/' + $routeParams.recipeID
        }).then(function successCallback(response) {
            var arg, input;
            $scope.recipe = response.data;
            $scope.triggerChannelImage = 'img/' + response.data.trigger.channel + '.png';
            $scope.actionChannelImage = 'img/' + response.data.action.channel + '.png';

            function invertErrorClass(event) {
                if ($(event.target).hasClass('ng-invalid')) {
                    if ($(event.target).parent().hasClass('has-danger') === false) {
                        $(event.target).parent().addClass('has-danger');
                    }
                } else {
                    if ($(event.target).parent().hasClass('has-danger')) {
                        $(event.target).parent().removeClass('has-danger');
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
