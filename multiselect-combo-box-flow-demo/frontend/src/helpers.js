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

  this.renderLabel();
}

export function renderLabel() {

  this._inputElementValue = this.selectedItems.reduce((prev, current) => {
    let val = '';
    if ((typeof current === 'string')) {
      val = current;
    } else {
      val = current[this.itemLabelPath];
    }
    return `${val}${prev === '' ? '' : `, ${prev}`}`;
  }, '');
}

export function overlaySelectedItemChanged(e) {
  // stop this private event from leaking outside.
  e.stopPropagation();
/*
  if (!this._isItemChecked(e.detail.item)) {
    this._selectItem(e.detail.item);
  } else {
    this._deselectItem(e.detail.item);
  }*/
  if (this.opened) {
    this._focusedIndex = this.filteredItems.indexOf(e.detail.item);
  } else if (this.selectedItem !== e.detail.item) {
    this.selectedItem = e.detail.item;
    this._detectAndDispatchChange();
  }
  this.dispatchEvent(new CustomEvent('change', {bubbles: true}));
}

export function onEnter(e) {
  // should close on enter when custom values are allowed, input field is cleared, or when an existing
  // item is focused with keyboard. If auto open is disabled, under the same conditions, commit value.
  if (
      (this.opened || this.autoOpenDisabled) &&
      (this.allowCustomValue || this._inputElementValue === '' || this._focusedIndex > -1)
  ) {
    const targetItem = this.filteredItems[this._focusedIndex];

    if (!(typeof targetItem === 'undefined')) {
      if (!this._isItemChecked(targetItem)) {
        this._selectItem(targetItem);
      } else {
        this._deselectItem(targetItem);
      }
    }

    // Do not submit the surrounding form.
    e.preventDefault();

    // Do not trigger global listeners
    e.stopPropagation();
  }
}

export function filterChanged(filter, itemValuePath, itemLabelPath) {
  if (filter === undefined) {
    return;
  }
  console.log("filterChanged");

  // Notify the dropdown about filter changing, so to let it skip the
  // scrolling restore
  this.$.overlay.filterChanged = true;

  if (this.items) {
    if (filter) {
      this.filteredItems = [...this.selectedItems, ...this._filterItems(this.items, filter)];
    } else {
      this.filteredItems = this._filterItems(this.items, filter);
    }
  } else {
    // With certain use cases (e. g., external filtering), `items` are
    // undefined. Filtering is unnecessary per se, but the filteredItems
    // observer should still be invoked to update focused item.
    this._filteredItemsChanged({path: 'filteredItems', value: this.filteredItems}, itemValuePath, itemLabelPath);
  }
}



/**
 * Change the default to focus on the first items not selected after filtering
 * @param e
 * @param itemValuePath
 * @param itemLabelPath
 * @private
 */
export function _filteredItemsChanged(e, itemValuePath, itemLabelPath) {
  if (e.value === undefined) {
    return;
  }
  if (e.path === 'filteredItems' || e.path === 'filteredItems.splices') {
    this._setOverlayItems(this.filteredItems);

    if (this.opened || this.autoOpenDisabled) {
      this._focusedIndex = this.filteredItems.findIndex(item => !this._isItemChecked(item));
    } else {
      this._focusedIndex = -1;
    }

    if (this.opened) {
      this._repositionOverlay();
    }
  }
}

