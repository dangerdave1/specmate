import { ElementValidatorBase } from '../element-validator-base';
import { IContainer } from '../../model/IContainer';
import { ValidationResult } from '../validation-result';
import { Config } from '../../config/config';
import { Type } from '../../util/type';
import { Validator } from '../validator-decorator';
import { BDDModel } from '../../model/BDDModel';
import { BDDNoTerminalNode } from '../../model/BDDNoTerminalNode';
import { BDDTerminalNode } from '../../model/BDDTerminalNode';
import { BDDConnection } from '../../model/BDDConnection';
import { BDDNode } from '../../model/BDDNode';

@Validator(BDDModel)
export class MultipleEntriesValidator extends ElementValidatorBase<BDDModel> {

    // this validator detects if there are too many startnodes (=nodes without incoming connections)
    public validate(element: BDDModel, contents: IContainer[]): ValidationResult {
        // sets to put all the nodes without incoming conns
        let startnodes: Set<BDDNode> = new Set();
        // look at all elements of the model
        for (let content of contents) {
            // Case 1: noterminal
            if (Type.is(content, BDDNoTerminalNode)) {
                let currentnode: BDDNoTerminalNode = content as BDDNoTerminalNode;
                if (!currentnode.incomingConnections || currentnode.incomingConnections.length <= 0) {
                    startnodes.add(currentnode);
                }
            // Case 2: terminal
            } else if (Type.is(content, BDDTerminalNode)) {
                let currentnode: BDDTerminalNode = content as BDDTerminalNode;
                if (!currentnode.incomingConnections || currentnode.incomingConnections.length <= 0) {
                    startnodes.add(currentnode);
                }
            // Case 3: sth else
            } else {
                continue;
            }
        } // end of for
        if (startnodes.size > 1) {
            // too many startnodes: return false validation and the problematic nodes in an array
            return new ValidationResult(Config.ERROR_MULTIPLE_ENTRIES, false, Array.from(startnodes.keys()));
        }
        // if this point is reached, everything is fine
        return ValidationResult.VALID;
    }
}
