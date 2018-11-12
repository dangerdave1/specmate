import { ModelFactoryBase } from './model-factory-base';
import { IContainer } from '../model/IContainer';
import { Config } from '../config/config';
import { ElementFactoryBase } from './element-factory-base';
import { BDDModel } from '../model/BDDModel';

export class BDDModelFactory extends ModelFactoryBase {
    protected get simpleModel(): IContainer {
        return new BDDModel();
    }

    protected get name(): string {
        return Config.BDD_NEW_MODEL_NAME + ' ' + ElementFactoryBase.getDateStr();
    }

    protected get description(): string {
        return Config.BDD_NEW_MODEL_DESCRIPTION;
    }
}
