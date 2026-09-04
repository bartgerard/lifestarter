import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  afterNextRender,
  inject,
} from '@angular/core';

/**
 * A label that sits inside its control and floats above it once the control is focused or filled —
 * the pattern the old build got from PrimeNG's `<span class="ui-float-label">`.
 *
 * PrimeNG is gone, so this is a native reimplementation. Unlike PrimeNG's version it needs no
 * JavaScript to track "is this filled": the floating is driven entirely by `:placeholder-shown`
 * and `:focus-within` in `styles.css`. Project the control first and its `<label>` second, exactly
 * as `<p-floatLabel>` expects:
 *
 * ```html
 * <app-float-label>
 *   <input class="control" id="email" type="email" [formField]="form.contact.email" />
 *   <label for="email">E-mail</label>
 * </app-float-label>
 * ```
 *
 * The `<label for>` stays a real, permanently visible label rather than a placeholder standing in
 * for one, so the accessible name survives once the field is filled — which is the whole reason to
 * prefer a float label over a bare `placeholder`.
 */
@Component({
  selector: 'app-float-label',
  template: '<ng-content />',
  host: { class: 'float-label' },
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FloatLabel {
  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);

  constructor() {
    afterNextRender(() => this.ensurePlaceholder());
  }

  /**
   * `:placeholder-shown` only matches a control that actually has a placeholder, so the CSS needs
   * one to decide whether the field is empty. A single space renders nothing and is ignored by
   * screen readers, which is why it is safe to add here rather than asking every caller to
   * remember it.
   */
  private ensurePlaceholder(): void {
    const control = this.host.nativeElement.querySelector<HTMLInputElement | HTMLTextAreaElement>(
      'input:not([type="checkbox"]):not([type="radio"]), textarea',
    );

    if (control && control.placeholder === '') {
      control.placeholder = ' ';
    }
  }
}
