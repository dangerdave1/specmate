import { ElementValidatorBase } from '../element-validator-base';
import { IContainer } from '../../model/IContainer';
import { ValidationResult } from '../validation-result';
import { Config } from '../../config/config';
import { Type } from '../../util/type';
import { Validator } from '../validator-decorator';
import { BDDModel } from '../../model/BDDModel';
import { BDDNoTerminalNode } from '../../model/BDDNoTerminalNode';
import { BDDTerminalNode } from '../../model/BDDTerminalNode';
import { BDDNode } from '../../model/BDDNode';
import { BDDConnection } from '../../model/BDDConnection';
import { CEGConnection } from '../../model/CEGConnection';
import { Proxy } from '../../model/support/proxy';

@Validator(BDDModel)
export class WrongConnsValidator extends ElementValidatorBase<BDDModel> {

    // this validator detects if there are deviations from the correct number of outgoing connections from nodes
    // correct: (terminals:0, nonterminals: max. 1 negated & non-negated each)
    public validate(element: BDDModel, contents: IContainer[]): ValidationResult {
        // the nodes with incorrect conns will be put here
        let badnodes: Set<BDDNode> = new Set();
        for (let content of contents) {
            // Case 1: noterminal
            if (Type.is(content, BDDNoTerminalNode)) {
                let currentnode: BDDNoTerminalNode = content as BDDNoTerminalNode;
                let posConns = 0;
                let negConns = 0;
                if (!currentnode.outgoingConnections) {
                    continue;
                }
                for (let conn of currentnode.outgoingConnections) {
                    const connection: BDDConnection = contents.filter(element => element.url === conn.url)[0] as BDDConnection;
                    if (connection.negate) {
                        negConns = negConns + 1;
                    } else {
                        posConns = posConns + 1;
                    }
                }
                if (posConns > 1 || negConns > 1) {
                    badnodes.add(currentnode);
                }
            // Case 2: terminal
            } else if (Type.is(content, BDDTerminalNode)) {
                let currentnode: BDDTerminalNode = content as BDDTerminalNode;
                if (currentnode.outgoingConnections) {
                    badnodes.add(currentnode);
                }
            } else {
                continue;
            }
        } // end of for
        // are there bad nodes?
        if (badnodes.size > 0) {
            // there are nodes with incorrect outgoing conns
            return new ValidationResult(Config.ERROR_WRONG_CONNECTIONS, false, Array.from(badnodes.keys()));
        }
        // everything is fine
        return ValidationResult.VALID;
    }
}
