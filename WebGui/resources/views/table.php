<table class="table chainer fixed" >
    <thead>
    <tr>
        <th ng-repeat="bookie in bookieData">{{bookie.name}}</th>
        <!--<th>
            <div class="row">
                <div class="col-md-12">
                    Edit
                </div>
            </div>
        </th>
        <th>
            <div class="row">
                <div class="col-md-12">
                    Remove
                </div>
            </div>
        </th>-->
    </tr>
    </thead>
    <tbody>
    <tr ng-click="startChainEdit(chain)" ng-repeat="chain in chains">
        <td ng-if="!chain.edit"
            ng-class="!chain[bookie.id] ? 'danger' : 'success'" ng-repeat="bookie in bookieData">
            {{getItemNameByBookieIdAndItemId(bookie.id, chain[bookie.id.toString()])}}
        </td>
        <td ng-if="chain.edit" ng-class="!chain[bookie.id] ? 'danger' : 'success'" ng-repeat="bookie in bookieData">
            <div class="form-group" ng-class="{ 'has-error' : false }">
                <select ng-required="false"
                        ng-options="item.id as item.name for item in getCategoryByIdForBookieAndChain(bookie.id, chain)"
                        class="form-control"
                        type="number"
                        name="tyis_tipi_id"
                        data-placeholder="Category"
                        chosen
                        ng-disabled="false"
                        ng-model="chain[bookie.id]">
                    <option value="">Category</option>
                </select>

                <div class="help-block" ng-messages="maketi3.tyis_tipi_id.$error"
                     ng-if="maketi3.tyis_tipi_id.$invalid">
                    <!-- Messages should go here. -->
                </div>
            </div>
        </td>
        <!--<td>
            <div class="row">
                <div class="col-md-12">
                    <button type="button" class="btn btn-warning"
                            ng-click="startChainEdit(chain)">
                        ED
                    </button>
                </div>
            </div>
        </td>
        <td>
            <div class="row">
                <div class="col-md-12">
                    <button type="button" class="btn btn-danger"
                            ng-click="chains.splice($index, 1)">RM
                    </button>
                </div>
            </div>
        </td>-->
    </tr>
    <!--New chain initiator row-->
    <tr>
        <td ng-repeat="bookie in bookieData">
            <div class="form-group"
                 ng-class="{ 'has-error' : !newChainInitiatorRowModel[bookie.id] }">
                <select ng-required="false"
                        ng-options="item.id as item.name for item in  chainInititatorRowData[bookie.id]"
                        class="form-control"
                        type="number"
                        name="tyis_tipi_id"
                        data-placeholder="Category"
                        chosen
                        ng-disabled="false"
                        ng-model="newChainInitiatorRowModel[bookie.id]">
                    <option value="">Category</option>
                </select>

                <div class="help-block" ng-messages="maketi3.tyis_tipi_id.$error"
                     ng-if="maketi3.tyis_tipi_id.$invalid">
                    <!-- Messages should go here. -->
                </div>
            </div>
        </td>
        <!--<td>
            <div class="row">
                <div class="col-md-12">
                    s
                </div>
            </div>
        </td>
        <td>
            <div class="row">
                <div class="col-md-12">
                    s
                </div>
            </div>
        </td>-->
    </tr>
    </tbody>
</table>