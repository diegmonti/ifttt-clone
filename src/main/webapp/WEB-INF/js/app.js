var iftttclone = angular.module('iftttcloneApp', ['ngRoute']);

iftttclone.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'partials/channels.html',
            controller: 'ChannelsController',
            controllerAs: 'controller'
        })
        .when('/login/:registered?', {
            templateUrl: 'partials/login.html',
            controller: 'LoginController',
            controllerAs: 'controller'
        })
        .when('/signIn', {
            templateUrl: 'partials/signIn.html',
            controller: 'SignInController',
            controllerAs: 'controller'
        })
        .when('/channel/:channelID', {
            templateUrl: 'partials/channel.html',
            controller: 'ChannelController',
            controllerAs: 'controller'
        })
        .when('/myRecipes', {
            templateUrl: 'partials/recipes.html',
            controller: 'PrivateRecipeController',
            controllerAs: 'controller'
        })
        .when('/modifyRecipe/:recipeId', {
            templateUrl: 'partials/modifyRecipe.html',
            controller: 'ModifyRecipeController',
            controllerAs: 'controller'
        })
        .when('/createRecipe', {
            templateUrl: 'partials/createRecipe.html',
            controller: 'CreateRecipeController',
            controllerAs: 'controller'
        })
        .when('/publishRecipe/:recipeID', {
            templateUrl: 'partials/publishRecipe.html',
            controller: 'PublishRecipeController',
            controllerAs: 'controller'
        })
        .when('/importPublicRecipe/:publicRecipeId', {
            templateUrl: 'partials/importPublicRecipe.html',
            controller: 'ImportPublicRecipeController',
            controllerAs: 'controller'
        })
        .when('/publicRecipes', {
            templateUrl: 'partials/publicRecipes.html',
            controller: 'PublicRecipesController',
            controllerAs: 'controller'
        }).when('/publicRecipes/:search', {
            templateUrl: 'partials/publicRecipes.html',
            controller: 'PublicRecipesController',
            controllerAs: 'controller'
        })
        .when('/preferences', {
            templateUrl: 'partials/preferences.html',
            controller: 'PreferencesController',
            controllerAs: 'controller'
        })
        .when('/recipeLog/:recipeId/:pageId?', {
            templateUrl: 'partials/recipeLog.html',
            controller: 'RecipeLogController',
            controllerAs: 'controller'
        })
        .when('/favoriteRecipes', {
            templateUrl: 'partials/favoriteRecipes.html',
            controller: 'FavoriteRecipesController',
            controllerAs: 'controller'
        }).when('/publishedRecipes', {
            templateUrl: 'partials/publishedRecipes.html',
            controller: 'PublishedRecipesController',
            controllerAs: 'controller'
        })
        .otherwise('/');
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}]);

iftttclone.factory('fieldInputFactory', function () {
    var factory = {};
    factory.createInput = function (type, field, model) {
        var input;
        if (type === 'TEMPERATURE') {
            input = $('<select>')
                .append($('<option>').val('C').text('Celsius'))
                .append($('<option>').val('F').text('Fahrenheit'));
        } else if (type === 'EMAIL') {
            input = $('<input>').attr('type', 'email');
        } else if (type === 'LONGTEXT') {
            input = $('<textarea>').attr({
                rows: "5",
                'required': true
            });
        } else if (type === 'INTEGER') {
            input = $('<input>').attr('type', 'number');
            // now i need to transform the value in the correspondent number
            field.value = Number(field.value);
        } else if (type === 'TIME') {
            input = $('<input>').attr({
                'type': 'text',
                'placeholder': 'hh:mm',
                'pattern': '([0-1][0-9]:[0-5][0-9])|(2[0-3]:[0-5][0-9])',
                'required': true
            });
        } else if (type === 'TIMESTAMP') {
            input = $('<input>').attr({
                'type': 'text',
                'placeholder': 'dd/MM/yyyy HH:mm',
                'required': true,
                'pattern': '^(((((0[1-9]|1[0-9]|2[0-8])[\/](0[1-9]|1[012]))|((29|30|31)[\/](0[13578]|1[02]))|((29|30)[\/](0[4,6,9]|11)))[\/](19|[2-9][0-9])[0-9][0-9])|(^29[\/]02[\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96))) ([01][0-9]|2[0-3]):([0-5][0-9])$'

            });
        } else if (type === 'TEXT') {
            input = $('<input>').attr({
            	'required': true,
              'type': 'text'
            });
        } else if (type === 'LOCATION') {
            input = $('<input>').attr({
              'type': 'text',
              'required': true
            });
        }
        else {
          input = $('<input>').attr('type', 'text');
        }

        input.attr({
            class: "form-control",
            'aria-describedby': "basic-addon3",
            'data-ng-model': model
        });

        return input;
    };
    return factory;
});
