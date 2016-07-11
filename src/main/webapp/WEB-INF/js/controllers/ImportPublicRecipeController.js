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
            var property;

            // response.data contains the public recipe
            $scope.recipe = response.data;

            // It is not what I need in the private recipe
            delete ($scope.recipe.description);

            // Now I need to create the private recipeTriggerFields and recipeActionField
            $scope.recipe.recipeTriggerFields = {};
            $scope.recipe.recipeActionFields = {};

            // Populating them
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

            for (property in $scope.recipe.publicRecipeActionFields) {
                if ($scope.recipe.publicRecipeActionFields.hasOwnProperty(property)) {
                    $scope.recipe.recipeActionFields[property].value = $scope.recipe.publicRecipeActionFields[property].value;
                }
            }
            delete ($scope.recipe.publicRecipeActionFields);
            delete ($scope.recipe.publicRecipeTriggerFields);

            $scope.triggerChannelImage = 'img/' + $scope.recipe.trigger.channel + '.png';
            $scope.actionChannelImage = 'img/' + $scope.recipe.action.channel + '.png';

            function populateTriggerFields(property) {
                var inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                var span = ($('<span>').attr({class: 'input-group-addon'}).text($scope.recipe.trigger.triggerFields[property].name));
                var input = fieldInputFactory.createInput($scope.recipe.trigger.triggerFields[property].type, $scope.recipe.recipeTriggerFields[property], 'recipe.recipeTriggerFields.' + property + '.value');

                $(input).change(function () {
                    if ($(input).hasClass('ng-invalid')) {
                        if ($(input).parent().hasClass('has-danger') === false) {
                            $(input).parent().addClass('has-danger');
                            fieldsErrorsNumber++;
                        }
                    } else {
                        if ($(input).parent().hasClass('has-danger')) {
                            $(input).parent().removeClass('has-danger');
                            fieldsErrorsNumber--;
                        }
                    }
                });

                inputGroup.append(span).append(input);
                $('#triggersDiv').append(inputGroup);
                $compile(input)($scope);
            }

            // Now I need to populate the divs
            for (property in $scope.recipe.recipeTriggerFields) {
                if ($scope.recipe.recipeTriggerFields.hasOwnProperty(property)) {
                    populateTriggerFields(property);
                }
            }

            function populateActionFields(property) {
                var inputGroup = $('<div>').attr({
                    class: 'input-group'
                });
                var span = ($('<span>').attr({class: 'input-group-addon'}).text($scope.recipe.action.actionFields[property].name));
                var input = fieldInputFactory.createInput($scope.recipe.action.actionFields[property].type, $scope.recipe.recipeActionFields[property], 'recipe.recipeActionFields.' + property + '.value');

                var button = ($('<div>').attr({
                    class: 'input-group-addon',
                    'data-toggle': 'modal',
                    'data-target': '#ingredientsModal'
                }));
                button.append($('<i>').attr({'class': 'fa fa-flask'}));
                button.on('click', function () {
                    $scope.inputSelected = input;
                    $scope.model = property;
                });

                $(input).change(function () {
                    if ($(input).hasClass('ng-invalid')) {
                        if ($(input).parent().hasClass('has-danger') === false) {
                            $(input).parent().addClass('has-danger');
                            fieldsErrorsNumber++;
                        }
                    } else {
                        if ($(input).parent().hasClass('has-danger')) {
                            $(input).parent().removeClass('has-danger');
                            fieldsErrorsNumber--;
                        }
                    }
                });

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
                    populateActionFields(property);
                }
            }

        }, function errorCallback(response) {
            console.error(response);
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
            }, function errorCallback(response) {
                $scope.error = true;
                $scope.errorMessage = "There was an error in the " + response.data.context.toLowerCase() + " field " + response.data.field;

                if (response.data.context === "TRIGGER") {
                    // Now I need to sign as red the wrong field, and remove it from the others.
                    $('#triggersDiv').children().each(function (index, value) {
                        // I know this are divs that contain a span and an input / textArea
                        var error = false;
                        if ($($(value).children()[0]).text() === response.data.field) {
                            error = true;
                        }
                        if (error === true) {
                            $(value).addClass('has-danger');
                            fieldsErrorsNumber++;
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
                            fieldsErrorsNumber++;
                            $('html,body').animate({scrollTop: $(value).offset().top}, 'slow');
                        }
                    });
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
            txtToAdd = "{{" + $scope.selectedIngredient + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

        self.favoriteRecipe = function () {
            var promise;
            if ($scope.recipe.favorite === true) {
                promise = $http.post('api/publicrecipes/' + $scope.recipe.id + '/remove');
            } else {
                promise = $http.post('api/publicrecipes/' + $scope.recipe.id + '/add');
            }
            promise.then(function successCallback() {
                $scope.recipe.favorite = !$scope.recipe.favorite;
            }, function errorCallback(response) {
                console.error(response);
            });
        };
    }]);
