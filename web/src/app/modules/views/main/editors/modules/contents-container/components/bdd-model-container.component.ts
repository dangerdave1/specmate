import { Component, Input } from '@angular/core';
import { ContentContainerBase } from '../base/contents-container-base';
import { IContainer } from '../../../../../../../model/IContainer';
import { ModelFactoryBase } from '../../../../../../../factory/model-factory-base';
import { Type } from '../../../../../../../util/type';
import { SpecmateDataService } from '../../../../../../data/modules/data-service/services/specmate-data.service';
import { NavigatorService } from '../../../../../../navigation/modules/navigator/services/navigator.service';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationModal } from '../../../../../../notification/modules/modals/services/confirmation-modal.service';
import { Id } from '../../../../../../../util/id';
import { BDDModel } from '../../../../../../../model/BDDModel';
import { BDDModelFactory } from '../../../../../../../factory/bdd-model-factory';

@Component({
    moduleId: module.id.toString(),
    selector: 'bdd-model-container',
    templateUrl: 'bdd-model-container.component.html',
    styleUrls: ['bdd-model-container.component.css']
})

export class BDDModelContainer extends ContentContainerBase<BDDModel> {

    constructor(dataService: SpecmateDataService, navigator: NavigatorService, translate: TranslateService, modal: ConfirmationModal) {
        super(dataService, navigator, translate, modal);
    }

    protected condition = (element: IContainer) => Type.is(element, BDDModel);

    public async createElement(name: string): Promise<BDDModel> {
        let factory: ModelFactoryBase = new BDDModelFactory(this.dataService);
        const element = await factory.create(this.parent, true, Id.uuid, name);
        return element as BDDModel;
    }
}
