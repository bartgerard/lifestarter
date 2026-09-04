import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { FieldTree, ValidationError } from '@angular/forms/signals';
import { TranslatePipe } from '@ngx-translate/core';

/** The validator kinds this app uses, mapped to their translation key. */
const MESSAGE_KEYS: Readonly<Record<string, string>> = {
  required: 'validation.required',
  email: 'validation.email',
  maxLength: 'validation.max-length',
  phoneNumber: 'validation.phone-number',
};

interface Message {
  readonly key: string;
  readonly params: Record<string, unknown>;
}

function isMaxLengthError(
  error: ValidationError,
): error is ValidationError & { readonly maxLength: number } {
  return error.kind === 'maxLength' && 'maxLength' in error;
}

/**
 * Renders the errors of a single field, but only once the guest has had a chance to fill it in.
 *
 * The element is always present in the DOM so that `aria-describedby` on the input can point at a
 * stable id; screen readers announce the change because the region is polite-live.
 */
@Component({
  selector: 'app-field-errors',
  imports: [TranslatePipe],
  template: `
    <p class="field__error" [id]="id()" aria-live="polite">
      @for (message of messages(); track $index) {
        <span>{{ message.key | translate: message.params }}</span>
      }
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FieldErrors<T> {
  readonly field = input.required<FieldTree<T>>();
  readonly id = input.required<string>();

  protected readonly messages = computed<readonly Message[]>(() => {
    const state = this.field()();

    if (!state.touched()) {
      return [];
    }

    return state.errors().map((error) => ({
      key: MESSAGE_KEYS[error.kind] ?? 'common.error',
      params: isMaxLengthError(error) ? { max: error.maxLength } : {},
    }));
  });
}
