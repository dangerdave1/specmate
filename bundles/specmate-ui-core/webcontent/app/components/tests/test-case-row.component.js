"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var Type_1 = require('../../util/Type');
var ParameterAssignment_1 = require('../../model/ParameterAssignment');
var TestCase_1 = require('../../model/TestCase');
var specmate_data_service_1 = require('../../services/specmate-data.service');
var core_1 = require('@angular/core');
var router_1 = require('@angular/router');
var TestCaseRow = (function () {
    function TestCaseRow(dataService, router, route) {
        this.dataService = dataService;
        this.router = router;
        this.route = route;
    }
    TestCaseRow.prototype.ngOnInit = function () {
        var _this = this;
        this.dataService.readContents(this.testCase.url).then(function (contents) {
            _this.assignments = contents.filter(function (c) { return Type_1.Type.is(c, ParameterAssignment_1.ParameterAssignment); }).map(function (c) { return c; });
            _this.assignmentMap = _this.deriveAssignmentMap(_this.assignments);
        });
    };
    /** Derives the parameter assignments matching to the display parameters in the right order */
    TestCaseRow.prototype.deriveAssignmentMap = function (assignments) {
        var assignmentMap = {};
        for (var _i = 0, _a = this.assignments; _i < _a.length; _i++) {
            var assignment = _a[_i];
            assignmentMap[assignment.parameter.url] = assignment;
        }
        return assignmentMap;
    };
    __decorate([
        core_1.Input(), 
        __metadata('design:type', TestCase_1.TestCase)
    ], TestCaseRow.prototype, "testCase", void 0);
    __decorate([
        core_1.Input(), 
        __metadata('design:type', Array)
    ], TestCaseRow.prototype, "displayParameters", void 0);
    TestCaseRow = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: '[test-case-row]',
            templateUrl: 'test-case-row.component.html'
        }), 
        __metadata('design:paramtypes', [specmate_data_service_1.SpecmateDataService, router_1.Router, router_1.ActivatedRoute])
    ], TestCaseRow);
    return TestCaseRow;
}());
exports.TestCaseRow = TestCaseRow;
//# sourceMappingURL=test-case-row.component.js.map