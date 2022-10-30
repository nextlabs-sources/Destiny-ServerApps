import {
  Component,
  OnInit,
  Input,
  forwardRef,
  Renderer2,
  ViewChild,
  ElementRef,
  Output,
  EventEmitter
} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-file-drag-drop',
  templateUrl: './file-drag-drop.component.html',
  styleUrls: ['./file-drag-drop.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FileDragDropComponent),
      multi: true
    }
  ]
})
export class FileDragDropComponent implements OnInit, ControlValueAccessor {

  @Input() fileLabel: string;
  @Input() setClass: string;
  @Input() acceptMultipleFile: boolean = false;
  @Output() onFileDelete = new EventEmitter();

  @ViewChild('upload_file_container', {static: false}) uploadFileContainer: ElementRef;
  @ViewChild('fileInput', {static: false}) fileInput: ElementRef;

  fileInputElement: HTMLElement;

  files: File[] = [];
  propagateChange = (_: any) => {
  };
  touched = () => {
  };

  constructor(private renderer: Renderer2) {
  }

  ngOnInit() {
  }

  selectFiles(fileList: FileList) {
    if (fileList.length > 0 && !this.uploadFileContainer.nativeElement.classList.contains('disabled')) {
      if (this.acceptMultipleFile) {
        Array.from(fileList).forEach(file => {
          this.files.push(file);
        });
      } else {
        this.files = [];
        this.files.push(fileList[0]);
      }
      this.touched();
      this.propagateChange(this.files);
    }
    this.fileInput.nativeElement.value = null;
  }

  deleteAttachment(index: number) {
    this.onFileDelete.emit(this.files[index]);
    this.files.splice(index, 1);
    this.touched();
    this.propagateChange(null);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.touched = fn;
  }

  writeValue(value: any): void {
    if (value != undefined) {
      this.files = value;
    }
  }

  setDisabledState(isDisabled: boolean) {
    setTimeout(() => {
      if (isDisabled) {
        this.renderer.addClass(this.uploadFileContainer.nativeElement, 'disabled');
      } else {
        this.renderer.removeClass(this.uploadFileContainer.nativeElement, 'disabled');
      }
    }, 50);
  }
}
