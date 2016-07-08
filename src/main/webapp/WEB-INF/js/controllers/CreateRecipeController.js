iftttclone.controller('CreateRecipeController', ['$scope', '$rootScope', '$http', '$timeout', '$compile', '$location', 'fieldInputFactory',
    function ($scope, $rootScope, $http, $timeout, $compile, $location, fieldInputFactory) {
        if ($rootScope.authenticated === false) {
            $location.path("/login");
        }

        var self = this, fieldsErrorsNumber = 0;
        self.currentSelected = "";
        $scope.recipe = {};
        $scope.recipe.title = "";
        $scope.triggerChannels = [];
        $scope.actionChannels = [];
        $scope.triggers = [];
        $scope.actions = [];
        $scope.triggerChannelNotConnected = false;


        /**
         * This function downloads the channels. Based on the self.currentSelected
         * property, it filters them checking that they support triggers/actions
         * and then puts them in the right array (triggerChannels/actionChannels).
         * Using the ng-repeat directive, the interface will be populated
         * accordingly.
         */
        function downloadChannels($event) {
            $scope.channels = [];
            $http({
                method: 'GET',
                url: 'api/channels'
            }).then(
                function successCallback(result) {
                    $($event.target).prop('disabled', true);
                    result.data.forEach(function (element) {
                        if (self.currentSelected === "trigger" && element.triggers) {
                            $scope.triggerChannels.push({
                                id: element.id,
                                title: element.name,
                                description: element.description,
                                link: 'img/' + element.id + '.png'
                            });
                        } else if (self.currentSelected === "action" && element.actions) {
                            $scope.actionChannels.push({
                                id: element.id,
                                title: element.name,
                                description: element.description,
                                link: 'img/' + element.id + '.png'
                            });
                        }
                    });
                },
                function errorCallback(result) {
                    console.log(result);
                }
            );
        }

        /**
         * This function downloads the triggers for the selected channel and
         * puts them into the triggers array.
         * The ng-directive will populate the view accordingly.
         */
        function downloadTriggers() {
            $scope.triggers = [];
            $http({
                method: 'GET',
                url: 'api/channels/' + $scope.recipe.trigger.channel
            }).then(
                function successCallback(result) {
                    var trigger;
                    if (result.data.connectionTime === null && result.data.withConnection === true) {
                        $scope.triggerChannelNotConnected = true;
                        return;
                    }
                    $scope.triggerChannelNotConnected = false;
                    for (trigger in result.data.triggers) {
                        if (result.data.triggers.hasOwnProperty(trigger)) {
                            $scope.triggers.push({
                                title: result.data.triggers[trigger].name,
                                description: result.data.triggers[trigger].description,
                                method: trigger
                            });
                        }
                    }
                }
            );
        }

        /**
         * This function downloads the trigger fields and directly inserts them
         * into the view, appending them to the div with id triggerFieldsDiv.
         */
        function downloadTriggerFields() {
            $scope.recipe.recipeTriggerFields = {};
            $('#triggerFieldsDiv').empty();
            $http({
                method: 'GET',
                url: 'api/channels/' + $scope.recipe.trigger.channel
            }).then(
                function successCallback(result) {
                    var index, triggerFields, div, button;

                    // first i need to save the ingredients
                    $scope.recipe.trigger.ingredients = result.data.triggers[$scope.recipe.trigger.method].ingredients;

                    function checkField(index, element) {
                        var label, input;
                        div = $('<div>').attr({class: 'input-group row'});
                        label = $('<span>').attr({class: 'input-group-addon'}).text(element.name);
                        $scope.recipe.recipeTriggerFields = {};
                        $scope.recipe.recipeTriggerFields[index] = {value: ''};
                        input = fieldInputFactory.createInput(element.type, $scope.recipe.recipeTriggerFields[index], 'recipe.recipeTriggerFields.' + index + '.value');

                        $(input).change(function () {
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
                        });
                        $compile(input)($scope);

                        div.append(label).append(input);
                        $('#triggerFieldsDiv').append(div);
                    }

                    triggerFields = result.data.triggers[$scope.recipe.trigger.method].triggerFields;
                    for (index in triggerFields) {
                        if (triggerFields.hasOwnProperty(index)) {
                            checkField(index, triggerFields[index]);
                        }
                    }
                    div = $('<div>').attr({class: 'row', id: 'acceptTriggerButton'});
                    button = $('<button>').attr({
                        class: 'btn btn-primary col-lg-4 col-lg-offset-3'
                    }).text("Confirm");
                    div.append($('<br>')).append(button);
                    $('#triggerFieldsDiv').append(div);
                    button.on('click', self.acceptTriggerFields);

                }
            );
        }

        /**
         * This function downloads the actions for the selected channel and
         * puts them into the actions array.
         * The ng-directive will populate the view accordingly.
         */
        function downloadActions() {
            $scope.actions = [];
            $http({
                method: 'GET',
                url: 'api/channels/' + $scope.recipe.action.channel
            }).then(
                function successCallback(result) {
                    var element;
                    if (result.data.connectionTime === null && result.data.withConnection === true) {
                        $scope.actionChannelNotConnected = true;
                        return;
                    }
                    $scope.actionChannelNotConnected = false;
                    for (element in result.data.actions) {
                        if (result.data.actions.hasOwnProperty(element)) {
                            $scope.actions.push({
                                title: result.data.actions[element].name,
                                description: result.data.actions[element].description,
                                method: element
                            });
                        }
                    }
                }
            );
        }

        /**
         * This function downloads the action fields and directly inserts them
         * into the view, appending them to the div with id actionFieldsDiv.
         */
        function downloadActionFields() {
            $scope.recipe.recipeActionFields = {};
            $http({
                method: 'GET',
                url: 'api/channels/' + $scope.recipe.action.channel
            }).then(
                function successCallback(result) {
                    var index, actionFields, element, div, button;

                    function f1(index, element) {
                        var label, input;
                        div = $('<div>').attr({class: 'input-group row'});
                        label = $('<span>').attr({class: 'input-group-addon'}).text(element.name);
                        $scope.recipe.recipeActionFields[index] = {value: ''};

                        input = fieldInputFactory.createInput(element.type, $scope.recipe.recipeActionFields[index], 'recipe.recipeActionFields.' + index + '.value');

                        button = ($('<div>').attr({
                            class: 'input-group-addon',
                            'data-toggle': 'modal',
                            'data-target': '#ingredientsModal'
                        }));
                        button.append($('<i>').attr({'class': 'fa fa-flask'}));
                        button.on('click', function () {
                            $scope.inputSelected = input;
                            $scope.model = index;
                        });

                        $(input).change(function () {
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
                        });

                        $compile(input)($scope);
                        div.append(label).append(input);
                        if (element.type === 'TEXT' || element.type === 'LONGTEXT' || element.type === 'NULLABLETEXT') {
                            div.append(button);
                        }
                        $('#actionFieldsDiv').append(div);
                    }

                    actionFields = result.data.actions[$scope.recipe.action.method].actionFields;
                    for (index in actionFields) {
                        if (actionFields.hasOwnProperty(index)) {
                            element = result.data.actions[$scope.recipe.action.method].actionFields[index];
                            f1(index, element);
                        }
                    }
                    $('#confirmDiv').empty();
                    div = $('<div>').attr({class: 'row', id: 'acceptActionButton'});
                    button = $('<button>').attr({
                        class: 'btn btn-primary col-lg-4 col-lg-offset-3'
                    }).text("Confirm");
                    div.append($('<br>')).append(button);
                    $('#actionFieldsDiv').append(div);
                    button.on('click', self.acceptActionsFields);
                }
            );
        }

        /**
         * This function takes the $scope.recipe and sends it to the server.
         * If the response is negative, the page is scrolled back to the top where
         * an error message will appear.
         */
        function createRecipe() {
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
                $scope.errorMessage = "Error in for the " + result.data.context + " " + result.data.field;
                $('html,body').animate({scrollTop: $("body").offset().top}, 'slow');


                // Now i need to sign as red the wrong field, and remove it from the others.
                $('#triggerFieldsDiv').children().each(function (index, value) {
                    // I know this are divs that contain a span and an input / textArea
                    var error = false;
                    if ($($(value).children()[0]).text() === result.data.field) {
                        error = true;
                    }
                    if (error === true) {
                        $($(value).children()[1]).addClass('alert-danger');
                        fieldsErrorsNumber++;
                    }
                });
                $('#actionFieldsDiv').children().each(function (index, value) {
                    // I know this are divs that contain a span and an input / textArea
                    var error = false;
                    if ($($(value).children()[0]).text() === result.data.field) {
                        error = true;
                    }
                    if (error === true) {
                        $($(value).children()[1]).addClass('alert-danger');
                        fieldsErrorsNumber++;
                    }
                });


            });
        }

        /**
         * This function is called when the user clicks on the trigger link, it sets to 'trigger'
         * the variable currentSelected, then calls downloadChannels.
         */
        self.selectTriggerClicked = function ($event) {
            self.currentSelected = "trigger";
            downloadChannels($event);
            $('html,body').animate({scrollTop: $("#channelTriggersDiv").offset().top}, 'slow');
        };

        /**
         * This function is called when the user clicks on the card with the action he wants.
         */
        self.selectActionClicked = function ($event) {
            self.currentSelected = "action";
            downloadChannels($event);
            $('html,body').animate({scrollTop: $("#channelActionsDiv").offset().top}, 'slow');
        };

        /**
         * This function is called when a card representing a channel is clicked.
         * Depending on the current status of currentSelected, either the function
         * downloadTriggers or downloadActions will be called.
         */
        self.channelSelected = function (id, currentSelected) {
            var image = $('<img>');
            $(image).attr("src", "img/" + id + ".png");
            $(image).attr("width", "110px");

            if (currentSelected === "trigger") {
                $('#triggerFieldsDiv').empty();
                $('#actionFieldsDiv').empty();

                delete ($scope.recipe.recipeTriggerFields);
                delete ($scope.recipe.recipeActionFields);

                $scope.recipe.trigger = {};
                $scope.recipe.trigger.channel = id;
                $("#triggerDiv").html(image);
                downloadTriggers();
                $('html,body').animate({scrollTop: $("#triggerChoicesDiv").offset().top}, 'slow');

            } else if (currentSelected === "action") {
                $('#actionFieldsDiv').empty();
                delete ($scope.recipe.recipeActionFields);

                $scope.recipe.action = {};
                $scope.recipe.action.channel = id;
                $("#actionDiv").html(image);
                downloadActions();
                $('html,body').animate({scrollTop: $("#actionsChoicesDiv").offset().top}, 'slow');
            }
        };


        /**
         * This function is called when the user clicks on the card with the
         * trigger he wants. It sets the proper values on $scope.recipe, then
         * calls downloadTriggerFields.
         */
        self.triggerSelected = function (id, name) {
            $scope.recipe.trigger.method = id;
            $scope.recipe.trigger.name = name;
            downloadTriggerFields();
            $('html,body').animate({scrollTop: $("#triggerFieldsContainer").offset().top}, 'slow');
        };

        /**
         * This function is called when the user clicks on the confirm button for the trigger, it
         * creates a link in the top recipe notation and then scrolls to it.
         */
        self.acceptTriggerFields = function () {
            if (fieldsErrorsNumber !== 0) {
                return;
            }
            $('#acceptTriggerButton').hide();

            var link = $('<button data-ng-click="controller.selectActionClicked($event)" class="btn btn-link">that</button>');
            $('#actionDiv').html(link);
            $compile(link)($scope);

            $('html,body').animate({scrollTop: $("body").offset().top}, 'slow');

        };

        /**
         * This function is called when the user clicks on the card with the
         * action he wants. It sets the proper values on $scope.recipe, then
         * calls downloadActionFields.
         */
        self.actionSelected = function (id, name) {
            $scope.recipe.action.method = id;
            $scope.recipe.action.name = name;
            downloadActionFields();
            $('html,body').animate({scrollTop: $("#actionFieldsContainer").offset().top}, 'slow');
        };

        /**
         * This function is called when the user clicks on the confirm button for the action
         * triggers, it calls createRecipe.
         */
        self.acceptActionsFields = function () {
            if (fieldsErrorsNumber !== 0) {
                return;
            }
            createRecipe();
        };

        /**
         * This function is called when the insert button is pressed in the modal.
         * It inserts in the correct place the selected ingredient.
         */
        self.insertIngredient = function () {
            var $txt, caretPos, textAreaTxt, txtToAdd;
            // in $scope.inputSelected I have the input where I should place the new element
            // in $scope.selectedIngredient I have the ingredient that that user wants to insert
            // $scope.model contains the selected action field
            $txt = $($scope.inputSelected);
            caretPos = $txt[0].selectionStart;
            textAreaTxt = $txt.val();
            txtToAdd = "{{" + $scope.selectedIngredient.name + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

    }]);
