/* eslint-disable no-invalid-this */

export function renderer(root, owner, model) {
  let labelText = '';
  if (!(typeof model.item === 'string')) {
    labelText = model.item[this.itemLabelPath];
  } else {
    labelText = model.item;
  }
  if (root.firstElementChild) {
    root.innerHTML = '';
  }
  const itemCheckbox = document.createElement('vaadin-checkbox');
  itemCheckbox.checked = this._isItemChecked(model.item) ? true : false;
  itemCheckbox.addEventListener('change', () => {
    if (itemCheckbox.checked) {
      this._selectItem(model.item);
    } else {
      this._deselectItem(model.item);
    }
  });
  itemCheckbox.textContent = labelText;
  root.appendChild(itemCheckbox);
}

export function commitValue() {
  if (this.$.overlay._items && this._focusedIndex > -1) {
    const focusedItem = this.$.overlay._items[this._focusedIndex];
    if (this.selectedItem !== focusedItem) {
      this.selectedItem = focusedItem;
    }
    // make sure input field is updated in case value doesn't change (i.e. FOO -> foo)
    // this._inputElementValue = this._getItemLabel(this.selectedItem);
  } else if (this._inputElementValue === '' || this._inputElementValue === undefined) {
    this.selectedItem = null;

    if (this.allowCustomValue) {
      this.value = '';
    }
  } else {
    if (
        this.allowCustomValue &&
        // to prevent a repetitive input value being saved after pressing ESC and Tab.
        !(
            this.filteredItems &&
            this.filteredItems.filter(item => this._getItemLabel(item) === this._inputElementValue).length
        )
    ) {
      const e = new CustomEvent('custom-value-set', {
        detail: this._inputElementValue,
        composed: true,
        cancelable: true,
        bubbles: true
      });
      this.dispatchEvent(e);
      if (!e.defaultPrevented) {
        const customValue = this._inputElementValue;
        this._selectItemForValue(customValue);
        this.value = customValue;
      }
    } else {
      // this._inputElementValue = this.selectedItem ? this._getItemLabel(this.selectedItem) : (this.value || '');
    }
  }

  this._detectAndDispatchChange();

  this._clearSelectionRange();

  if (!this.dataProvider) {
    this.filter = '';
  }
}

export function overlaySelectedItemChanged(e) {
  // stop this private event from leaking outside.
  e.stopPropagation();

  if (this.opened) {
    this._focusedIndex = this.filteredItems.indexOf(e.detail.item);
  } else if (this.selectedItem !== e.detail.item) {
    this.selectedItem = e.detail.item;
    this._detectAndDispatchChange();
  }
}

export function onEnter(e) {
  // should close on enter when custom values are allowed, input field is cleared, or when an existing
  // item is focused with keyboard. If auto open is disabled, under the same conditions, commit value.
  if (
      (this.opened || this.autoOpenDisabled) &&
      (this.allowCustomValue || this._inputElementValue === '' || this._focusedIndex > -1)
  ) {
    const targetItem = this.items[this._focusedIndex];
    if (!this._isItemChecked(targetItem)) {
      this._selectItem(targetItem);
    } else {
      this._deselectItem(targetItem);
    }

    // Do not submit the surrounding form.
    e.preventDefault();

    // Do not trigger global listeners
    e.stopPropagation();
  }
}
