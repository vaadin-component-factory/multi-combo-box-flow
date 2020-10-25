/**
 * @license
 * Copyright (C) 2015 Vaadin Ltd.
 * This program is available under Commercial Vaadin Add-On License 3.0 (CVALv3).
 * See the file LICENSE.md distributed with this software for more information about licensing.
 * See [the website]{@link https://vaadin.com/license/cval-3} for the complete license.
 */

// import { html, PolymerElement } from '@polymer/polymer/polymer-element';
import { ThemableMixin } from '@vaadin/vaadin-themable-mixin';
import { ElementMixin } from '@vaadin/vaadin-element-mixin';
import { ComboBoxElement } from '@vaadin/vaadin-combo-box';
import '@vaadin/vaadin-license-checker/vaadin-license-checker';
import '@vaadin/vaadin-checkbox/vaadin-checkbox';

import { commitValue, overlaySelectedItemChanged, renderer, onEnter, _filteredItemsChanged, filterChanged, renderLabel } from './helpers.js';

/**
 * `<vcf-multiselect-combo-box>` A multiselect combobox
 *
 * ```html
 * <vcf-multiselect-combo-box></vcf-multiselect-combo-box>
 * ```
 *
 * ### Styling
 *
 * The following custom properties are available for styling:
 *
 * Custom property | Description | Default
 * ----------------|-------------|-------------
 * `--vcf-multiselect-combo-box-property` | Example custom property | `unset`
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------------|----------------
 * `part` | Example part
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description | Part name
 * -------------|-------------|------------
 * `attribute` | Example styling attribute | :host
 *
 * @memberof Vaadin
 * @mixes ElementMixin
 * @mixes ThemableMixin
 * @demo demo/index.html
 */
class VcfMultiselectComboBox extends ElementMixin(ThemableMixin(ComboBoxElement)) {

  constructor() {
    super();

    this._boundOverriddenCommitValue = commitValue.bind(this);
    this.renderLabel = renderLabel.bind(this);
    this._boundOverriddenOverlaySelectedItemChanged = overlaySelectedItemChanged.bind(this);
    this._boundRenderer = renderer.bind(this);
    this._boundOnEnter = onEnter.bind(this);
    this._filteredItemsChanged = _filteredItemsChanged.bind(this);

    // This will prevent the component from setting the
    // `value` property and showing the blue tick beside
    // the selected item.
    this._selectedItemChanged = () => {};
    this._prefillFocusedItemLabel = () => {};
  }

  static get properties() {
    return {
      selectedItems: {
        type: Array,
        value: () => [],
        observer: '_selectedItemsChanged'
      }
    };
  }

  ready() {
    super.ready();

    this._commitValue = this._boundOverriddenCommitValue;
    this.renderer = this._boundRenderer;
    this._onEnter = this._boundOnEnter;
    this._filterChanged = filterChanged.bind(this);

    const boundOldOpenedChanged = this._openedChanged.bind(this);
    this._openedChanged = (value, old) => {
      boundOldOpenedChanged(value, old);

      if (value) {
        this._addTopButtons();

        this._inputElementValue = '';
      }
    }
  }

  connectedCallback() {
    super.connectedCallback();

    this.$.overlay.removeEventListener('selection-changed', this._boundOverlaySelectedItemChanged);
    this.$.overlay.addEventListener('selection-changed', this._boundOverriddenOverlaySelectedItemChanged);
    /* item selection instead of checkbox
    var that = this;
    this._oldisItemSelected  = this.$.overlay._isItemSelected;
    this.$.overlay._isItemSelected = function(item, selectedItem, itemIdPath) {
      if (item !== undefined) {
        // find first item in selected item
        return that.selectedItems.some(value => this.get(itemIdPath, item) === this.get(itemIdPath, value));
      }
      return false;
    }*/
  }

  _selectedItemsChanged(value, oldValue) {
    if (this.items) {
      this.items = this.items
          .sort((a, b) => {
            if (typeof a === 'string') {
              if (this.selectedItems.indexOf(a) > -1) {
                return -1;
              } else if (this.selectedItems.indexOf(b) > -1) {
                return 1;
              } else {
                return 0;
              }
            } else {
              if (this.selectedItems.some(i => i[this.itemValuePath] === a[this.itemValuePath])) {
                return -1;
              } else if (this.selectedItems.some(i => i[this.itemValuePath] === b[this.itemValuePath])) {
                return 1;
              } else {
                return 0;
              }
            }
          })
          .slice(0);
    }

    this.render();
    /*this._inputElementValue = '';*/

    const e = new CustomEvent('selected-items-changed', {
      detail: value,
      composed: true,
      cancelable: false,
      bubbles: true
    });
    this.dispatchEvent(e);
  }

  /** @private */
  _isItemChecked(item) {
    if (typeof item === 'string') {
      return this.selectedItems.indexOf(item) > -1;
    } else {
      return this.selectedItems.some(i => i[this.itemValuePath] === item[this.itemValuePath]);
    }
  }

  /** @private */
  _selectItem(item) {
    if (!this._isItemChecked(item)) {
      this.selectedItems = [...this.selectedItems, item];
    }
  }

  _deselectItem(item) {
    if (this._isItemChecked(item)) {
      const itemIndex = this.selectedItems.findIndex(i => {
        if (typeof item === 'string') {
          return i === item;
        } else {
          return i[this.itemValuePath] === item[this.itemValuePath];
        }
      });

      this.selectedItems = [...this.selectedItems.slice(0, itemIndex), ...this.selectedItems.slice(itemIndex + 1)];
    }
  }

  /** @private */
  _addTopButtons() {
    if (this.opened) {
      const topButtonsContainer = document.createElement('div');
      topButtonsContainer.id = 'top-buttons-container';
      topButtonsContainer.style.display = 'flex';
      topButtonsContainer.style.flexDirection = 'row';
      /*topButtonsContainer.style.padding = '0 .5em 0';*/
      const selectAllButton = document.createElement('vaadin-button');
      selectAllButton.innerText = "Select All";
      selectAllButton.setAttribute("theme", "small");
      selectAllButton.style.flexGrow = 1;
      const clearButton = document.createElement('vaadin-button');
      clearButton.setAttribute("theme", "small");
      clearButton.innerText = "Clear";

      const cancelButton = document.createElement('vaadin-button');
      cancelButton.innerText = "Cancel";
      cancelButton.setAttribute("theme", "small");
      cancelButton.style.flexGrow = 1;


      selectAllButton.addEventListener('click', () => {
        if (this.items) {
          this.selectedItems = [...this.items];
        } else if (this.$server) {
          this.$server.selectAll();
        }
      });

      clearButton.addEventListener('click', () => {
        this.selectedItems = [];
      });

      cancelButton.addEventListener('click', () => {
        if (this.$server) {
          this.$server.cancelChanges();
        }
      });

      topButtonsContainer.appendChild(selectAllButton);
      if (this.$server) {
        topButtonsContainer.appendChild(cancelButton);
      } else {
        topButtonsContainer.appendChild(clearButton);
      }
      const targetNode = this.$.overlay.$.dropdown.$.overlay.$.content.shadowRoot;
      if (!targetNode.querySelector('#top-buttons-container')) {
        this.$.overlay.$.dropdown.$.overlay.$.content.shadowRoot.prepend(topButtonsContainer);
      }
    }
  }

  /**
   * @protected
   */
  static _finalizeClass() {
    super._finalizeClass();

    const devModeCallback = window.Vaadin.developmentModeCallback;
    const licenseChecker = devModeCallback && devModeCallback['vaadin-license-checker'];
    if (typeof licenseChecker === 'function') {
      licenseChecker(VcfMultiselectComboBox);
    }
  }

  static get is() {
    return 'vcf-multiselect-combo-box';
  }

  static get version() {
    return '0.1.3';
  }
}

customElements.define(VcfMultiselectComboBox.is, VcfMultiselectComboBox);

/**
 * @namespace Vaadin
 */
window.Vaadin.VcfMultiselectComboBox = VcfMultiselectComboBox;
