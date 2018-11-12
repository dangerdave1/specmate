import { CreateNodeToolBase } from '../create-node-tool-base';
import { ElementFactoryBase } from '../../../../../../../../factory/element-factory-base';
import { BDDModel } from '../../../../../../../../model/BDDModel';
import { BDDTerminalNodeFactory } from '../../../../../../../../factory/bdd-terminal-node-factory';
import { BDDTerminalNode } from '../../../../../../../../model/BDDTerminalNode';

export class BDDTerminal1NodeTool extends CreateNodeToolBase<BDDTerminalNode> {

    protected modelType: { className: string; } = BDDModel;

    public icon = 'plus';
    public name = 'tools.addBddTerminal1Node';

    protected getElementFactory(coords: { x: number; y: number; }): ElementFactoryBase<BDDTerminalNode> {
        return new BDDTerminalNodeFactory(true, coords, this.dataService);
    }
}
