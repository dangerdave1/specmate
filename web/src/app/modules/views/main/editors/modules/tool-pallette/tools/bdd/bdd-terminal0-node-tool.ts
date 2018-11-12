import { CreateNodeToolBase } from '../create-node-tool-base';
import { ElementFactoryBase } from '../../../../../../../../factory/element-factory-base';
import { BDDModel } from '../../../../../../../../model/BDDModel';
import { BDDTerminalNodeFactory } from '../../../../../../../../factory/bdd-terminal-node-factory';
import { BDDTerminalNode } from '../../../../../../../../model/BDDTerminalNode';

export class BDDTerminal0NodeTool extends CreateNodeToolBase<BDDTerminalNode> {

    protected modelType: { className: string; } = BDDModel;

    public icon = 'plus';
    public name = 'tools.addBddTerminal0Node';

    protected getElementFactory(coords: { x: number; y: number; }): ElementFactoryBase<BDDTerminalNode> {
        return new BDDTerminalNodeFactory(false, coords, this.dataService);
    }
}
