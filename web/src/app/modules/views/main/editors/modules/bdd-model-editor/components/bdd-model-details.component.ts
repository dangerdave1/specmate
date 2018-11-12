import { Component, ViewChild } from '@angular/core';
import { SpecmateViewBase } from '../../../base/specmate-view-base';
import { IContainer } from '../../../../../../../model/IContainer';
import { GraphicalEditor } from '../../graphical-editor/components/graphical-editor.component';
import { SpecmateDataService } from '../../../../../../data/modules/data-service/services/specmate-data.service';
import { NavigatorService } from '../../../../../../navigation/modules/navigator/services/navigator.service';
import { ActivatedRoute } from '@angular/router';
import { ConfirmationModal } from '../../../../../../notification/modules/modals/services/confirmation-modal.service';
import { TranslateService } from '@ngx-translate/core';
import { BDDModel } from '../../../../../../../model/BDDModel';

@Component({
    moduleId: module.id.toString(),
    selector: 'bdd-model-details-editor',
    templateUrl: 'bdd-model-details.component.html',
    styleUrls: ['bdd-model-details.component.css']
})

export class BDDModelDetails extends SpecmateViewBase {

    private model: BDDModel;
    private contents: IContainer[];

    @ViewChild(GraphicalEditor)
    private editor: GraphicalEditor;

    constructor(
        dataService: SpecmateDataService,
        navigator: NavigatorService,
        route: ActivatedRoute,
        modal: ConfirmationModal,
        translate: TranslateService) {

        super(dataService, navigator, route, modal, translate);
    }

    protected onElementResolved(element: IContainer): void {
        this.model = <BDDModel>element;
        this.dataService.readContents(this.model.url).then((contents: IContainer[]) => this.contents = contents);
    }

    protected get isValid(): boolean {
        if (!this.editor) {
            return true;
        }
        return this.editor.isValid;
    }
}
