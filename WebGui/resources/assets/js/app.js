import {server} from './modules/server';

var underscore = angular.module('underscore', []);
underscore.factory('_', ['$window', function ($window) {
    return $window._; // assumes underscore has already been loaded on the page
}]);

var app = angular.module('matcher', ['localytics.directives', 'underscore', 'ui.router', 'server'])

app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/home');

/*    $stateProvider.state("zone", {
        url: "/",
        templateUrl: "views/zone.html",
    });*/

    $stateProvider
        .state('home', {
            url: '/home',
            controller: 'MainCtrl',
            templateUrl: 'main',
            resolve: {

                // Get AngularJS resource to query
                //Server: 'Server',

                // Use the resource to fetch data from the server
                bookieData: ['Server', function (Server) {
                    return Server.getCategoryInfos();
                }],

                // Use the resource to fetch data from the server
                chains: ['Server', function (Server) {
                    return Server.getChains();
                }]
            }
        })
        .state('home.sub', {
            url: '/sub/{chainIndex}',
            controller: 'SubCtrl',
            templateUrl: 'table'
        })
        .state('fail', {
            url: '/fail/{message}',
            template: '<h1>Failure</h1><pre>{{message}}</pre>',
            controller: function ($scope, $stateParams) {
                $scope.message = $stateParams.message;
            }
        });
});

app.run(function ($state, $rootScope) {
    $state.go('home');

    $rootScope.$on('$stateChangeError',
        function(event, toState, toParams, fromState, fromParams, error){
            console.error("Error was: ", error);
            $state.go('fail', { message: error }); // careful not to create an infinite loop here
        });

    /*$state.defaultErrorHandler(function(error) {
        $state.go('fail', { errorMessage: error }); // careful not to create an infinite loop here
    });*/
});

console.log("Just one");

var MainCtrl = function ($scope, _, $state, bookieData, chains, Server) {

    console.log("bookieData resolved in MainCtrl: ", bookieData);

    console.log(typeof chains);
    console.log("Length of existing chains: " + chains.length);

    $scope.name = 'World';
    //$scope.bookieData = bookieData;
    console.log('bookieData: ', bookieData);
    $scope.bookieData = bookieData;
    $scope.chains = chains;
    $scope.newChainInitiatorRowModel = {};

    $scope.storeChains = function () {
        Server.storeChains($scope.chains).then(function (res) {
            alert("Saved!")
        }, function (res) {
            alert("Failed!")
        });
    };

    $scope.getItemNameByBookieIdAndItemId = function (bookieId, itemId) {

        var bookie = _.findWhere($scope.bookieData, {id : bookieId});
        if(!bookie) return null;

        var item = _.findWhere(bookie.items, {id : itemId});

        return item ? item.name : null;
    };

    /**
     * NICR selects
     *
     * @type {{cd
     *  key : "bookieId" Id of bookie.
     *  value : items[] list of items that are not used to any chain yet.
     * }}
     */
    $scope.chainInititatorRowData = {};
    $scope.updateChainInititatorRowData = function () {

        $scope.chainInititatorRowData = {};
        for (var bookieIndex in $scope.bookieData) {
            var bookie = $scope.bookieData[bookieIndex];
            var bookieId = bookie.id;

            $scope.chainInititatorRowData[bookieId] = [];

            //Iterate over each item of this bookie
            for (var iii in bookie.items) {
                var category = bookie.items[iii];

                //Iterate over each chain
                var used;
                for (var index in $scope.chains) {

                    // Check if there is an item for this bookie in this chain
                    // and it matches current category. This means category is used by chain.
                    var bookieChaninItem = $scope.chains[index][bookieId];
                    used = bookieChaninItem == category.id;
                    if(used) {
                        break;
                    }
                }

                if (!used) {
                    $scope.chainInititatorRowData[bookieId].push(category);
                }
            }
        }
    };

    $scope.getCategoryByIdForBookieAndChain = function (bookieId, chain) {

        $scope.updateChainInititatorRowData();

        var resultCategory = null;

        var bookie = _.findWhere($scope.bookieData, {id : bookieId});

        _.each(bookie.items, function (category) {
            if(category.id == chain[bookieId]) {
                resultCategory = category;
            }
        });

        var result = $scope.chainInititatorRowData[bookieId].slice(0);
        result.push(resultCategory);

        return result;
    };

    $scope.unbindChainInitiatorWatcher = $scope.$watchCollection('newChainInitiatorRowModel', function (newValue, oldValue, scope) {

        // Check if run after reset
        if (_.isEmpty(newValue)) {
            return;
        }

        $scope.chains.push(newValue);
        $scope.startChainEdit(newValue);

        //After chains are updated update initiator selects too.
        $scope.updateChainInititatorRowData();

        $scope.newChainInitiatorRowModel = {};
    });

    $scope.startChainEdit = function(chain) {
        /*if(chain.edit) {
         chain.edit = false;
         return;
         }*/

        console.log("Parents startChainEdit: did run");

        for(var chainIndex in $scope.chains) {
            $scope.chains[chainIndex].edit = false;
        }

        chain.edit = true;

        $state.go("home.sub", {chainIndex : $scope.chains.indexOf(chain)});
    };

    $scope.updateChainInititatorRowData();

    $scope.bookieCount = $scope.bookieData.length;
    $scope.bookieIndex = _.object(_.map($scope.bookieData, function (item, key) {
        return [item.id, key];
    }));

    $scope.tableTdMaxWith = 100 / $scope.bookieData.length + 2;
};
app.controller('MainCtrl', MainCtrl);

var SubCtrl = function ($scope, _, $stateParams, $state) {

    $scope.name = 'World';
    var parentChains = $scope.chains;
    var editingChain = parentChains[$stateParams.chainIndex];
    if(!editingChain) {
        $state.go('home');
        return;
    }

    if(!editingChain.subs) {
        editingChain.subs = [];
    }
    $scope.chains = editingChain.subs;
    $scope.newChainInitiatorRowModel = {};

    $scope.getParentCategoryForBookie = function(bookieId) {

        var bookie = _.findWhere($scope.bookieData, {id : bookieId});

        // Parent category for this bookie
        var parent = editingChain[bookieId];

        //If no parent category choosen for this bookie. Move to next bookie.
        var parentObject;
        if(!bookie || !parent || !(parentObject = _.findWhere(bookie.items, {id : parent}))) {
            return null;
        }

        return parentObject;
    };

    $scope.getItemNameByBookieIdAndItemId = function (bookieId, itemId) {

        var bookie = _.findWhere($scope.bookieData, {id : bookieId});
        if(!bookie) return null;

        var parentCategory = $scope.getParentCategoryForBookie(bookieId);

        //If no parent category choosen for this bookie. Move to next bookie.
        if(!parentCategory) return null;

        var categories = parentCategory.items;

        var item = _.findWhere(categories, {id : itemId});

        return item ? item.name : null;
    };

    /**
     * NICR selects
     *
     * @type {{
     *  key : "bookieId" Id of bookie.
     *  value : items[] list of items that are not used to any chain yet.
     * }}
     */
    $scope.chainInititatorRowData = {};
    $scope.updateChainInititatorRowData = function () {

        $scope.chainInititatorRowData = {};
        for (var bookieIndex in $scope.bookieData) {
            var bookie = $scope.bookieData[bookieIndex];
            var bookieId = bookie.id;

            //First reset items for this bookie.
            $scope.chainInititatorRowData[bookieId] = [];

            var parentCategory = $scope.getParentCategoryForBookie(bookieId);

            //If no parent category choosen for this bookie. Move to next bookie.
            if(!parentCategory) {
                continue;
            }

            var categories = parentCategory.items;

            //Iterate over each item of this bookie
            for (var iii in categories) {
                var category = categories[iii];

                //Iterate over each chain
                var used;
                for (var index in $scope.chains) {

                    // Check if there is an item for this bookie in this chain
                    // and it matches current category. This means category is used by chain.
                    var bookieChaninItem = $scope.chains[index][bookieId];
                    used = bookieChaninItem == category.id;
                    if(used) {
                        break;
                    }
                }

                if (!used) {
                    $scope.chainInititatorRowData[bookieId].push(category);
                }
            }
        }
    };

    $scope.unbindChainInitiatorWatcher = $scope.$watchCollection('newChainInitiatorRowModel', function (newValue, oldValue, scope) {

        // Check if run after reset
        if (_.isEmpty(newValue)) {
            return;
        }

        $scope.chains.push(newValue);
        $scope.startChainEdit(newValue);

        //After chains are updated update initiator selects too.
        $scope.updateChainInititatorRowData();

        $scope.newChainInitiatorRowModel = {};
    });

    $scope.startChainEdit = function(chain) {
        /*if(chain.edit) {
         chain.edit = false;
         return;
         }*/

        console.log("Subs startChainEdit: did run");

        for(var chainIndex in $scope.chains) {
            $scope.chains[chainIndex].edit = false;
        }

        chain.edit = true;

        //$state.go("home.sub", {chainIndex : $scope.chains.indexOf(chain)});
    };

    $scope.getCategoryByIdForBookieAndChain = function (bookieId, chain) {

        $scope.updateChainInititatorRowData();

        var result = [];
        var resultCategory = null;

        var parentCategory = $scope.getParentCategoryForBookie(bookieId);

        //If no parent category choosen for this bookie. Move to next bookie.
        if(!parentCategory) {
            return result;
        }

        var categories = parentCategory.items;

        _.each(categories, function (category) {
            if(category.id == chain[bookieId]) {
                resultCategory = category;
            }
        });

        result = $scope.chainInititatorRowData[bookieId].slice(0);
        result.push(resultCategory);

        return result;
    };

    $scope.$watchCollection(function () {
        return editingChain;
    }, function (newValue) {
        $scope.updateChainInititatorRowData();
    });

    $scope.updateChainInititatorRowData();
};

app.controller('SubCtrl', SubCtrl);

function getNewBookieData() {

    var bookieNames = [
        "AdjaraBet",
        "EuropeBet",
        "LiderBet",
        "CrocoBet",
        "BetLive",
        "CrystalBet"
    ];

    var bookieData = [];
    for (var i in bookieNames) {
        bookieData[i] = {
            id: i + 5620,
            name: bookieNames[i],
            items: []
        };

        var countries = getNewCouuntries();

        for (var index in countries) {

            if(index > 1) break;

            var country = countries[index];
            country.code = undefined;
            /*var countryName = country.name;

             if(countryName.length > 24) {
             countryName = countryName.substr(0, 24);
             }*/

            bookieData[i].items[index] = country;
        }
    }

    return bookieData;
}

function getNewCouuntries() {
    var newCouuntries = [];
    var idStartNumber = 1009;

    for (var index in _countries) {
        var country = _countries[index];
        country.id = idStartNumber + index;
        country.items = getNewClubs();
        newCouuntries[index] = country;
    }

    return newCouuntries;
}

var _countries = [
    {name: 'Afghanistan', code: 'AF'},
    {name: 'Åland Islands', code: 'AX'},
    {name: 'Albania', code: 'AL'},
    {name: 'Algeria', code: 'DZ'},
    {name: 'American Samoa', code: 'AS'},
    {name: 'AndorrA', code: 'AD'},
    {name: 'Angola', code: 'AO'},
    {name: 'Anguilla', code: 'AI'},
    {name: 'Antarctica', code: 'AQ'},
    {name: 'Antigua and Barbuda', code: 'AG'},
    {name: 'Argentina', code: 'AR'},
    {name: 'Armenia', code: 'AM'},
    {name: 'Aruba', code: 'AW'},
    {name: 'Australia', code: 'AU'},
    {name: 'Austria', code: 'AT'},
    {name: 'Azerbaijan', code: 'AZ'},
    {name: 'Bahamas', code: 'BS'},
    {name: 'Bahrain', code: 'BH'},
    {name: 'Bangladesh', code: 'BD'},
    {name: 'Barbados', code: 'BB'},
    {name: 'Belarus', code: 'BY'},
    {name: 'Belgium', code: 'BE'},
    {name: 'Belize', code: 'BZ'},
    {name: 'Benin', code: 'BJ'},
    {name: 'Bermuda', code: 'BM'},
    {name: 'Bhutan', code: 'BT'},
    {name: 'Bolivia', code: 'BO'},
    {name: 'Bosnia and Herzegovina', code: 'BA'},
    {name: 'Botswana', code: 'BW'},
    {name: 'Bouvet Island', code: 'BV'},
    {name: 'Brazil', code: 'BR'},
    {name: 'British Indian Ocean Territory', code: 'IO'},
    {name: 'Brunei Darussalam', code: 'BN'},
    {name: 'Bulgaria', code: 'BG'},
    {name: 'Burkina Faso', code: 'BF'},
    {name: 'Burundi', code: 'BI'},
    {name: 'Cambodia', code: 'KH'},
    {name: 'Cameroon', code: 'CM'},
    {name: 'Canada', code: 'CA'},
    {name: 'Cape Verde', code: 'CV'},
    {name: 'Cayman Islands', code: 'KY'},
    {name: 'Central African Republic', code: 'CF'},
    {name: 'Chad', code: 'TD'},
    {name: 'Chile', code: 'CL'},
    {name: 'China', code: 'CN'},
    {name: 'Christmas Island', code: 'CX'},
    {name: 'Cocos (Keeling) Islands', code: 'CC'},
    {name: 'Colombia', code: 'CO'},
    {name: 'Comoros', code: 'KM'},
    {name: 'Congo', code: 'CG'},
    {name: 'Congo, The Democratic Republic of the', code: 'CD'},
    {name: 'Cook Islands', code: 'CK'},
    {name: 'Costa Rica', code: 'CR'},
    {name: 'Cote D\'Ivoire', code: 'CI'},
    {name: 'Croatia', code: 'HR'},
    {name: 'Cuba', code: 'CU'},
    {name: 'Cyprus', code: 'CY'},
    {name: 'Czech Republic', code: 'CZ'},
    {name: 'Denmark', code: 'DK'},
    {name: 'Djibouti', code: 'DJ'},
    {name: 'Dominica', code: 'DM'},
    {name: 'Dominican Republic', code: 'DO'},
    {name: 'Ecuador', code: 'EC'},
    {name: 'Egypt', code: 'EG'},
    {name: 'El Salvador', code: 'SV'},
    {name: 'Equatorial Guinea', code: 'GQ'},
    {name: 'Eritrea', code: 'ER'},
    {name: 'Estonia', code: 'EE'},
    {name: 'Ethiopia', code: 'ET'},
    {name: 'Falkland Islands (Malvinas)', code: 'FK'},
    {name: 'Faroe Islands', code: 'FO'},
    {name: 'Fiji', code: 'FJ'},
    {name: 'Finland', code: 'FI'},
    {name: 'France', code: 'FR'},
    {name: 'French Guiana', code: 'GF'},
    {name: 'French Polynesia', code: 'PF'},
    {name: 'French Southern Territories', code: 'TF'},
    {name: 'Gabon', code: 'GA'},
    {name: 'Gambia', code: 'GM'},
    {name: 'Georgia', code: 'GE'},
    {name: 'Germany', code: 'DE'},
    {name: 'Ghana', code: 'GH'},
    {name: 'Gibraltar', code: 'GI'},
    {name: 'Greece', code: 'GR'},
    {name: 'Greenland', code: 'GL'},
    {name: 'Grenada', code: 'GD'},
    {name: 'Guadeloupe', code: 'GP'},
    {name: 'Guam', code: 'GU'},
    {name: 'Guatemala', code: 'GT'},
    {name: 'Guernsey', code: 'GG'},
    {name: 'Guinea', code: 'GN'},
    {name: 'Guinea-Bissau', code: 'GW'},
    {name: 'Guyana', code: 'GY'},
    {name: 'Haiti', code: 'HT'},
    {name: 'Heard Island and Mcdonald Islands', code: 'HM'},
    {name: 'Holy See (Vatican City State)', code: 'VA'},
    {name: 'Honduras', code: 'HN'},
    {name: 'Hong Kong', code: 'HK'},
    {name: 'Hungary', code: 'HU'},
    {name: 'Iceland', code: 'IS'},
    {name: 'India', code: 'IN'},
    {name: 'Indonesia', code: 'ID'},
    {name: 'Iran, Islamic Republic Of', code: 'IR'},
    {name: 'Iraq', code: 'IQ'},
    {name: 'Ireland', code: 'IE'},
    {name: 'Isle of Man', code: 'IM'},
    {name: 'Israel', code: 'IL'},
    {name: 'Italy', code: 'IT'},
    {name: 'Jamaica', code: 'JM'},
    {name: 'Japan', code: 'JP'},
    {name: 'Jersey', code: 'JE'},
    {name: 'Jordan', code: 'JO'},
    {name: 'Kazakhstan', code: 'KZ'},
    {name: 'Kenya', code: 'KE'},
    {name: 'Kiribati', code: 'KI'},
    {name: 'Korea, Democratic People\'S Republic of', code: 'KP'},
    {name: 'Korea, Republic of', code: 'KR'},
    {name: 'Kuwait', code: 'KW'},
    {name: 'Kyrgyzstan', code: 'KG'},
    {name: 'Lao People\'S Democratic Republic', code: 'LA'},
    {name: 'Latvia', code: 'LV'},
    {name: 'Lebanon', code: 'LB'},
    {name: 'Lesotho', code: 'LS'},
    {name: 'Liberia', code: 'LR'},
    {name: 'Libyan Arab Jamahiriya', code: 'LY'},
    {name: 'Liechtenstein', code: 'LI'},
    {name: 'Lithuania', code: 'LT'},
    {name: 'Luxembourg', code: 'LU'},
    {name: 'Macao', code: 'MO'},
    {name: 'Macedonia, The Former Yugoslav Republic of', code: 'MK'},
    {name: 'Madagascar', code: 'MG'},
    {name: 'Malawi', code: 'MW'},
    {name: 'Malaysia', code: 'MY'},
    {name: 'Maldives', code: 'MV'},
    {name: 'Mali', code: 'ML'},
    {name: 'Malta', code: 'MT'},
    {name: 'Marshall Islands', code: 'MH'},
    {name: 'Martinique', code: 'MQ'},
    {name: 'Mauritania', code: 'MR'},
    {name: 'Mauritius', code: 'MU'},
    {name: 'Mayotte', code: 'YT'},
    {name: 'Mexico', code: 'MX'},
    {name: 'Micronesia, Federated States of', code: 'FM'},
    {name: 'Moldova, Republic of', code: 'MD'},
    {name: 'Monaco', code: 'MC'},
    {name: 'Mongolia', code: 'MN'},
    {name: 'Montserrat', code: 'MS'},
    {name: 'Morocco', code: 'MA'},
    {name: 'Mozambique', code: 'MZ'},
    {name: 'Myanmar', code: 'MM'},
    {name: 'Namibia', code: 'NA'},
    {name: 'Nauru', code: 'NR'},
    {name: 'Nepal', code: 'NP'},
    {name: 'Netherlands', code: 'NL'},
    {name: 'Netherlands Antilles', code: 'AN'},
    {name: 'New Caledonia', code: 'NC'},
    {name: 'New Zealand', code: 'NZ'},
    {name: 'Nicaragua', code: 'NI'},
    {name: 'Niger', code: 'NE'},
    {name: 'Nigeria', code: 'NG'},
    {name: 'Niue', code: 'NU'},
    {name: 'Norfolk Island', code: 'NF'},
    {name: 'Northern Mariana Islands', code: 'MP'},
    {name: 'Norway', code: 'NO'},
    {name: 'Oman', code: 'OM'},
    {name: 'Pakistan', code: 'PK'},
    {name: 'Palau', code: 'PW'},
    {name: 'Palestinian Territory, Occupied', code: 'PS'},
    {name: 'Panama', code: 'PA'},
    {name: 'Papua New Guinea', code: 'PG'},
    {name: 'Paraguay', code: 'PY'},
    {name: 'Peru', code: 'PE'},
    {name: 'Philippines', code: 'PH'},
    {name: 'Pitcairn', code: 'PN'},
    {name: 'Poland', code: 'PL'},
    {name: 'Portugal', code: 'PT'},
    {name: 'Puerto Rico', code: 'PR'},
    {name: 'Qatar', code: 'QA'},
    {name: 'Reunion', code: 'RE'},
    {name: 'Romania', code: 'RO'},
    {name: 'Russian Federation', code: 'RU'},
    {name: 'RWANDA', code: 'RW'},
    {name: 'Saint Helena', code: 'SH'},
    {name: 'Saint Kitts and Nevis', code: 'KN'},
    {name: 'Saint Lucia', code: 'LC'},
    {name: 'Saint Pierre and Miquelon', code: 'PM'},
    {name: 'Saint Vincent and the Grenadines', code: 'VC'},
    {name: 'Samoa', code: 'WS'},
    {name: 'San Marino', code: 'SM'},
    {name: 'Sao Tome and Principe', code: 'ST'},
    {name: 'Saudi Arabia', code: 'SA'},
    {name: 'Senegal', code: 'SN'},
    {name: 'Serbia and Montenegro', code: 'CS'},
    {name: 'Seychelles', code: 'SC'},
    {name: 'Sierra Leone', code: 'SL'},
    {name: 'Singapore', code: 'SG'},
    {name: 'Slovakia', code: 'SK'},
    {name: 'Slovenia', code: 'SI'},
    {name: 'Solomon Islands', code: 'SB'},
    {name: 'Somalia', code: 'SO'},
    {name: 'South Africa', code: 'ZA'},
    {name: 'South Georgia and the South Sandwich Islands', code: 'GS'},
    {name: 'Spain', code: 'ES'},
    {name: 'Sri Lanka', code: 'LK'},
    {name: 'Sudan', code: 'SD'},
    {name: 'Suriname', code: 'SR'},
    {name: 'Svalbard and Jan Mayen', code: 'SJ'},
    {name: 'Swaziland', code: 'SZ'},
    {name: 'Sweden', code: 'SE'},
    {name: 'Switzerland', code: 'CH'},
    {name: 'Syrian Arab Republic', code: 'SY'},
    {name: 'Taiwan, Province of China', code: 'TW'},
    {name: 'Tajikistan', code: 'TJ'},
    {name: 'Tanzania, United Republic of', code: 'TZ'},
    {name: 'Thailand', code: 'TH'},
    {name: 'Timor-Leste', code: 'TL'},
    {name: 'Togo', code: 'TG'},
    {name: 'Tokelau', code: 'TK'},
    {name: 'Tonga', code: 'TO'},
    {name: 'Trinidad and Tobago', code: 'TT'},
    {name: 'Tunisia', code: 'TN'},
    {name: 'Turkey', code: 'TR'},
    {name: 'Turkmenistan', code: 'TM'},
    {name: 'Turks and Caicos Islands', code: 'TC'},
    {name: 'Tuvalu', code: 'TV'},
    {name: 'Uganda', code: 'UG'},
    {name: 'Ukraine', code: 'UA'},
    {name: 'United Arab Emirates', code: 'AE'},
    {name: 'United Kingdom', code: 'GB'},
    {name: 'United States', code: 'US'},
    {name: 'United States Minor Outlying Islands', code: 'UM'},
    {name: 'Uruguay', code: 'UY'},
    {name: 'Uzbekistan', code: 'UZ'},
    {name: 'Vanuatu', code: 'VU'},
    {name: 'Venezuela', code: 'VE'},
    {name: 'Viet Nam', code: 'VN'},
    {name: 'Virgin Islands, British', code: 'VG'},
    {name: 'Virgin Islands, U.S.', code: 'VI'},
    {name: 'Wallis and Futuna', code: 'WF'},
    {name: 'Western Sahara', code: 'EH'},
    {name: 'Yemen', code: 'YE'},
    {name: 'Zambia', code: 'ZM'},
    {name: 'Zimbabwe', code: 'ZW'}
];

function getNewClubs() {

    var newClubs = [];

    for (var index in _clubs) {

        if(index > 1) break;

        var club = _clubs[index];
        club.id = index;
        club.code = undefined;
        club.key = undefined;
        newClubs.push(club);
    }

    return newClubs;
}

var _clubs = [
    {
        "key": "milan",
        "name": "Milan",
        "code": "MIL"
    },
    {
        "key": "inter",
        "name": "Inter",
        "code": "INT"
    },
    {
        "key": "lazio",
        "name": "Lazio",
        "code": "LAZ"
    },
    {
        "key": "roma",
        "name": "Roma",
        "code": "ROM"
    },
    {
        "key": "genoa",
        "name": "Genoa",
        "code": "GEN"
    },
    {
        "key": "sampdoria",
        "name": "Sampdoria",
        "code": "SAM"
    },
    {
        "key": "juventus",
        "name": "Juventus",
        "code": "JUV"
    },
    {
        "key": "torino",
        "name": "Torino",
        "code": "TOR"
    },
    {
        "key": "napoli",
        "name": "Napoli",
        "code": "NAP"
    },
    {
        "key": "atalanta",
        "name": "Atalanta",
        "code": "ATA"
    },
    {
        "key": "chievoverona",
        "name": "Chievo",
        "code": "CHI"
    },
    {
        "key": "fiorentina",
        "name": "Fiorentina",
        "code": "FIO"
    },
    {
        "key": "udinese",
        "name": "Udinese",
        "code": "UDI"
    },
    {
        "key": "sassuolo",
        "name": "Sassuolo",
        "code": "SAS"
    },
    {
        "key": "bologna",
        "name": "Bologna",
        "code": "BOL"
    },
    {
        "key": "cagliari",
        "name": "Cagliari",
        "code": "CAG"
    },
    {
        "key": "crotone",
        "name": "Crotone",
        "code": "CRO"
    },
    {
        "key": "hellasverona",
        "name": "Verona",
        "code": "HEL"
    },
    {
        "key": "spal",
        "name": "SPAL",
        "code": null
    },
    {
        "key": "benevento",
        "name": "Benevento",
        "code": null
    }
];

var fullRemoteData =
    [
        {
            "id": "25",
            "name": "AdjaraBet",
            "items": [
                {
                    "id": "94",
                    "bookie_id": 25,
                    "name": "Georgia_AdjaraBet",
                    "category_info_id": null,
                    "items": [
                        {
                            "id": "95",
                            "bookie_id": 25,
                            "name": "National league_AdjaraBet",
                            "category_info_id": 94
                        },
                        {
                            "id": "96",
                            "bookie_id": 25,
                            "name": "East league_AdjaraBet",
                            "category_info_id": 94
                        },
                        {
                            "id": "97",
                            "bookie_id": 25,
                            "name": "West league_AdjaraBet",
                            "category_info_id": 94
                        }
                    ]
                },
                {
                    "id": "98",
                    "bookie_id": 25,
                    "name": "Italy_AdjaraBet",
                    "category_info_id": null,
                    "items": [
                        {
                            "id": "99",
                            "bookie_id": 25,
                            "name": "Serie A_AdjaraBet",
                            "category_info_id": 98
                        },
                        {
                            "id": "100",
                            "bookie_id": 25,
                            "name": "Coppa Italia_AdjaraBet",
                            "category_info_id": 98
                        }
                    ]
                }
            ]
        },
        {
            "id": "26",
            "name": "BetLive",
            "items": [
                {
                    "id": "106",
                    "bookie_id": 26,
                    "name": "Georgia_BetLive",
                    "category_info_id": null,
                    "items": [
                        {
                            "id": "107",
                            "bookie_id": 26,
                            "name": "National league_BetLive",
                            "category_info_id": 106
                        },
                        {
                            "id": "108",
                            "bookie_id": 26,
                            "name": "East league_BetLive",
                            "category_info_id": 106
                        },
                        {
                            "id": "109",
                            "bookie_id": 26,
                            "name": "West league_BetLive",
                            "category_info_id": 106
                        }
                    ]
                },
                {
                    "id": "110",
                    "bookie_id": 26,
                    "name": "Italy_BetLive",
                    "category_info_id": null,
                    "items": [
                        {
                            "id": "111",
                            "bookie_id": 26,
                            "name": "Serie A_BetLive",
                            "category_info_id": 110
                        },
                        {
                            "id": "112",
                            "bookie_id": 26,
                            "name": "Coppa Italia_BetLive",
                            "category_info_id": 110
                        }
                    ]
                }
            ]
        }];

var fullRemoteData_without_reds =
    [
        {
            "id": "25",
            "name": "AdjaraBet",
            "items": [
                {
                    "id": "94",
                    "name": "Georgia_AdjaraBet",
                    "items": [
                        {
                            "id": "95",
                            "name": "National league_AdjaraBet"
                        },
                        {
                            "id": "96",
                            "name": "East league_AdjaraBet"
                        }
                    ]
                },
                {
                    "id": "98",
                    "name": "Italy_AdjaraBet",
                    "items": [
                        {
                            "id": "99",
                            "name": "Serie A_AdjaraBet"
                        },
                        {
                            "id": "100",
                            "name": "Coppa Italia_AdjaraBet"
                        }
                    ]
                }
            ]
        },
        {
            "id": "26",
            "name": "BetLive",
            "items": [
                {
                    "id": "106",
                    "name": "Georgia_BetLive",
                    "items": [
                        {
                            "id": "107",
                            "name": "National league_BetLive"
                        },
                        {
                            "id": "108",
                            "name": "East league_BetLive"
                        }
                    ]
                },
                {
                    "id": "110",
                    "name": "Italy_BetLive",
                    "items": [
                        {
                            "id": "111",
                            "name": "Serie A_BetLive"
                        },
                        {
                            "id": "112",
                            "name": "Coppa Italia_BetLive"
                        }
                    ]
                }
            ]
        }];

var fullRemoteData_name_id_reorder = [
    {
        "id": "25",
        "name": "AdjaraBet",
        "items": [
            {
                "name": "Georgia_AdjaraBet",
                "id": "94",
                "items": [
                    {
                        "name": "National league_AdjaraBet",
                        "id": "95"
                    },
                    {
                        "name": "East league_AdjaraBet",
                        "id": "96"
                    }
                ]
            },
            {
                "name": "Italy_AdjaraBet",
                "id": "98",
                "items": [
                    {
                        "name": "Serie A_AdjaraBet",
                        "id": "99"
                    },
                    {
                        "name": "Coppa Italia_AdjaraBet",
                        "id": "100"
                    }
                ]
            }
        ]
    },
    {
        "id": "26",
        "name": "BetLive",
        "items": [
            {
                "name": "Georgia_BetLive",
                "id": "106",
                "items": [
                    {
                        "name": "National league_BetLive",
                        "id": "107"
                    },
                    {
                        "name": "East league_BetLive",
                        "id": "108"
                    }
                ]
            },
            {
                "name": "Italy_BetLive",
                "id": "110",
                "items": [
                    {
                        "name": "Serie A_BetLive",
                        "id": "111"
                    },
                    {
                        "name": "Coppa Italia_BetLive",
                        "id": "112"
                    }
                ]
            }
        ]
    }];

var fullRemoteData_interanl_ids = [
    {
        "id": "25",
        "name": "AdjaraBet",
        "items": [
            {
                "name": "Georgia_AdjaraBet",
                "id": "10090",
                "items": [
                    {
                        "name": "National league_AdjaraBet",
                        "id": "0"
                    },
                    {
                        "name": "East league_AdjaraBet",
                        "id": "1"
                    }
                ]
            },
            {
                "name": "Italy_AdjaraBet",
                "id": "10091",
                "items": [
                    {
                        "name": "Serie A_AdjaraBet",
                        "id": "0"
                    },
                    {
                        "name": "Coppa Italia_AdjaraBet",
                        "id": "1"
                    }
                ]
            }
        ]
    },
    {
        "id": "26",
        "name": "BetLive",
        "items": [
            {
                "name": "Georgia_BetLive",
                "id": "10090",
                "items": [
                    {
                        "name": "National league_BetLive",
                        "id": "0"
                    },
                    {
                        "name": "East league_BetLive",
                        "id": "1"
                    }
                ]
            },
            {
                "name": "Italy_BetLive",
                "id": "10091",
                "items": [
                    {
                        "name": "Serie A_BetLive",
                        "id": "0"
                    },
                    {
                        "name": "Coppa Italia_BetLive",
                        "id": "1"
                    }
                ]
            }
        ]
    }]
;

var fullLocalData = [
    {
        "id": "05620",
        "name": "AdjaraBet",
        "items": [
            {
                "name": "Afghanistan",
                "code": "AF",
                "id": "10090",
                "items": [
                    {
                        "key": "milan",
                        "name": "Milan",
                        "code": "MIL",
                        "id": "0"
                    },
                    {
                        "key": "inter",
                        "name": "Inter",
                        "code": "INT",
                        "id": "1"
                    },
                    {
                        "key": "lazio",
                        "name": "Lazio",
                        "code": "LAZ",
                        "id": "2"
                    }
                ]
            },
            {
                "name": "Åland Islands",
                "code": "AX",
                "id": "10091",
                "items": [
                    {
                        "key": "milan",
                        "name": "Milan",
                        "code": "MIL",
                        "id": "0"
                    },
                    {
                        "key": "inter",
                        "name": "Inter",
                        "code": "INT",
                        "id": "1"
                    },
                    {
                        "key": "lazio",
                        "name": "Lazio",
                        "code": "LAZ",
                        "id": "2"
                    },
                    {
                        "key": "roma",
                        "name": "Roma",
                        "code": "ROM",
                        "id": "3"
                    },
                    {
                        "key": "genoa",
                        "name": "Genoa",
                        "code": "GEN",
                        "id": "4"
                    },
                    {
                        "key": "sampdoria",
                        "name": "Sampdoria",
                        "code": "SAM",
                        "id": "5"
                    }
                ]
            }
        ]
    },
    {
        "id": "45620",
        "name": "BetLive",
        "items": [
            {
                "name": "Afghanistan",
                "code": "AF",
                "id": "10090",
                "items": [
                    {
                        "key": "milan",
                        "name": "Milan",
                        "code": "MIL",
                        "id": "0"
                    },
                    {
                        "key": "inter",
                        "name": "Inter",
                        "code": "INT",
                        "id": "1"
                    },
                    {
                        "key": "lazio",
                        "name": "Lazio",
                        "code": "LAZ",
                        "id": "2"
                    },
                    {
                        "key": "roma",
                        "name": "Roma",
                        "code": "ROM",
                        "id": "3"
                    },
                    {
                        "key": "genoa",
                        "name": "Genoa",
                        "code": "GEN",
                        "id": "4"
                    },
                    {
                        "key": "sampdoria",
                        "name": "Sampdoria",
                        "code": "SAM",
                        "id": "5"
                    }
                ]
            },
            {
                "name": "Åland Islands",
                "code": "AX",
                "id": "10091",
                "items": [
                    {
                        "key": "milan",
                        "name": "Milan",
                        "code": "MIL",
                        "id": "0"
                    },
                    {
                        "key": "inter",
                        "name": "Inter",
                        "code": "INT",
                        "id": "1"
                    },
                    {
                        "key": "lazio",
                        "name": "Lazio",
                        "code": "LAZ",
                        "id": "2"
                    },
                    {
                        "key": "roma",
                        "name": "Roma",
                        "code": "ROM",
                        "id": "3"
                    },
                    {
                        "key": "genoa",
                        "name": "Genoa",
                        "code": "GEN",
                        "id": "4"
                    },
                    {
                        "key": "sampdoria",
                        "name": "Sampdoria",
                        "code": "SAM",
                        "id": "5"
                    }
                ]
            }
        ]
    }
];

var fullLocalData_without_blues = [
    {
        "id": "05620",
        "name": "AdjaraBet",
        "items": [
            {
                "name": "Afghanistan",
                "id": "10090",
                "items": [
                    {
                        "name": "Milan",
                        "id": "0"
                    },
                    {
                        "name": "Inter",
                        "id": "1"
                    }
                ]
            },
            {
                "name": "Åland Islands",
                "id": "10091",
                "items": [
                    {
                        "name": "Milan",
                        "id": "0"
                    },
                    {
                        "name": "Inter",
                        "id": "1"
                    }
                ]
            }
        ]
    },
    {
        "id": "45620",
        "name": "BetLive",
        "items": [
            {
                "name": "Afghanistan",
                "id": "10090",
                "items": [
                    {
                        "name": "Milan",
                        "id": "0"
                    },
                    {
                        "name": "Inter",
                        "id": "1"
                    }
                ]
            },
            {
                "name": "Åland Islands",
                "id": "10091",
                "items": [
                    {
                        "name": "Milan",
                        "id": "0"
                    },
                    {
                        "name": "Inter",
                        "id": "1"
                    }
                ]
            }
        ]
    }
];

var generated_data = [
    {
        "id":"05620",
        "name":"AdjaraBet",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    },
    {
        "id":"15620",
        "name":"EuropeBet",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    },
    {
        "id":"25620",
        "name":"LiderBet",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    },
    {
        "id":"35620",
        "name":"CrocoBet",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    },
    {
        "id":"45620",
        "name":"BetLive",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    },
    {
        "id":"55620",
        "name":"CrystalBet",
        "items":[
            {
                "name":"Afghanistan",
                "id":"10090",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            },
            {
                "name":"Åland Islands",
                "id":"10091",
                "items":[
                    {
                        "name":"Milan",
                        "id":"0"
                    },
                    {
                        "name":"Inter",
                        "id":"1"
                    }]
            }]
    }]
;