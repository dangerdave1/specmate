import { PositionableElementFactoryBase } from './positionable-element-factory-base';
import { IContainer } from '../model/IContainer';
import { Id } from '../util/id';
import { Url } from '../util/url';
import { Config } from '../config/config';
import { ElementFactoryBase } from './element-factory-base';
import { BDDNoTerminalNode } from '../model/BDDNoTerminalNode';

export class BDDNoTerminalNodeFactory extends PositionableElementFactoryBase<BDDNoTerminalNode> {

    public create(parent: IContainer, commit: boolean, compoundId?: string, name?: string): Promise<BDDNoTerminalNode> {

        compoundId = compoundId || Id.uuid;

        let id: string = Id.uuid;
        let url: string = Url.build([parent.url, id]);
        let node: BDDNoTerminalNode = new BDDNoTerminalNode();
        node.name = name || Config.BDD_NEW_NODE_NAME + ' ' + ElementFactoryBase.getDateStr();
        node.description = Config.BDD_NEW_NODE_DESCRIPTION;
        node.id = id;
        node.url = url;
        node.variable = Config.BDD_NODE_NEW_VARIABLE;
        node.condition = Config.BDD_NODE_NEW_CONDITION;
        node.x = this.coords.x;
        node.y = this.coords.y;
        node.tracesFrom = [];
        node.tracesTo = [];

        return this.dataService.createElement(node, true, compoundId).then(() => node);
    }
}
