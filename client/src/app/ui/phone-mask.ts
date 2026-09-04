import { Directive, ElementRef, HostListener, inject } from '@angular/core';

/** The literal the mask always starts with. Everything after it is digits. */
const PREFIX = '+32 ';

/** `+32 999 999 9?99` — three, three, then one required digit and up to two optional. */
const GROUPS = [3, 3, 3] as const;
const MIN_DIGITS = 7;
const MAX_DIGITS = GROUPS.reduce((total, size) => total + size, 0);

/** Everything the mask permits, so a paste of `+32 (0)471/23.45.67` still lands correctly. */
export function digitsOf(value: string): string {
  const withoutPrefix = value.startsWith(PREFIX) ? value.slice(PREFIX.length) : value;

  return withoutPrefix.replace(/\D/g, '').slice(0, MAX_DIGITS);
}

export function formatPhoneNumber(value: string): string {
  const digits = digitsOf(value);

  if (digits === '') {
    return '';
  }

  const parts: string[] = [];
  let offset = 0;

  for (const size of GROUPS) {
    if (offset >= digits.length) {
      break;
    }

    parts.push(digits.slice(offset, offset + size));
    offset += size;
  }

  return PREFIX + parts.join(' ');
}

export function isCompletePhoneNumber(value: string): boolean {
  return digitsOf(value).length >= MIN_DIGITS;
}

/**
 * The `+32 999 999 9?99` input mask the site used to get from PrimeNG's `p-inputMask`.
 *
 * Three behaviours are worth spelling out:
 *
 * - **The field stays empty until it is focused.** The float label decides whether to rest
 *   inside the control or float above it from `:placeholder-shown`, so a permanently
 *   pre-filled `+32 ` would pin the label to the top of an untouched, optional field. On blur
 *   a lone prefix is cleared again.
 * - **A partial number is kept, not wiped** — the original set `autoClear="false"`.
 * - **The caret is put back where it belongs.** Reformatting rewrites `value`, which would
 *   otherwise send the caret to the end on every keystroke; counting digits rather than
 *   characters keeps it stable across the inserted spaces.
 */
@Directive({
  selector: 'input[appPhoneMask]',
})
export class PhoneMask {
  private readonly element = inject<ElementRef<HTMLInputElement>>(ElementRef);

  /** Guards against the synthetic `input` event below re-entering `onInput`. */
  private rewriting = false;

  @HostListener('focus')
  protected onFocus(): void {
    const input = this.element.nativeElement;

    if (input.value === '') {
      this.write(PREFIX, PREFIX.length);
    }
  }

  @HostListener('input')
  protected onInput(): void {
    if (this.rewriting) {
      return;
    }

    const input = this.element.nativeElement;
    const caret = input.selectionStart ?? input.value.length;

    // How many digits precede the caret is the one position that survives reformatting.
    const digitsBeforeCaret = digitsOf(input.value.slice(0, caret)).length;
    const formatted = formatPhoneNumber(input.value);

    this.write(formatted === '' ? PREFIX : formatted, this.caretAfter(formatted, digitsBeforeCaret));
  }

  @HostListener('blur')
  protected onBlur(): void {
    const input = this.element.nativeElement;

    if (digitsOf(input.value) === '') {
      this.write('', 0);
    }
  }

  /** The offset just past the nth digit, so the caret lands where the user left it. */
  private caretAfter(formatted: string, digits: number): number {
    if (digits === 0) {
      return PREFIX.length;
    }

    let seen = 0;

    for (let index = PREFIX.length; index < formatted.length; index += 1) {
      if (/\d/.test(formatted[index])) {
        seen += 1;

        if (seen === digits) {
          return index + 1;
        }
      }
    }

    return formatted.length;
  }

  /**
   * Re-emits `input` after correcting the value, so whatever is bound to this control stores
   * the masked text and not the raw keystrokes — whichever order the listeners happen to run in.
   */
  private write(value: string, caret: number): void {
    const input = this.element.nativeElement;

    if (input.value !== value) {
      input.value = value;

      this.rewriting = true;

      try {
        input.dispatchEvent(new Event('input', { bubbles: true }));
      } finally {
        this.rewriting = false;
      }
    }

    input.setSelectionRange(caret, caret);
  }
}
