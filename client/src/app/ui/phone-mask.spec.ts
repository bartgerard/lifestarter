import { Component, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';

import { PhoneMask, digitsOf, formatPhoneNumber, isCompletePhoneNumber } from './phone-mask';

describe('formatPhoneNumber', () => {
  it('groups digits as +32 999 999 999', () => {
    expect(formatPhoneNumber('471234567')).toBe('+32 471 234 567');
  });

  it('formats a partial number as far as it goes', () => {
    expect(formatPhoneNumber('4')).toBe('+32 4');
    expect(formatPhoneNumber('4712')).toBe('+32 471 2');
    expect(formatPhoneNumber('4712345')).toBe('+32 471 234 5');
  });

  it('keeps an empty value empty, so the float label still rests inside the field', () => {
    expect(formatPhoneNumber('')).toBe('');
    expect(formatPhoneNumber('+32 ')).toBe('');
  });

  it('rescues a pasted number written any which way', () => {
    expect(formatPhoneNumber('+32 (0)471/23.45.67')).toBe('+32 047 123 456');
    expect(formatPhoneNumber('0032471234567')).toBe('+32 003 247 123');
  });

  it('refuses more digits than the mask holds', () => {
    expect(digitsOf('4712345678901')).toHaveLength(9);
    expect(formatPhoneNumber('4712345678901')).toBe('+32 471 234 567');
  });
});

describe('isCompletePhoneNumber', () => {
  it('accepts seven digits, the point where the mask turns optional', () => {
    expect(isCompletePhoneNumber('+32 471 234 5')).toBe(true);
    expect(isCompletePhoneNumber('+32 471 234 567')).toBe(true);
  });

  it('rejects a number abandoned half-way', () => {
    expect(isCompletePhoneNumber('+32 471 23')).toBe(false);
    expect(isCompletePhoneNumber('+32 ')).toBe(false);
  });
});

@Component({
  imports: [PhoneMask],
  template: `<input id="phone" type="tel" appPhoneMask (input)="onInput($event)" />`,
})
class Host {
  readonly seen = signal('');

  onInput(event: Event): void {
    this.seen.set((event.target as HTMLInputElement).value);
  }
}

function host() {
  const fixture = TestBed.createComponent(Host);
  fixture.detectChanges();

  const element = fixture.nativeElement as HTMLElement;
  const input = element.querySelector<HTMLInputElement>('#phone')!;

  const type = (value: string) => {
    input.value = value;
    input.setSelectionRange(value.length, value.length);
    input.dispatchEvent(new Event('input', { bubbles: true }));
    fixture.detectChanges();
  };

  const fire = (name: string) => {
    input.dispatchEvent(new Event(name));
    fixture.detectChanges();
  };

  return { fixture, input, type, fire };
}

describe('PhoneMask directive', () => {
  it('leaves an untouched field empty', () => {
    const { input } = host();

    expect(input.value).toBe('');
  });

  it('offers the prefix once the field is focused', () => {
    const { input, fire } = host();

    fire('focus');

    expect(input.value).toBe('+32 ');
    expect(input.selectionStart).toBe('+32 '.length);
  });

  it('formats as the guest types', () => {
    const { input, type, fire } = host();

    fire('focus');
    type('+32 471');
    expect(input.value).toBe('+32 471');

    type('+32 4712');
    expect(input.value).toBe('+32 471 2');
  });

  it('reports the masked value, not the raw keystrokes', () => {
    const { fixture, type, fire } = host();

    fire('focus');
    type('+32 471234567');

    expect(fixture.componentInstance.seen()).toBe('+32 471 234 567');
  });

  it('keeps a half-typed number rather than clearing it, as autoClear="false" did', () => {
    const { input, type, fire } = host();

    fire('focus');
    type('+32 47123');
    fire('blur');

    expect(input.value).toBe('+32 471 23');
  });

  it('empties a field where only the prefix was ever offered', () => {
    const { input, fire } = host();

    fire('focus');
    fire('blur');

    // Otherwise the float label would stay pinned above an untouched, optional field.
    expect(input.value).toBe('');
  });

  it('puts the caret after the digit just typed, not at the end of the group separator', () => {
    const { input, fire } = host();

    fire('focus');

    input.value = '+32 471';
    input.setSelectionRange(7, 7);
    input.dispatchEvent(new Event('input', { bubbles: true }));

    // Three digits in, so just past the last one — before the space the mask is about to add.
    expect(input.selectionStart).toBe(7);
  });
});
