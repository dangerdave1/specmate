import { NgModule } from '@angular/core';
import { GraphicalEditorModule } from '../graphical-editor/graphical-editor.module';
import { BrowserModule } from '@angular/platform-browser';
import { BDDModelDetails } from './components/bdd-model-details.component';

@NgModule({
  imports: [
    // MODULE IMPORTS
    GraphicalEditorModule,
    BrowserModule
  ],
  declarations: [
    // COMPONENTS IN THIS MODULE
    BDDModelDetails
  ],
  exports: [
    // THE COMPONENTS VISIBLE TO THE OUTSIDE
    BDDModelDetails
  ],
  providers: [
    // SERVICES
  ],
  bootstrap: [
    // COMPONENTS THAT ARE BOOTSTRAPPED HERE
  ]
})

export class BDDModelEditorModule { }
