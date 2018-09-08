import {Pipe, PipeTransform} from '@angular/core';
import {TranslationService} from "../service/translation.service";

@Pipe({
  name: 'translate'
})
export class TranslatePipe implements PipeTransform {

  constructor(
    private translationService: TranslationService
  ) {
  }

  transform(
    value: any,
    args?: any
  ): string {
    return this.translationService.translate(value);
  }

}
