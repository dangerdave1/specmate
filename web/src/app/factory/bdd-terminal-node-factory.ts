import { PositionableElementFactoryBase } from './positionable-element-factory-base';
import { IContainer } from '../model/IContainer';
import { Id } from '../util/id';
import { Url } from '../util/url';
import { Config } from '../config/config';
import { ElementFactoryBase } from './element-factory-base';
import { SpecmateDataService } from '../modules/data/modules/data-service/services/specmate-data.service';
import { BDDTerminalNode } from '../model/BDDTerminalNode';

export class BDDTerminalNodeFactory extends PositionableElementFactoryBase<BDDTerminalNode> {

    constructor(private value: boolean, coords: {x: number, y: number}, dataService: SpecmateDataService) {
        super(coords, dataService);
    }

    public create(parent: IContainer, commit: boolean, compoundId?: string, name?: string): Promise<BDDTerminalNode> {

        compoundId = compoundId || Id.uuid;

        let id: string = Id.uuid;
        let url: string = Url.build([parent.url, id]);
        let node: BDDTerminalNode = new BDDTerminalNode();
        node.name = name || Config.BDD_NEW_NODE_NAME + ' ' + ElementFactoryBase.getDateStr();
        node.description = Config.BDD_NEW_NODE_DESCRIPTION;
        node.id = id;
        node.url = url;
        node.value = this.value;
        node.x = this.coords.x;
        node.y = this.coords.y;
        node.tracesFrom = [];
        node.tracesTo = [];

        return this.dataService.createElement(node, true, compoundId).then(() => node);
    }
}
