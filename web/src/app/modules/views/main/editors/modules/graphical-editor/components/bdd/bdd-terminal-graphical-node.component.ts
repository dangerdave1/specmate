import { Component, Input } from '@angular/core';
import { DraggableElementBase } from '../../elements/draggable-element-base';
import { Config } from '../../../../../../../../config/config';
import { SpecmateDataService } from '../../../../../../../data/modules/data-service/services/specmate-data.service';
import { SelectedElementService } from '../../../../../../side/modules/selected-element/services/selected-element.service';
import { ValidationService } from '../../../../../../../forms/modules/validation/services/validation.service';
import { MultiselectionService } from '../../../tool-pallette/services/multiselection.service';
import { BDDTerminalNode } from '../../../../../../../../model/BDDTerminalNode';

@Component({
    moduleId: module.id.toString(),
    selector: '[bdd-terminal-graphical-node]',
    templateUrl: 'bdd-terminal-graphical-node.component.svg',
    styleUrls: ['bdd-terminal-graphical-node.component.css']
})

export class BDDTerminalGraphicalNode extends DraggableElementBase<BDDTerminalNode> {
    public nodeType: { className: string; } = BDDTerminalNode;

    public get dimensions(): {width: number, height: number} {
        return {
            width: Config.CEG_NODE_HEIGHT,
            height: Config.CEG_NODE_HEIGHT
        };
    }

    @Input()
    node: BDDTerminalNode;

    public get element(): BDDTerminalNode {
        return this.node;
    }

    public get text(): string {
        return this.node.value ? '1' : '0';
    }

    constructor(
        protected dataService: SpecmateDataService,
        selectedElementService: SelectedElementService,
        validationService: ValidationService,
        multiselectionService: MultiselectionService) {
        super(selectedElementService, validationService, multiselectionService);
    }
}
