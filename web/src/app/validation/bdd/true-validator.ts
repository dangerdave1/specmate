import { ElementValidatorBase } from '../element-validator-base';
import { IContainer } from '../../model/IContainer';
import { ValidationResult } from '../validation-result';
import { Config } from '../../config/config';
import { Type } from '../../util/type';
import { Validator } from '../validator-decorator';
import { BDDModel } from '../../model/BDDModel';

@Validator(BDDModel)
export class TrueValidator extends ElementValidatorBase<BDDModel> {

    public validate(element: BDDModel, contents: IContainer[]): ValidationResult {
        return ValidationResult.VALID;
    }
}
