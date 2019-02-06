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
        let terms: Set<BDDTerminalNode> = new Set();
        let noterms: Set<BDDNoTerminalNode> = new Set();
        let conns: Set<BDDConnection> = new Set();
        // first: get content in order
        for (let content of contents) {
            // Case 1: noterminal
            if (Type.is(content, BDDNoTerminalNode)) {
                noterms.add(content as BDDNoTerminalNode);
            // Case 2: terminal
            } else if (Type.is(content, BDDTerminalNode)) {
                terms.add(content as BDDTerminalNode);
            // Case 3: conn
            } else if (Type.is(content, BDDConnection)) {
                conns.add(content as BDDConnection);
            }
        } // end of for
        // the nodes with incorrect conns will be put here
        let badnodes: Set<BDDNode> = new Set();
        // walk through terms
        for (let term of Array.from(terms)) {
            if (term.outgoingConnections && term.outgoingConnections.length >= 1) {
                badnodes.add(term);
            }
        }
        // walk through noterms
        for (let noterm of Array.from(noterms)) {
            // counters for the outgoing conns
            let posConns = 0;
            let negConns = 0;
            for (let conn of noterm.outgoingConnections) {
                // if ((conn as BDDConnection).negate) {
                // wie kann man Proxy zu BDDConnection konvertieren?
            }
            if (posConns > 1 || negConns > 1) {
                badnodes.add(noterm);
            }
        }
        // the end
        if (badnodes.size > 0) {
            // there are nodes with incorrect outgoing conns
            return new ValidationResult(Config.ERROR_WRONG_CONNECTIONS, false, Array.from(badnodes.keys()));
        }
        // everything is fine
        return ValidationResult.VALID;
    }
}
