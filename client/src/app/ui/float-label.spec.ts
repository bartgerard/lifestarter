import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';

import { FloatLabel } from './float-label';

@Component({
  imports: [FloatLabel],
  template: `
    <app-float-label>
      <input id="given" type="text" />
      <label for="given">Given name</label>
    </app-float-label>

    <app-float-label>
      <input id="kept" type="text" placeholder="+32 …" />
      <label for="kept">Phone</label>
    </app-float-label>

    <app-float-label>
      <textarea id="notes"></textarea>
      <label for="notes">Notes</label>
    </app-float-label>

    <app-float-label>
      <select id="country"><option value="">Choose</option></select>
      <label for="country">Country</label>
    </app-float-label>
  `,
})
class Host {}

async function render(): Promise<HTMLElement> {
  const fixture = TestBed.createComponent(Host);
  fixture.detectChanges();
  await fixture.whenStable();

  return fixture.nativeElement as HTMLElement;
}

describe('FloatLabel', () => {
  it('gives a bare control the blank placeholder the CSS needs to detect emptiness', async () => {
    const host = await render();

    expect(host.querySelector<HTMLInputElement>('#given')!.placeholder).toBe(' ');
    expect(host.querySelector<HTMLTextAreaElement>('#notes')!.placeholder).toBe(' ');
  });

  it('never overwrites a placeholder the caller meant to show', async () => {
    const host = await render();

    expect(host.querySelector<HTMLInputElement>('#kept')!.placeholder).toBe('+32 …');
  });

  it('leaves the label a real, addressable label', async () => {
    const host = await render();
    const label = host.querySelector<HTMLLabelElement>('label[for="given"]')!;

    expect(label.htmlFor).toBe('given');
    expect(label.textContent?.trim()).toBe('Given name');
    expect(label.control).toBe(host.querySelector('#given'));
  });

  it('marks its host so the stylesheet can find it', async () => {
    const host = await render();

    expect(host.querySelectorAll('.float-label')).toHaveLength(4);
  });

  it('has nothing to add to a select, which cannot be "empty"', async () => {
    const host = await render();

    expect(host.querySelector<HTMLSelectElement>('#country')!.hasAttribute('placeholder')).toBe(
      false,
    );
  });
});
