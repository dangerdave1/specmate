import { Id } from '../../../util/Id';
import { ConfirmationModal } from '../../../services/notification/confirmation-modal.service';
import { Type } from '../../../util/Type';
import { GraphicalElementDetails } from './graphical-element-details.component';
import { ChangeDetectionStrategy, ViewChildren, QueryList, ViewChild, SimpleChange, Component, Input, ChangeDetectorRef } from '@angular/core';

import { Config } from '../../../config/config';

import { IContainer } from '../../../model/IContainer';
import { CEGModel } from '../../../model/CEGModel';
import { CEGNode } from '../../../model/CEGNode';
import { CEGConnection } from '../../../model/CEGConnection';

import { DeleteTool } from './tools/delete-tool';
import { ConnectionTool } from './tools/connection-tool';
import { MoveTool } from './tools/move-tool';
import { NodeTool } from './tools/node-tool';

import { SpecmateDataService } from "../../../services/data/specmate-data.service";
import { GraphicalEditorBase } from "../../core/graphical/graphical-editor-base";
import { ISpecmateModelObject } from "../../../model/ISpecmateModelObject";
import { CEGGraphicalConnection } from "./elements/ceg/ceg-graphical-connection.component";
import { ITool } from "./tools/i-tool";


@Component({
    moduleId: module.id,
    selector: 'graphical-editor',
    templateUrl: 'graphical-editor.component.html',
    styleUrls: ['graphical-editor.component.css'],
    changeDetection: ChangeDetectionStrategy.Default
})
export class GraphicalEditor extends GraphicalEditorBase {

    @ViewChildren(GraphicalElementDetails)
    private nodeDetails: QueryList<GraphicalElementDetails>;


    private _graphicalConnections: QueryList<CEGGraphicalConnection>;

    @ViewChildren(CEGGraphicalConnection)
    private set graphicalConnections(graphicalConnections: QueryList<CEGGraphicalConnection>) {
        this._graphicalConnections = graphicalConnections;
        this.changeDetectorRef.detectChanges();
    }

    private _model: CEGModel;

    public get model(): CEGModel {
        return this._model;
    }

    @Input()
    public set model(model: CEGModel) {
        this._model = model;
        this.initTools(model);
    }

    private _contents: IContainer[];
    @Input()
    public set contents(contents: IContainer[]) {
        this._contents = contents;
        if(!this.tools) {
            return;
        }
        this.activateDefaultTool();
    }

    public get contents(): IContainer[] {
        return this._contents;
    }

    private nodeType = CEGNode;
    private connectionType = CEGConnection;

    public tools: ITool<ISpecmateModelObject>[];
    private activeTool: ITool<ISpecmateModelObject>;

    constructor(private dataService: SpecmateDataService, private changeDetectorRef: ChangeDetectorRef, private modal: ConfirmationModal) {
        super();
    }

    private initTools(model: CEGModel): void {
        this.tools = [
            new MoveTool(),
            new NodeTool(model, this.dataService),
            new ConnectionTool(model, this.dataService),
            new DeleteTool(model, this.dataService)
        ];
    }

    private get cursor(): string {
        if(this.activeTool) {
            return this.activeTool.cursor;
        }
        return 'auto';
    }

    public get isValid(): boolean {
        if (!this.nodeDetails) {
            return true;
        }
        return !this.nodeDetails.some((details: GraphicalElementDetails) => !details.isValid);
    }

    private isValidElement(element: IContainer): boolean {
        let nodeDetail: GraphicalElementDetails = this.nodeDetails.find((details: GraphicalElementDetails) => details.element === element);
        if (!nodeDetail) {
            return true;
        }
        return nodeDetail.isValid;
    }

    private getGraphicalConnections(node: CEGNode): CEGGraphicalConnection[] {
        if (!this._graphicalConnections) {
            return [];
        }
        return this._graphicalConnections.filter((connection: CEGGraphicalConnection) => connection.connection.target.url === node.url);
    }

    private activate(tool: ITool<any>): void {
        if (!tool) {
            return;
        }
        if (this.activeTool) {
            this.activeTool.deactivate();
        }
        this.activeTool = tool;
        this.activeTool.activate();
    }

    private isActive(tool: ITool<IContainer>): boolean {
        return this.activeTool === tool;
    }

    private get selectedNodes(): IContainer[] {
        if (this.activeTool) {
            return this.activeTool.selectedElements;
        }
        return [];
    }

    private get selectedNode(): IContainer {
        let selectedNodes = this.selectedNodes;
        if (selectedNodes.length > 0) {
            return selectedNodes[selectedNodes.length - 1];
        }
        return undefined;
    }

    private isSelected(element: IContainer) {
        return this.selectedNodes.indexOf(element) >= 0;
    }

    private select(element: IContainer): void {
        if (this.activeTool) {
            this.activeTool.select(element).then(() => {
                if(this.activeTool.done) {
                    this.activateDefaultTool();
                }
            });
        }
    }

    private click(evt: MouseEvent): void {
        if (this.activeTool) {
            this.activeTool.click(evt).then(() => {
                if(this.activeTool.done) {
                    this.activateDefaultTool();
                }
            });
        }
    }

    public reset(): void {
        if (this.activeTool) {
            this.activeTool.deactivate();
            this.activeTool.activate();
        }
    }

    private activateDefaultTool(): void {
        if(this.contents && this.contents.length > 0) {
            this.activate(this.tools[0]);
        }
        else {
            this.activate(this.tools[1]);
        }
    }
    
    private get nodes(): CEGNode[] {
        return this.contents.filter((element: IContainer) => Type.is(element, CEGNode)) as CEGNode[];
    }

    private get connections(): CEGConnection[] {
        return this.contents.filter((element: IContainer) => Type.is(element, CEGConnection)) as CEGConnection[];
    }

    private delete(): void {
        this.modal.open('Do you really want to delete all elements in ' + this.model.name + '?')
            .then(() => this.removeAllElements())
            .catch(() => {});
    }

    private removeAllElements(): void {
        let compoundId: string = Id.uuid;
        for (let i = this.connections.length - 1; i >= 0; i--) {
            this.dataService.deleteElement(this.connections[i].url, true, compoundId);
        }
        for (let i = this.nodes.length - 1; i >= 0; i--) {
            this.dataService.deleteElement(this.nodes[i].url, true, compoundId);
        }
    }
}
