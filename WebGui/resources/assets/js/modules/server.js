const server = angular.module('server', []);

server.factory('Server', ['$http', '$q', '$timeout', function($http, $q, $timeout) {

    console.log("Creating new Server instance.");

    function typical (method, url, data, prepare) {
        var deferred = $q.defer();


        /*$timeout(function () {
            deferred.reject([]);
        }, 0);*/

        try {
            $http[method](url, data ? data : null).then(ok, fail);
        } catch (e) {

            $timeout(function () {
                deferred.reject(data);
            }, 0);

            return deferred.promise;
        }

        function ok(data){
            var data = data.data;

            if(data && data.status == "OK"){

                if(prepare && data.data){
                    prepare(data);
                }

                deferred.resolve(data.data || "");
            } else {
                deferred.reject(data);
            }
        }

        function fail(res){
            deferred.reject(res);
        }

        return deferred.promise;
    }

    var Server = {

        getCategoryInfos : function () {
            return typical('get', '/category-infos', null, function (responseData) {
                var data = responseData.data;

                console.log("getCategoryInfos data: ", data);
                
                function convert(items) {

                    return _.map(items, function (item) {

                        //console.log("item: ", item);

                        if(item.category_info_id) {
                            delete item.items;
                        }

                        if(item.items) {
                            convert(item.items);
                        }

                        return item;
                    });
                }

                console.log("Converting data : ", data);
                var converted = convert(data);

                console.log("Converted data : ", converted);

                return converted;
            });
        },

        getSbDropDownData : function () {
            return typical('get', '/api/sb-dropdown-data');
        },

        getSbDropDownExportData : function () {
            return typical('get', '/api/sb-dropdown-export-data');
        },

        // PASSPORT

        getPassportData : function(id) {
            return typical('get', '/api/passports/'+ id +'/edit', null, function(responseData){

                var tmp;

                for (var key in responseData.data){

                    //console.log("Parsing: ", key, responseData.data[key]);

                    if(typeof responseData.data[key] == "string"){
                        //FIXME: Don't really trust this for some browsers
                        try{
                            tmp = JSON.parse(responseData.data[key]);
                        } catch (e){
                            //console.log("Error while parsing: ", key, responseData.data[key], e.message);
                            continue;
                        }
                        //if(key != "regioni" && key != "satyeo_ubani" && key != "satyeo_ubani"){
                        responseData.data[key] = tmp;
                        //} else {
                        //responseData.data[key] = undefined;
                        // }
                    }
                }

                //console.log('prepare run: ', responseData.data);
            });
        },
        createPassport : function(data, as_user){
            var url = '/api/passports/create' + (as_user ? '/' + as_user : '');

            return typical('post', url, data);
        },
        updatePassport : function(id, data){
            return typical('post', '/api/passports/'+ id +'/update', data);
        },
        deletePassport : function(id){
            return typical('get', '/api/passports/'+ id +'/delete');
        },
        getLatest : function(count){
            return typical('get', '/api/passports/latest/'+ count);
        },
        searchDocs : function(key){
            return typical('get', '/api/passports/search/'+ key);
        },
        getLatestClosed : function(count){
            return typical('get', '/api/passports/latest/'+ count +'/closed');
        },
        exportPassportDoc : function(id){

            location.href = '/api/passports/'+ id +'/doc';

        },

        /**
         * Create single passport dublicate optionally for specific user
         * @param id
         * @param as_user
         * @returns promise
         */
        createPassportDublicate : function(id, as_user){

            var defer = $q.defer();

            $q.all([
                /**
                 * Load data from target forest cut.
                 */
                this.getPassportData(id),
                this.getForma6Data(id),
                this.getTrees(id)
            ]).then(function(dataList){
                /**
                 * Create new passport from loaded data
                 * First passport and then forma6, forma1, just like created manually.
                 */
                Server.createPassport(dataList[0], as_user).then(function(res){
                    $q.all([
                        Server.updateForma6(res.created_id, dataList[1]),
                        Server.updateForma1(res.created_id, dataList[2])
                    ]).then(function(){
                        defer.resolve(res.created_id);
                    }).catch(function(){
                        defer.reject(false);
                    });
                });
            });

            return defer.promise;
        },

        /**
         * Create passport dublicates for specific user.
         * @param items
         * @param user_id
         * @returns promise
         */
        dublicatePassportsForUser : function(items, user_id){

            if(!items || !(items.length > 0) || !user_id){
                return;
            }

            return $q.all(_.map(items, function(value){

                return Server.createPassportDublicate(value, user_id);

            }));
        },
        /**
         *
         * Change owner/user of passport
         * @param id
         * @param new_user_id
         * @returns promise
         */
        updatePassportUser : function(id, new_user_id){
            return typical('get', '/api/passports/'+ id +'/update-user/' + new_user_id);
        },

        deletePassportMany : function(ids){

            var promises = [];

            _.each(ids, function(id){
                promises.push(Server.deletePassport(id));
            });

            return $q.all(promises);
        },

        //FORMA 6

        getForma6Data : function(id){
            return typical('get', '/api/forma6/'+ id +'/edit');
        },
        updateForma6 : function(id, data){
            return typical('post', '/api/forma6/'+ id +'/update', data);
        },
        deleteForma6 : function(id){
            return typical('get', '/api/forma6/'+ id +'/delete');
        },

        // SERVER ASSISTANCE IN CALCULATIONS

        getTreesTanrigi : function(){
            return typical('get', '/api/calcs/tanrigi');
        },

        //FORMA 1

        getTrees : function(id){
            return typical('get', '/api/forma1/'+ id + '/edit');
        },
        updateForma1 : function(id, data){
            return typical('put', '/api/forma1/'+ id, data);
        },
        deleteTrees : function(id){
            return typical('delete', '/api/trees/'+ id);
        },

        //Users

        getUserList : function(){
            return typical('get', '/api/users');
        },

        // Admin
        getXisSaxeobas : function(){
            return typical('get', '/api/xis-saxeobas');
        },

        getTanrigisCxriliSaxeobas : function(){
            return typical('get', '/api/tanrigis-cxrili-saxeobas');
        },

        syncChosenCalcTables : function(data){
            return typical('post', '/api/sync-calc-tables', data);
        },

        syncIsRed : function(data){
            return typical('post', '/api/sync-is-red', data);
        },

        getAssociatedList : function(){
            return typical('get', '/api/get-assoc-table-list');
        },

        getRedList : function(){
            return typical('get', '/api/get-red-list');
        },

        createNewSaxeoba : function(data){
            return typical('post', '/api/create-new-saxeoba', data);
        },

        deleteSaxeoba : function(id){
            return typical('get', '/api/saxeoba/'+ id +'/delete');
        },

        validatePassport : function(id){
            return typical('get', '/api/validate-passport/' + id );
        },

        /**
         * Sataqsacio barati
         */

        saveSb : function (data) {
            return typical('post', '/api/sb/create', data);
        },

        getSbKvartalis : function(regioni_id, satyeo_ubani_id, satyeo_id){

            return typical('get', '/api/sb/kvartalis/' + regioni_id + '/' + satyeo_ubani_id + '/' + satyeo_id);
        },

        getSbLiteris : function(regioni_id, satyeo_ubani_id, satyeo_id, kvartali){

            return typical('get', '/api/sb/literis/' + regioni_id + '/' + satyeo_ubani_id + '/' + satyeo_id + '/' + kvartali);
        },

        getSbData : function(regioni_id, satyeo_ubani_id, satyeo_id, kvartali, literi){
            return typical('get', '/api/sb/edit/' + regioni_id + '/' + satyeo_ubani_id + '/' + satyeo_id + '/' + kvartali + '/' + literi);
        },

        deleteSb : function(id){
            return typical('delete', '/api/sb/delete/' + id);
        },

        sbSyncChosenMaragiTables : function(data){
            return typical('post', '/api/sb/sync-maragi-tables', data);
        },

        sbSyncBonitetiTables : function(data){
            return typical('post', '/api/sb/sync-boniteti-tables', data);
        },

        sbSyncXnovanebaTables : function(data){
            return typical('post', '/api/sb/sync-xnovaneba-tables', data);
        },

        sbGetDrops : function(){
            return typical('get', '/api/sb/get-admin-drop-data');
        }
    };

    return Server;
}]);

export {server};