import { ElementValidatorBase } from '../element-validator-base';
import { IContainer } from '../../model/IContainer';
import { ValidationResult } from '../validation-result';
import { Config } from '../../config/config';
import { Type } from '../../util/type';
import { Validator } from '../validator-decorator';
import { BDDModel } from '../../model/BDDModel';
import { BDDNoTerminalNode } from '../../model/BDDNoTerminalNode';

@Validator(BDDModel)
export class BDDEmptyModelValidator extends ElementValidatorBase<BDDModel> {

    // this validator detects if there is no BDDNoTerminalNode (in this case the BDD has no meaningful content)
    public validate(element: BDDModel, contents: IContainer[]): ValidationResult {
        const valid: boolean = contents.some((element: IContainer) => Type.is(element, BDDNoTerminalNode));
        if (valid) {
            return ValidationResult.VALID;
        }
        return new ValidationResult(Config.ERROR_NO_NONTERMINAL, false, []);
    }
}
