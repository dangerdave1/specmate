import { Component } from '@angular/core';
import { GraphicalConnectionBase } from '../../elements/graphical-connection-base';
import { SelectedElementService } from '../../../../../../side/modules/selected-element/services/selected-element.service';
import { ValidationService } from '../../../../../../../forms/modules/validation/services/validation.service';
import { MultiselectionService } from '../../../tool-pallette/services/multiselection.service';
import { BDDConnection } from '../../../../../../../../model/BDDConnection';

@Component({
    moduleId: module.id.toString(),
    selector: '[bdd-graphical-connection]',
    templateUrl: 'bdd-graphical-connection.component.svg',
    styleUrls: ['bdd-graphical-connection.component.css']
})
export class BDDGraphicalConnection extends GraphicalConnectionBase<BDDConnection> {
    public nodeType: { className: string; } = BDDConnection;

    constructor(selectedElementService: SelectedElementService,
                validationService: ValidationService,
                selectionRectService: MultiselectionService) {
        super(selectedElementService, validationService, selectionRectService);
    }

    public get isNegated(): boolean {
        if (this.connection.negate === undefined || this.connection.negate.toString() === '') {
            this.connection.negate = false;
        }
        return this.connection.negate;
    }
}
