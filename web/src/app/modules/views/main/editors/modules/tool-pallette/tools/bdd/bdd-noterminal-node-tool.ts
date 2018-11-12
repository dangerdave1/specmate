import { CreateNodeToolBase } from '../create-node-tool-base';
import { ElementFactoryBase } from '../../../../../../../../factory/element-factory-base';
import { BDDNoTerminalNode } from '../../../../../../../../model/BDDNoTerminalNode';
import { BDDModel } from '../../../../../../../../model/BDDModel';
import { BDDNoTerminalNodeFactory } from '../../../../../../../../factory/bdd-noterminal-node-factory';

export class BDDNoTerminalNodeTool extends CreateNodeToolBase<BDDNoTerminalNode> {

    protected modelType: { className: string; } = BDDModel;

    public icon = 'plus';
    public name = 'tools.addBddNoTerminalNode';

    protected getElementFactory(coords: { x: number; y: number; }): ElementFactoryBase<BDDNoTerminalNode> {
        return new BDDNoTerminalNodeFactory(coords, this.dataService);
    }
}
