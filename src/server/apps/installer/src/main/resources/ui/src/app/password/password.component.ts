import {Component, OnInit, forwardRef, Input, ViewChild, ElementRef, AfterViewInit} from '@angular/core';
import {FormGroup, Validators, FormControl, AbstractControl, ValidationErrors} from '@angular/forms';
import {ControlValueAccessor, Validator, NG_VALUE_ACCESSOR, NG_VALIDATORS} from '@angular/forms';
import {PasswordValidators} from "../password-validators";
import {PASSWORD_FIELD_TOUCHED} from "../installer-constants";

@Component({
  selector: 'app-password',
  templateUrl: './password.component.html',
  styleUrls: ['./password.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PasswordComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => PasswordComponent),
      multi: true
    }
  ]
})
export class PasswordComponent implements OnInit, AfterViewInit, ControlValueAccessor, Validator {

  @Input() name: string;

  @ViewChild('popoverHandle', {static: false}) popoverHandle: ElementRef;

  showPassword = false;

  passwordForm: FormGroup;
  onChange = (_: any) => {
  };
  onTouched = () => {
  };
  value: string = '';

  constructor() {
  }

  ngOnInit() {
    this.passwordForm = new FormGroup({
        'installerPassword': new FormControl('', Validators.compose([
          Validators.required,
          // check whether the entered password has a number
          PasswordValidators.patternValidator(/\d/, {
            number: true
          }),
          // check whether the entered password has upper case letter
          PasswordValidators.patternValidator(/[A-Z]/, {
            upperCase: true
          }),
          // check whether the entered password has a lower case letter
          PasswordValidators.patternValidator(/[a-z]/, {
            lowerCase: true
          }),
          // check whether the entered password has a special character
          PasswordValidators.patternValidator(
            /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/,
            {
              specialCharacters: true
            }
          ),
          // check for more than two identical consecutive characters
          PasswordValidators.consecutiveCharactersValidator(),
          PasswordValidators.customLengthValidator()
        ])),
        'confirmPassword': new FormControl('', Validators.required)
      }
    );
    this.passwordForm.setValidators(PasswordValidators.passwordMatchValidator());
  }

  ngAfterViewInit(): void {

    this.passwordForm.get('installerPassword').valueChanges.subscribe((value) => {
      if (this.passwordForm.get('installerPassword').valid){
        // @ts-ignore
        this.popoverHandle.close();
      } else if (this.passwordForm.get('installerPassword').touched){
        // @ts-ignore
        this.popoverHandle.open();
      }
    });
  }

  togglePasswordHide() {
    this.showPassword = !this.showPassword;
  }

  writeValue(value: any): void {
    let password = value || '';
    if (value === PASSWORD_FIELD_TOUCHED) {
      this.passwordForm.get('installerPassword').markAsTouched();
      this.passwordForm.get('confirmPassword').markAsTouched();
    } else {
      this.passwordForm.get('installerPassword').setValue(password);
      this.passwordForm.get('confirmPassword').setValue(password);
    }
  }

  pushChanges() {
    this.onChange(this.passwordForm.value.installerPassword);
  }

  registerOnChange(fn: (_: any) => {}): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => {}): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
  }

  validate(c: AbstractControl): ValidationErrors | null {
    return this.passwordForm.get('installerPassword').errors || this.passwordForm.get('confirmPassword').errors;
  }
}
