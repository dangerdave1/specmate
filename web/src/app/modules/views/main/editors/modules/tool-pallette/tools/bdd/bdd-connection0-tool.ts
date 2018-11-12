import { ConnectionToolBase } from '../connection-tool-base';
import { IModelNode } from '../../../../../../../../model/IModelNode';
import { IModelConnection } from '../../../../../../../../model/IModelConnection';
import { ElementFactoryBase } from '../../../../../../../../factory/element-factory-base';
import { CEGConnectionFactory } from '../../../../../../../../factory/ceg-connection-factory';
import { BDDConnection } from '../../../../../../../../model/BDDConnection';
import { BDDModel } from '../../../../../../../../model/BDDModel';
import { BDDConnectionFactory } from '../../../../../../../../factory/bdd-connection-factory';

export class BDDConnection0Tool extends ConnectionToolBase<BDDConnection> {

    protected modelType: { className: string; } = BDDModel;

    public name = 'tools.addBdd0Connection';
    public icon = 'sitemap';

    protected getFactory(e1: IModelNode, e2: IModelNode): ElementFactoryBase<IModelConnection> {
        return new BDDConnectionFactory(false, e1, e2, this.dataService);
    }
}
