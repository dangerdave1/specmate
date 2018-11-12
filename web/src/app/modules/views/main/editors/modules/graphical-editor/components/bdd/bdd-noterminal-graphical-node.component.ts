import { Component, Input } from '@angular/core';
import { DraggableElementBase } from '../../elements/draggable-element-base';
import { Config } from '../../../../../../../../config/config';
import { SpecmateDataService } from '../../../../../../../data/modules/data-service/services/specmate-data.service';
import { SelectedElementService } from '../../../../../../side/modules/selected-element/services/selected-element.service';
import { ValidationService } from '../../../../../../../forms/modules/validation/services/validation.service';
import { MultiselectionService } from '../../../tool-pallette/services/multiselection.service';
import { BDDNoTerminalNode } from '../../../../../../../../model/BDDNoTerminalNode';

@Component({
    moduleId: module.id.toString(),
    selector: '[bdd-noterminal-graphical-node]',
    templateUrl: 'bdd-noterminal-graphical-node.component.svg',
    styleUrls: ['bdd-noterminal-graphical-node.component.css']
})

export class BDDNoTerminalGraphicalNode extends DraggableElementBase<BDDNoTerminalNode> {
    public nodeType: { className: string; } = BDDNoTerminalNode;

    public get dimensions(): {width: number, height: number} {
        return {
            width: Config.CEG_NODE_WIDTH,
            height: Config.CEG_NODE_HEIGHT
        };
    }

    @Input()
    node: BDDNoTerminalNode;

    public get element(): BDDNoTerminalNode {
        return this.node;
    }

    constructor(
        protected dataService: SpecmateDataService,
        selectedElementService: SelectedElementService,
        validationService: ValidationService,
        multiselectionService: MultiselectionService) {
        super(selectedElementService, validationService, multiselectionService);
    }
}
