iftttclone.controller('CreateRecipeController', ['$scope', '$rootScope', '$http', '$timeout', '$compile', '$location', 'fieldInputFactory',
    function ($scope, $rootScope, $http, $timeout, $compile, $location, fieldInputFactory) {

        var self = this, fieldsErrorsNumber = 0;
        self.currentSelected = "";
        $scope.recipe = {};
        $scope.triggerChannels = [];
        $scope.actionChannels = [];
        $scope.triggers = [];
        $scope.actions = [];
        $scope.triggerChannelNotConnected = false;

        function downloadChannels() {
            $scope.channels = [];
            $http({
                method: 'GET',
                url: 'api/channels'
            }).then(
                function successCallback(result) {
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

        function downloadActions() {
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
                    $scope.actions = [];
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

        function downloadActionFields() {
            $scope.recipe.recipeTriggerFields = {};
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

                        $scope.recipe.recipeActionFields = {};
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
                $scope.errorMessage = result.data.message;

                console.log($scope.recipe);
                (function () {
                    var ol = $('<ol>').attr('class', 'breadcrumb');
                    ol.append(
                        $('<li>').attr('class', 'active').text('Trigger: ' + $scope.recipe.trigger.name)
                    );
                    $('#triggerFieldsHeaderDiv').empty().append(ol);
                }());

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

        self.selectTriggerClicked = function () {
            self.currentSelected = "trigger";
            downloadChannels();
        };

        self.selectActionClicked = function () {
            self.currentSelected = "action";
            downloadChannels();
        };

        self.channelSelected = function (id) {

            var image = $('<img>');
            $(image).attr("src", "img/" + id + ".png");
            $(image).attr("width", "110px");

            if (self.currentSelected === "trigger") {
                $('#triggerFieldsDiv').empty();
                $('#actionFieldsDiv').empty();

                delete ($scope.recipe.recipeTriggerFields);
                delete ($scope.recipe.recipeActionFields);

                $scope.recipe.trigger = {};
                $scope.recipe.trigger.channel = id;
                $("#triggerDiv").html(image);
                downloadTriggers();
            } else if (self.currentSelected === "action") {
                $('#actionFieldsDiv').empty();
                delete ($scope.recipe.recipeActionFields);

                $scope.recipe.action = {};
                $scope.recipe.action.channel = id;
                $("#actionDiv").html(image);
                downloadActions();
            }
        };

        self.triggerSelected = function (id, name) {
            $scope.recipe.trigger.method = id;
            $scope.recipe.trigger.name = name;
            downloadTriggerFields();
        };

        self.acceptTriggerFields = function () {
            if (fieldsErrorsNumber !== 0) {
                return;
            }
            $('#acceptTriggerButton').hide();

            var link = $('<a>').attr({
                class: 'btn btn-link'
            }).text('that').on('click', self.selectActionClicked);
            $('#actionDiv').html(link);
        };

        self.actionSelected = function (id, name) {
            $scope.recipe.action.method = id;
            $scope.recipe.action.name = name;
            downloadActionFields();
        };

        self.acceptActionsFields = function () {
            if (fieldsErrorsNumber !== 0) {
                return;
            }
            $('#acceptActionButton').hide();

            var button = $('<button>').attr({
                class: 'btn btn-primary col-lg-4 col-lg-offset-3'
            }).text('Confirm');
            button.on('click', createRecipe);
            $('#confirmDiv').append(button);
        };

        self.insertIngredient = function () {
            var $txt, caretPos, textAreaTxt, txtToAdd;
            // in $scope.inputSelected i have the input where i should place the new element
            // in $scope.selectedIngredient i have the ingredient that that user wants to insert
            // $scope.model contains the selected action field
            $txt = $($scope.inputSelected);
            caretPos = $txt[0].selectionStart;
            textAreaTxt = $txt.val();
            txtToAdd = "{{" + $scope.selectedIngredient + "}}";
            $scope.recipe.recipeActionFields[$scope.model].value = (textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos));
        };

    }]);