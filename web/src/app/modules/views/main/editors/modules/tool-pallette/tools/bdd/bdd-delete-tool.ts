import { DeleteToolBase } from '../delete-tool-base';
import { BDDModel } from '../../../../../../../../model/BDDModel';

export class BDDDeleteTool extends DeleteToolBase {
    protected modelType: { className: string; } = BDDModel;
}
