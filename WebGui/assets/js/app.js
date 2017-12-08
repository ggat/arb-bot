var app = angular.module('matcher', []);


app.controller('MainCtrl', function($scope) {

    $scope.name = 'World';
    $scope.data = [
        {
            name : "AdjaraBet",
            items: [
                "UEFA",
                "Kutu"
            ]
        },
        {
            name : "LiderBet",
            items: [
                "UEFA",
                "Kutu"
            ]
        },
        {
            name : "CrocoBet",
            items: [
                "UEFA",
                "Kutu"
            ]
        },
        {
            name : "EuropeBet",
            items: [
                "UEFA",
                "Kutu"
            ]
        }
    ];
});