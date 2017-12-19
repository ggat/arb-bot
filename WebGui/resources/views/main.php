<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading"> Map category names</div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-md-12">
                            <button class="btn btn-success" type="button">
                                Completed <span class="badge">1</span>
                            </button>
                            <button class="btn btn-warning" type="button">
                                Incomplete <span class="badge">4</span>
                            </button>
                            <button class="btn btn-default" type="button">
                                Opportunities <span class="badge">238</span>
                            </button>

                            <button ng-click="storeChains()" class="btn btn-info" type="button">
                                Save
                            </button>
                        </div>
                    </div>
                    <!-- Table -->
                    <div ng-include="'table'" ></div>
                    <hr/>
                    <ui-view></ui-view>
                </div>
            </div>
        </div>
    </div>
</div>