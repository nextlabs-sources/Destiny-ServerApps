import {ValidationErrors, ValidatorFn, AbstractControl, FormGroup} from '@angular/forms';

export class PasswordValidators {
  static patternValidator(regex: RegExp, error: ValidationErrors): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } => {
      // test the value of the control against the regexp supplied
      const valid = regex.test(control.value);

      // if true, return no error (no error), else return error passed in the second parameter
      return valid ? null : error;
    };
  }

  static consecutiveCharactersValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } => {
      let regex = /(.)\1\1/;
      const valid = !regex.test(control.value);

      // if true, return no error
      return valid ? null : {
        consecutiveCharacters: true
      };
    };
  }

  static customLengthValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } => {
      let error = {passwordLength: true};
      if (!control.value) {
        return error;
      }
      let value = control.value.replace(" ", "");
      if (value.length < 10 || value.length > 128){
        return error;
      }
      return null;
    };
  }

  static passwordMatchValidator(): ValidatorFn{
    return (control: AbstractControl): ValidationErrors => {
      const password: string = control.get('installerPassword').value; // get password from our password form control
      const confirmPassword: string = control.get('confirmPassword').value; // get password from our confirmPassword form control
      // compare is the password math
      if (password !== confirmPassword) {
        // if they don't match, set an error in our confirmPassword form control
        control.get('confirmPassword').setErrors({passwordNotMatch: true});
      } else {
        control.get('confirmPassword').setErrors(null);
      }
      return;
    };
  }
}
