package com.vaadin.componentfactory.multiselect;

/*
 * #%L
 * Multiselect combobox Component
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.NotSupported;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.Set;


@Tag("vcf-multiselect-combo-box")
@JsModule("@vaadin-component-factory/vcf-multiselect-combo-box/src/vcf-multiselect-combo-box.js")
@NpmPackage(value = "@vaadin-component-factory/vcf-multiselect-combo-box", version = "2.0.1")
public abstract class GeneratedMultiComboBox<R extends GeneratedMultiComboBox<R, T>, T>
        extends AbstractSinglePropertyField<R, Set<T>>
        implements HasStyle, Focusable<R> {

    /**
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * 
     * @return the {@code autofocus} property from the webcomponent
     */
    protected boolean isAutofocusBoolean() {
        return getElement().getProperty("autofocus", false);
    }

    /**
     * @param autofocus
     *            the boolean value to set
     */
    protected void setAutofocus(boolean autofocus) {
        getElement().setProperty("autofocus", autofocus);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to disable this input.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code disabled} property from the webcomponent
     */
    protected boolean isDisabledBoolean() {
        return getElement().getProperty("disabled", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to disable this input.
     * </p>
     * 
     * @param disabled
     *            the boolean value to set
     */
    protected void setDisabled(boolean disabled) {
        getElement().setProperty("disabled", disabled);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Number of items fetched at a time from the dataprovider.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code pageSize} property from the webcomponent
     */
    protected double getPageSizeDouble() {
        return getElement().getProperty("pageSize", 0.0);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Number of items fetched at a time from the dataprovider.
     * </p>
     * 
     * @param pageSize
     *            the double value to set
     */
    protected void setPageSize(double pageSize) {
        getElement().setProperty("pageSize", pageSize);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Total number of items.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code size} property from the webcomponent
     */
    protected double getSizeDouble() {
        return getElement().getProperty("size", 0.0);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Total number of items.
     * </p>
     * 
     * @param size
     *            the double value to set
     */
    protected void setSize(double size) {
        getElement().setProperty("size", size);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * True if the dropdown is open, false otherwise.
     * <p>
     * This property is synchronized automatically from client side when a
     * 'opened-changed' event happens.
     * </p>
     * 
     * @return the {@code opened} property from the webcomponent
     */
    @Synchronize(property = "opened", value = "opened-changed")
    protected boolean isOpenedBoolean() {
        return getElement().getProperty("opened", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * True if the dropdown is open, false otherwise.
     * </p>
     * 
     * @param opened
     *            the boolean value to set
     */
    protected void setOpened(boolean opened) {
        getElement().setProperty("opened", opened);
    }

    /**
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * 
     * @return the {@code readonly} property from the webcomponent
     */
    protected boolean isReadonlyBoolean() {
        return getElement().getProperty("readonly", false);
    }

    /**
     * @param readonly
     *            the boolean value to set
     */
    protected void setReadonly(boolean readonly) {
        getElement().setProperty("readonly", readonly);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A full set of items to filter the visible options from. The items can be
     * of either {@code String} or {@code Object} type.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code items} property from the webcomponent
     */
    protected JsonArray getItemsJsonArray() {
        return (JsonArray) getElement().getPropertyRaw("items");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A full set of items to filter the visible options from. The items can be
     * of either {@code String} or {@code Object} type.
     * </p>
     * 
     * @param items
     *            the JsonArray value to set
     */
    protected void setItems(JsonArray items) {
        getElement().setPropertyJson("items", items);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * If {@code true}, the user can input a value that is not present in the
     * items list. {@code value} property will be set to the input value in this
     * case. Also, when {@code value} is set programmatically, the input value
     * will be set to reflect that value.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code allowCustomValue} property from the webcomponent
     */
   /* protected boolean isAllowCustomValueBoolean() {
        return getElement().getProperty("allowCustomValue", false);
    }*/

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * If {@code true}, the user can input a value that is not present in the
     * items list. {@code value} property will be set to the input value in this
     * case. Also, when {@code value} is set programmatically, the input value
     * will be set to reflect that value.
     * </p>
     * 
     * @param allowCustomValue
     *            the boolean value to set
     */
   /* protected void setAllowCustomValue(boolean allowCustomValue) {
        getElement().setProperty("allowCustomValue", allowCustomValue);
    }
*/
    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A subset of items, filtered based on the user input. Filtered items can
     * be assigned directly to omit the internal filtering functionality. The
     * items can be of either {@code String} or {@code Object} type.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code filteredItems} property from the webcomponent
     */
    protected JsonArray getFilteredItemsJsonArray() {
        return (JsonArray) getElement().getPropertyRaw("filteredItems");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A subset of items, filtered based on the user input. Filtered items can
     * be assigned directly to omit the internal filtering functionality. The
     * items can be of either {@code String} or {@code Object} type.
     * </p>
     * 
     * @param filteredItems
     *            the JsonArray value to set
     */
    protected void setFilteredItems(JsonArray filteredItems) {
        getElement().setPropertyJson("filteredItems", filteredItems);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * When set to {@code true}, &quot;loading&quot; attribute is added to host
     * and the overlay element.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code loading} property from the webcomponent
     */
    protected boolean isLoadingBoolean() {
        return getElement().getProperty("loading", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * When set to {@code true}, &quot;loading&quot; attribute is added to host
     * and the overlay element.
     * </p>
     * 
     * @param loading
     *            the boolean value to set
     */
    protected void setLoading(boolean loading) {
        getElement().setProperty("loading", loading);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Filtering string the user has typed into the input field.
     * <p>
     * This property is synchronized automatically from client side when a
     * 'filter-changed' event happens.
     * </p>
     * 
     * @return the {@code filter} property from the webcomponent
     */
    @Synchronize(property = "filter", value = "filter-changed")
    protected String getFilterString() {
        return getElement().getProperty("filter");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Filtering string the user has typed into the input field.
     * </p>
     * 
     * @param filter
     *            the String value to set
     */
    protected void setFilter(String filter) {
        getElement().setProperty("filter", filter == null ? "" : filter);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The selected item from the {@code items} array.
     * <p>
     * This property is synchronized automatically from client side when a
     * 'selected-item-changed' event happens.
     * </p>
     * 
     * @return the {@code selectedItem} property from the webcomponent
     */
    @Synchronize(property = "selectedItem", value = "selected-item-changed")
    protected JsonObject getSelectedItemJsonObject() {
        return (JsonObject) getElement().getPropertyRaw("selectedItem");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The selected item from the {@code items} array.
     * </p>
     * 
     * @param selectedItem
     *            the JsonObject value to set
     */
    protected void setSelectedItem(JsonObject selectedItem) {
        getElement().setPropertyJson("selectedItem", selectedItem);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for label of the item. If {@code items} is an array of objects, the
     * {@code itemLabelPath} is used to fetch the displayed string label for
     * each item.
     * </p>
     * <p>
     * The item label is also used for matching items when processing user
     * input, i.e., for filtering and selecting items.
     * </p>
     * <p>
     * When using item templates, the property is still needed because it is
     * used for filtering, and for displaying the selected item value in the
     * input box.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code itemLabelPath} property from the webcomponent
     */
    protected String getItemLabelPathString() {
        return getElement().getProperty("itemLabelPath");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for label of the item. If {@code items} is an array of objects, the
     * {@code itemLabelPath} is used to fetch the displayed string label for
     * each item.
     * </p>
     * <p>
     * The item label is also used for matching items when processing user
     * input, i.e., for filtering and selecting items.
     * </p>
     * <p>
     * When using item templates, the property is still needed because it is
     * used for filtering, and for displaying the selected item value in the
     * input box.
     * </p>
     * 
     * @param itemLabelPath
     *            the String value to set
     */
    protected void setItemLabelPath(String itemLabelPath) {
        getElement().setProperty("itemLabelPath",
                itemLabelPath == null ? "" : itemLabelPath);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for the value of the item. If {@code items} is an array of objects,
     * the {@code itemValuePath:} is used to fetch the string value for the
     * selected item.
     * </p>
     * <p>
     * The item value is used in the {@code value} property of the combo box, to
     * provide the form value.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code itemValuePath} property from the webcomponent
     */
    protected String getItemValuePathString() {
        return getElement().getProperty("itemValuePath");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for the value of the item. If {@code items} is an array of objects,
     * the {@code itemValuePath:} is used to fetch the string value for the
     * selected item.
     * </p>
     * <p>
     * The item value is used in the {@code value} property of the combo box, to
     * provide the form value.
     * </p>
     * 
     * @param itemValuePath
     *            the String value to set
     */
    protected void setItemValuePath(String itemValuePath) {
        getElement().setProperty("itemValuePath",
                itemValuePath == null ? "" : itemValuePath);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for the id of the item. If {@code items} is an array of objects, the
     * {@code itemIdPath} is used to compare and identify the same item in
     * {@code selectedItem} and {@code filteredItems} (items given by the
     * {@code dataProvider} callback).
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code itemIdPath} property from the webcomponent
     */
    protected String getItemIdPathString() {
        return getElement().getProperty("itemIdPath");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Path for the id of the item. If {@code items} is an array of objects, the
     * {@code itemIdPath} is used to compare and identify the same item in
     * {@code selectedItem} and {@code filteredItems} (items given by the
     * {@code dataProvider} callback).
     * </p>
     * 
     * @param itemIdPath
     *            the String value to set
     */
    protected void setItemIdPath(String itemIdPath) {
        getElement().setProperty("itemIdPath",
                itemIdPath == null ? "" : itemIdPath);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The name of this element.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code name} property from the webcomponent
     */
    protected String getNameString() {
        return getElement().getProperty("name");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The name of this element.
     * </p>
     * 
     * @param name
     *            the String value to set
     */
    protected void setName(String name) {
        getElement().setProperty("name", name == null ? "" : name);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true if the value is invalid.
     * <p>
     * This property is synchronized automatically from client side when a
     * 'invalid-changed' event happens.
     * </p>
     * 
     * @return the {@code invalid} property from the webcomponent
     */
    @Synchronize(property = "invalid", value = "invalid-changed")
    protected boolean isInvalidBoolean() {
        return getElement().getProperty("invalid", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true if the value is invalid.
     * </p>
     * 
     * @param invalid
     *            the boolean value to set
     */
    protected void setInvalid(boolean invalid) {
        getElement().setProperty("invalid", invalid);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The label for this element.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code label} property from the webcomponent
     */
    protected String getLabelString() {
        return getElement().getProperty("label");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The label for this element.
     * </p>
     * 
     * @param label
     *            the String value to set
     */
    protected void setLabel(String label) {
        getElement().setProperty("label", label == null ? "" : label);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to mark the input as required.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code required} property from the webcomponent
     */
    protected boolean isRequiredBoolean() {
        return getElement().getProperty("required", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to mark the input as required.
     * </p>
     * 
     * @param required
     *            the boolean value to set
     */
    protected void setRequired(boolean required) {
        getElement().setProperty("required", required);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to prevent the user from entering invalid input.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code preventInvalidInput} property from the webcomponent
     */
    protected boolean isPreventInvalidInputBoolean() {
        return getElement().getProperty("preventInvalidInput", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to prevent the user from entering invalid input.
     * </p>
     * 
     * @param preventInvalidInput
     *            the boolean value to set
     */
    protected void setPreventInvalidInput(boolean preventInvalidInput) {
        getElement().setProperty("preventInvalidInput", preventInvalidInput);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A pattern to validate the {@code input} with.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code pattern} property from the webcomponent
     */
    protected String getPatternString() {
        return getElement().getProperty("pattern");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A pattern to validate the {@code input} with.
     * </p>
     * 
     * @param pattern
     *            the String value to set
     */
    protected void setPattern(String pattern) {
        getElement().setProperty("pattern", pattern == null ? "" : pattern);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The error message to display when the input is invalid.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code errorMessage} property from the webcomponent
     */
    protected String getErrorMessageString() {
        return getElement().getProperty("errorMessage");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The error message to display when the input is invalid.
     * </p>
     * 
     * @param errorMessage
     *            the String value to set
     */
    protected void setErrorMessage(String errorMessage) {
        getElement().setProperty("errorMessage",
                errorMessage == null ? "" : errorMessage);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A placeholder string in addition to the label.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code placeholder} property from the webcomponent
     */
    protected String getPlaceholderString() {
        return getElement().getProperty("placeholder");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * A placeholder string in addition to the label.
     * </p>
     * 
     * @param placeholder
     *            the String value to set
     */
    protected void setPlaceholder(String placeholder) {
        getElement().setProperty("placeholder",
                placeholder == null ? "" : placeholder);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to display the clear icon which clears the input.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     * 
     * @return the {@code clearButtonVisible} property from the webcomponent
     */
    protected boolean isClearButtonVisibleBoolean() {
        return getElement().getProperty("clearButtonVisible", false);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Set to true to display the clear icon which clears the input.
     * </p>
     * 
     * @param clearButtonVisible
     *            the boolean value to set
     */
    protected void setClearButtonVisible(boolean clearButtonVisible) {
        getElement().setProperty("clearButtonVisible", clearButtonVisible);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Clears the cached pages and reloads data from dataprovider when needed.
     * </p>
     */
    protected void clearCache() {
        getElement().callFunction("clearCache");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Manually invoke existing renderer.
     * </p>
     */
    protected void render() {
        getElement().callFunction("render");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Opens the dropdown list.
     * </p>
     */
    protected void open() {
        getElement().callFunction("open");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Closes the dropdown list.
     * </p>
     */
    protected void close() {
        getElement().callFunction("close");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Reverts back to original value.
     * </p>
     */
    protected void cancel() {
        getElement().callFunction("cancel");
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Returns true if {@code value} is valid, and sets the {@code invalid} flag
     * appropriately.
     * </p>
     * <p>
     * This function is not supported by Flow because it returns a
     * <code>boolean</code>. Functions with return types different than void are
     * not supported at this moment.
     */
    @NotSupported
    protected void validate() {
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Returns true if the current input value satisfies all constraints (if
     * any)
     * </p>
     * <p>
     * You can override the {@code checkValidity} method for custom validations.
     * </p>
     */
    protected void checkValidity() {
        getElement().callFunction("checkValidity");
    }

    @DomEvent("custom-value-set")
    public static class CustomValueSetEvent<R extends GeneratedMultiComboBox<R, ?>>
            extends ComponentEvent<R> {
        private final String detail;

        public CustomValueSetEvent(R source, boolean fromClient,
                @EventData("event.detail") String detail) {
            super(source, fromClient);
            this.detail = detail;
        }

        public String getDetail() {
            return detail;
        }
    }

    /**
     * Adds a listener for {@code custom-value-set} events fired by the
     * webcomponent.
     * 
     * @param listener
     *            the listener
     * @return a {@link Registration} for removing the event listener
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Registration addCustomValueSetListener(
            ComponentEventListener<CustomValueSetEvent<R>> listener) {
        return addListener(CustomValueSetEvent.class,
                (ComponentEventListener) listener);
    }

    public static class SelectedItemChangeEvent<R extends GeneratedMultiComboBox<R, ?>>
            extends ComponentEvent<R> {
        private final JsonObject selectedItem;

        public SelectedItemChangeEvent(R source, boolean fromClient) {
            super(source, fromClient);
            this.selectedItem = source.getSelectedItemJsonObject();
        }

        public JsonObject getSelectedItem() {
            return selectedItem;
        }
    }

    /**
     * Adds a listener for {@code selected-item-changed} events fired by the
     * webcomponent.
     * 
     * @param listener
     *            the listener
     * @return a {@link Registration} for removing the event listener
     */
    protected Registration addSelectedItemChangeListener(
            ComponentEventListener<SelectedItemChangeEvent<R>> listener) {
        return getElement().addPropertyChangeListener("selectedItem",
                event -> listener.onComponentEvent(
                        new SelectedItemChangeEvent<R>((R) this,
                                event.isUserOriginated())));
    }

    public static class OpenedChangeEvent<R extends GeneratedMultiComboBox<R, ?>>
            extends ComponentEvent<R> {
        private final boolean opened;

        public OpenedChangeEvent(R source, boolean fromClient) {
            super(source, fromClient);
            this.opened = source.isOpenedBoolean();
        }

        public boolean isOpened() {
            return opened;
        }
    }

    /**
     * Adds a listener for {@code opened-changed} events fired by the
     * webcomponent.
     * 
     * @param listener
     *            the listener
     * @return a {@link Registration} for removing the event listener
     */
    protected Registration addOpenedChangeListener(
            ComponentEventListener<OpenedChangeEvent<R>> listener) {
        return getElement()
                .addPropertyChangeListener("opened",
                        event -> listener.onComponentEvent(
                                new OpenedChangeEvent<R>((R) this,
                                        event.isUserOriginated())));
    }

    public static class FilterChangeEvent<R extends GeneratedMultiComboBox<R, ?>>
            extends ComponentEvent<R> {
        private final String filter;

        public FilterChangeEvent(R source, boolean fromClient) {
            super(source, fromClient);
            this.filter = source.getFilterString();
        }

        public String getFilter() {
            return filter;
        }
    }

    /**
     * Adds a listener for {@code filter-changed} events fired by the
     * webcomponent.
     * 
     * @param listener
     *            the listener
     * @return a {@link Registration} for removing the event listener
     */
    protected Registration addFilterChangeListener(
            ComponentEventListener<FilterChangeEvent<R>> listener) {
        return getElement()
                .addPropertyChangeListener("filter",
                        event -> listener.onComponentEvent(
                                new FilterChangeEvent<R>((R) this,
                                        event.isUserOriginated())));
    }

    public static class InvalidChangeEvent<R extends GeneratedMultiComboBox<R, ?>>
            extends ComponentEvent<R> {
        private final boolean invalid;

        public InvalidChangeEvent(R source, boolean fromClient) {
            super(source, fromClient);
            this.invalid = source.isInvalidBoolean();
        }

        public boolean isInvalid() {
            return invalid;
        }
    }

    /**
     * Adds a listener for {@code invalid-changed} events fired by the
     * webcomponent.
     * 
     * @param listener
     *            the listener
     * @return a {@link Registration} for removing the event listener
     */
    protected Registration addInvalidChangeListener(
            ComponentEventListener<InvalidChangeEvent<R>> listener) {
        return getElement()
                .addPropertyChangeListener("invalid",
                        event -> listener.onComponentEvent(
                                new InvalidChangeEvent<R>((R) this,
                                        event.isUserOriginated())));
    }

    /**
     * Adds the given components as children of this component at the slot
     * 'prefix'.
     * 
     * @param components
     *            The components to add.
     * @see <a href=
     *      "https://developer.mozilla.org/en-US/docs/Web/HTML/Element/slot">MDN
     *      page about slots</a>
     * @see <a href=
     *      "https://html.spec.whatwg.org/multipage/scripting.html#the-slot-element">Spec
     *      website about slots</a>
     */
    protected void addToPrefix(Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("slot", "prefix");
            getElement().appendChild(component.getElement());
        }
    }

    /**
     * Removes the given child components from this component.
     * 
     * @param components
     *            The components to remove.
     * @throws IllegalArgumentException
     *             if any of the components is not a child of this component.
     */
    protected void remove(Component... components) {
        for (Component component : components) {
            if (getElement().equals(component.getElement().getParent())) {
                component.getElement().removeAttribute("slot");
                getElement().removeChild(component.getElement());
            } else {
                throw new IllegalArgumentException("The given component ("
                        + component + ") is not a child of this component");
            }
        }
    }

    /**
     * Removes all contents from this component, this includes child components,
     * text content as well as child elements that have been added directly to
     * this component using the {@link Element} API.
     */
    protected void removeAll() {
        getElement().getChildren()
                .forEach(child -> child.removeAttribute("slot"));
        getElement().removeAllChildren();
    }

    /**
     * Constructs a new GeneratedVaadinComboBox component with the given
     * arguments.
     * 
     * @param initialValue
     *            the initial value to set to the value
     * @param defaultValue
     *            the default value to use if the value isn't defined
     * @param elementPropertyType
     *            the type of the element property
     * @param presentationToModel
     *            a function that converts a string value to a model value
     * @param modelToPresentation
     *            a function that converts a model value to a string value
     * @param <P>
     *            the property type
     */
    public <P> GeneratedMultiComboBox(Set<T> initialValue, Set<T> defaultValue,
                                      Class<P> elementPropertyType,
                                      SerializableFunction<P, Set<T>> presentationToModel,
                                      SerializableFunction<Set<T>, P> modelToPresentation) {
        super("selectedItems", defaultValue, elementPropertyType, presentationToModel,
                modelToPresentation);
        if (initialValue != null) {
            setModelValue(initialValue, false);
            setPresentationValue(initialValue);
        }
    }

    /**
     * Constructs a new GeneratedVaadinComboBox component with the given
     * arguments.
     * 
     * @param initialValue
     *            the initial value to set to the value
     * @param defaultValue
     *            the default value to use if the value isn't defined
     * @param acceptNullValues
     *            whether <code>null</code> is accepted as a model value
     */
    public GeneratedMultiComboBox(Set<T> initialValue, Set<T> defaultValue,
                                  boolean acceptNullValues) {
        super("selectedItems", defaultValue, acceptNullValues);
        if (initialValue != null) {
            setModelValue(initialValue, false);
            setPresentationValue(initialValue);
        }
    }

    /**
     * Constructs a new GeneratedVaadinComboBox component with the given
     * arguments.
     * 
     * @param initialValue
     *            the initial value to set to the value
     * @param defaultValue
     *            the default value to use if the value isn't defined
     * @param elementPropertyType
     *            the type of the element property
     * @param presentationToModel
     *            a function that accepts this component and a property value
     *            and returns a model value
     * @param modelToPresentation
     *            a function that accepts this component and a model value and
     *            returns a property value
     * @param <P>
     *            the property type
     */
    public <P> GeneratedMultiComboBox(Set<T> initialValue, Set<T> defaultValue,
                                      Class<P> elementPropertyType,
                                      SerializableBiFunction<R, P, Set<T>> presentationToModel,
                                      SerializableBiFunction<R, Set<T>, P> modelToPresentation) {
        super("selectedItems", defaultValue, elementPropertyType, presentationToModel, modelToPresentation);
        if (initialValue != null) {
            setModelValue(initialValue, false);
            setPresentationValue(initialValue);
        }
    }

    /**
     * Default constructor.
     */
    public GeneratedMultiComboBox() {
        this(null, null, null, (SerializableFunction) null,
                (SerializableFunction) null);
    }


}
