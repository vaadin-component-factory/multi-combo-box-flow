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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.data.binder.HasFilterableDataProvider;
import com.vaadin.flow.data.provider.ArrayUpdater;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CompositeDataGenerator;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.DataKeyMapper;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.Rendering;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableBiPredicate;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsModule("./flow-component-renderer.js")
@JsModule("./comboBoxConnector.js")
public class MultiComboBox<T> extends GeneratedMultiComboBox<MultiComboBox<T>, T>
    implements HasSize, HasValidation,
    HasFilterableDataProvider<T, String>, HasHelper {

    private static final String PROP_INPUT_ELEMENT_VALUE = "_inputElementValue";
    private static final String PROP_SELECTED_ITEM = "selectedItem";
    private static final String PROP_VALUE = "selectedItems";
    private static final String PROP_AUTO_OPEN_DISABLED = "autoOpenDisabled";
    private Registration dataProviderListener = null;
    private boolean shouldForceServerSideFiltering = false;

    private MultiComboboxMode currentMode = MultiComboboxMode.EAGER;
    private MultiComboBoxI18n i18n;

    /**
     * A callback method for fetching items. The callback is provided with a
     * non-null string filter, offset index and limit.
     *
     * @param <T>
     *            item (bean) type in ComboBox
     */
    @FunctionalInterface
    public interface FetchItemsCallback<T> extends Serializable {

        /**
         * Returns a stream of items that match the given filter, limiting the
         * results with given offset and limit.
         *
         * @param filter
         *            a non-null filter string
         * @param offset
         *            the first index to fetch
         * @param limit
         *            the fetched item count
         * @return stream of items
         */
        public Stream<T> fetchItems(String filter, int offset, int limit);
    }

    private final class UpdateQueue implements ArrayUpdater.Update {
        private transient List<Runnable> queue = new ArrayList<>();

        private UpdateQueue(int size) {
            enqueue("$connector.updateSize", size);
        }

        @Override
        public void set(int start, List<JsonValue> items) {
            enqueue("$connector.set", start,
                items.stream().collect(JsonUtils.asArray()),
                MultiComboBox.this.lastFilter);
        }

        @Override
        public void clear(int start, int length) {
            // NO-OP
        }

        @Override
        public void commit(int updateId) {
            enqueue("$connector.confirm", updateId, MultiComboBox.this.lastFilter);
            queue.forEach(Runnable::run);
            queue.clear();
        }

        private void enqueue(String name, Serializable... arguments) {
            queue.add(() -> getElement().callJsFunction(name, arguments));
        }
    }

    /**
     * Lazy loading updater, used when calling setDataProvider()
     */
    private final ArrayUpdater arrayUpdater = new ArrayUpdater() {
        @Override
        public Update startUpdate(int sizeChange) {
            return new UpdateQueue(sizeChange);
        }

        @Override
        public void initialize() {
            // NO-OP
        }
    };

    /**
     * Predicate to check {@link MultiComboBox} items against user typed strings.
     */
    @FunctionalInterface
    public interface ItemFilter<T> extends SerializableBiPredicate<T, String> {
        @Override
        public boolean test(T item, String filterText);
    }

    private ItemLabelGenerator<T> itemLabelGenerator = String::valueOf;

    private Renderer<T> renderer;
    private boolean renderScheduled;

    // Filter set by the client when requesting data. It's sent back to client
    // together with the response so client may know for what filter data is
    // provided.
    private String lastFilter;

    private DataCommunicator<T> dataCommunicator;
    private final CompositeDataGenerator<T> dataGenerator = new CompositeDataGenerator<>();
    private Registration dataGeneratorRegistration;

    private Element template;

    private int customValueListenersCount;

    private SerializableConsumer<String> filterSlot = filter -> {
        // Just ignore when setDataProvider has not been called
    };

    private enum UserProvidedFilter {
        UNDECIDED, YES, NO
    }

    private UserProvidedFilter userProvidedFilter = UserProvidedFilter.UNDECIDED;

    /**
     * Creates an empty combo box with the defined page size for lazy loading.
     * <p>
     * The default page size is 50.
     * <p>
     * The page size is also the largest number of items that can support
     * client-side filtering. If you provide more items than the page size, the
     * component has to fall back to server-side filtering.
     *
     * @param pageSize
     *            the amount of items to request at a time for lazy loading
     */
    public MultiComboBox(int pageSize) {
        super(null, null, JsonValue.class, MultiComboBox::presentationToModel,
            MultiComboBox::modelToPresentation);
        dataGenerator.addDataGenerator((item, jsonObject) -> jsonObject
            .put("label", generateLabel(item)));

        setItemValuePath("key");
        setItemIdPath("key");
        setPageSize(pageSize);

        addAttachListener(e -> initConnector());

        runBeforeClientResponse(ui -> {
            // If user didn't provide any data, initialize with empty data set.
            if (dataCommunicator == null) {
                setItems();
            }
        });
        // sort on close
        addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                getDataProvider().refreshAll();
            }
        });
        getElement().setAttribute("theme", "vcf-multiselect-combo-box");

    }

    /**
     * Default constructor. Creates an empty combo box.
     */
    public MultiComboBox() {
        this(50);
    }

    /**
     * Creates an empty combo box with the defined label.
     *
     * @param label
     *            the label describing the combo box
     */
    public MultiComboBox(String label) {
        this();
        setLabel(label);
    }

    /**
     * Creates a combo box with the defined label and populated with the items
     * in the collection.
     *
     * @param label
     *            the label describing the combo box
     * @param items
     *            the items to be shown in the list of the combo box
     * @see #setItems(Collection)
     */
    public MultiComboBox(String label, Collection<T> items) {
        this();
        setLabel(label);
        setItems(items);
    }

    /**
     * Creates a combo box with the defined label and populated with the items
     * in the array.
     *
     * @param label
     *            the label describing the combo box
     * @param items
     *            the items to be shown in the list of the combo box
     * @see #setItems(Object...)
     */
    @SafeVarargs
    public MultiComboBox(String label, T... items) {
        this();
        setLabel(label);
        setItems(items);
    }

    private static <T> Set<T> presentationToModel(MultiComboBox<T> comboBox,
                                                  JsonValue presentation) {
        if (!(presentation instanceof JsonArray) || comboBox.dataCommunicator == null) {
            return comboBox.getEmptyValue();
        }
        JsonArray presentationArray = (JsonArray) presentation;
        Set<T> result = new HashSet<>();
        for (int i = 0; i < presentationArray.length(); i++) {
            JsonObject object = presentationArray.getObject(i);

            T data = comboBox.getKeyMapper().get(object.getString("key"));
            if (data != null) {
                result.add(data);
            }
        }
        // all the filtered items are removed from the key mapper
        // but should be still in the selectedItems
        // add all old values that are not in the key mapper (filtered items)
        // if the UI is removing a item, it should be in the keymapper
        if (comboBox.getValue() != null) {
            for (T item : comboBox.getValue()) {
                if (!comboBox.getKeyMapper().has(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private static <T> JsonArray modelToPresentation(MultiComboBox<T> comboBox,
                                                     Set<T> models) {
        return modelToPresentation(comboBox, models, String::valueOf);
    }

    private static <T> JsonArray modelToPresentation(MultiComboBox<T> comboBox,
                                                     Collection<T> models, ItemLabelGenerator<T> generateLabel) {
        if (models == null) {
            return Json.createArray();
        }
        JsonArray array = Json.createArray();
        int i = 0;
        for (T model : models) {
            JsonObject json = Json.createObject();
            String key = comboBox.getKeyMapper().key(model);
            json.put("key", key);
            json.put("label", generateLabel.apply(model));
            array.set(i, json);
            i++;
        }
        return array;
    }

    @Override
    public void setValue(Set<T> value) {
        if (dataCommunicator == null) {
            if (value == null) {
                return;
            } else {
                throw new IllegalStateException(
                    "Cannot set a value for a ComboBox without items. "
                        + "Use setItems or setDataProvider to populate "
                        + "items into the ComboBox before setting a value.");
            }
        }
        super.setValue(value);
        refreshValue();
    }

    private void refreshValue() {
        Set<T> values = getValue();
        if (values != null) {
            DataKeyMapper<T> keyMapper = getKeyMapper();
            for (T value : values) {

                if (value != null && keyMapper.has(value)) {
                    value = keyMapper.get(keyMapper.key(value));
                }

                if (value == null) {
                    getElement().setProperty(PROP_SELECTED_ITEM, null);
                    getElement().setProperty(PROP_VALUE, "");
                    getElement().setProperty(PROP_INPUT_ELEMENT_VALUE, "");
                    return;
                }

                // This ensures that the selection works even with lazy loading when the
                // item is not yet loaded
                JsonObject json = Json.createObject();
                json.put("key", keyMapper.key(value));
                dataGenerator.generateData(value, json);
                //setSelectedItem(json);

            }
        }
        getElement().setPropertyJson(PROP_VALUE, modelToPresentation(this, values, this::generateLabel));
        // refresh the label if closed
        if (!isOpened()) {
            getElement().executeJs("$0.renderLabel()",this);
        }

    }

    /**
     * Sets the TemplateRenderer responsible to render the individual items in
     * the list of possible choices of the ComboBox. It doesn't affect how the
     * selected item is rendered - that can be configured by using
     * {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     *
     * @param renderer
     *            a renderer for the items in the selection list of the
     *            ComboBox, not <code>null</code>
     *
     * Note that filtering of the ComboBox is not affected by the renderer that
     * is set here. Filtering is done on the original values and can be affected
     * by {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     */
    public void setRenderer(Renderer<T> renderer) {
        Objects.requireNonNull(renderer, "The renderer must not be null");
        this.renderer = renderer;

        if (template == null) {
            template = new Element("template");
            getElement().appendChild(template);
        }
        scheduleRender();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the label displayed for that item, which
     * you can configure with
     * {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     * <p>
     * Filtering will be handled in the client-side if the size of the data set
     * is less than the page size. To force client-side filtering with a larger
     * data set (at the cost of increased network traffic), you can increase the
     * page size with {@link #setPageSize(int)}.
     * <p>
     * Setting the items creates a new DataProvider, which in turn resets the
     * combo box's value to {@code null}. If you want to add and remove items to
     * the current item set without resetting the value, you should update the
     * previously set item collection and call
     * {@code getDataProvider().refreshAll()}.
     */
    @Override
    public void setItems(Collection<T> items) {
        setDataProvider(DataProvider.ofCollection(items));
    }

    /**
     * Sets the data items of this combo box and a filtering function for
     * defining which items are displayed when user types into the combo box.
     * <p>
     * Note that defining a custom filter will force the component to make
     * server roundtrips to handle the filtering. Otherwise it can handle
     * filtering in the client-side, if the size of the data set is less than
     * the {@link #setPageSize(int) pageSize}.
     * <p>
     * Setting the items creates a new DataProvider, which in turn resets the
     * combo box's value to {@code null}. If you want to add and remove items to
     * the current item set without resetting the value, you should update the
     * previously set item collection and call
     * {@code getDataProvider().refreshAll()}.
     *
     * @param itemFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param items
     *            the data items to display
     */
    public void setItems(ItemFilter<T> itemFilter, Collection<T> items) {
        ListDataProvider<T> listDataProvider = DataProvider.ofCollection(items);

        setDataProvider(itemFilter, listDataProvider);
    }

    /**
     * Sets the data items of this combo box and a filtering function for
     * defining which items are displayed when user types into the combo box.
     * <p>
     * Note that defining a custom filter will force the component to make
     * server roundtrips to handle the filtering. Otherwise it can handle
     * filtering in the client-side, if the size of the data set is less than
     * the {@link #setPageSize(int) pageSize}.
     * <p>
     * Setting the items creates a new DataProvider, which in turn resets the
     * combo box's value to {@code null}. If you want to add and remove items to
     * the current item set without resetting the value, you should update the
     * previously set item collection and call
     * {@code getDataProvider().refreshAll()}.
     *
     * @param itemFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param items
     *            the data items to display
     */
    public void setItems(ItemFilter<T> itemFilter,
                         @SuppressWarnings("unchecked") T... items) {
        setItems(itemFilter, Arrays.asList(items));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The filter-type of the given data provider must be String so that it can
     * handle the filters typed into the ComboBox by users. If your data
     * provider uses some other type of filter, you can provide a function which
     * converts the ComboBox's filter-string into that type via
     * {@link #setDataProvider(DataProvider, SerializableFunction)}. Another way
     * to do the same thing is to use this method with your data provider
     * converted with
     * {@link DataProvider#withConvertedFilter(SerializableFunction)}.
     * <p>
     * Changing the combo box's data provider resets its current value to
     * {@code null}.
     */
    @Override
    public void setDataProvider(DataProvider<T, String> dataProvider) {
        setDataProvider(dataProvider, SerializableFunction.identity());
    }

    /**
     * {@inheritDoc}
     * <p>
     * ComboBox triggers filtering queries based on the strings users type into
     * the field. For this reason you need to provide the second parameter, a
     * function which converts the filter-string typed by the user into
     * filter-type used by your data provider. If your data provider already
     * supports String as the filter-type, it can be used without a converter
     * function via {@link #setDataProvider(DataProvider)}.
     * <p>
     * Using this method provides the same result as using a data provider
     * wrapped with
     * {@link DataProvider#withConvertedFilter(SerializableFunction)}.
     * <p>
     * Changing the combo box's data provider resets its current value to
     * {@code null}.
     */
    @Override
    public <C> void setDataProvider(DataProvider<T, C> dataProvider,
                                    SerializableFunction<String, C> filterConverter) {
        Objects.requireNonNull(dataProvider,
            "The data provider can not be null");
        Objects.requireNonNull(filterConverter,
            "filterConverter cannot be null");

        if (userProvidedFilter == UserProvidedFilter.UNDECIDED) {
            userProvidedFilter = UserProvidedFilter.YES;
        }

        if (dataCommunicator == null) {
            dataCommunicator = new DataCommunicator<>(dataGenerator,
                arrayUpdater, data -> getElement()
                .callJsFunction("$connector.updateData", data),
                getElement().getNode());
        }

        scheduleRender();
        setValue(null);

        SerializableFunction<String, C> convertOrNull = filterText -> {
            if (filterText == null) {
                return null;
            }

            return filterConverter.apply(filterText);
        };

        SerializableConsumer<C> providerFilterSlot = dataCommunicator
            .setDataProvider(dataProvider,
                convertOrNull.apply(getFilterString()));

        filterSlot = filter -> {
            if (!Objects.equals(filter, lastFilter)) {
                providerFilterSlot.accept(convertOrNull.apply(filter));
                lastFilter = filter;
            }
        };

        shouldForceServerSideFiltering = userProvidedFilter == UserProvidedFilter.YES;
        setupDataProviderListener(dataProvider);

        userProvidedFilter = UserProvidedFilter.UNDECIDED;
    }

    private <C> void setupDataProviderListener(DataProvider<T, C> dataProvider) {
        if (dataProviderListener != null) {
            dataProviderListener.remove();
        }
        dataProviderListener = dataProvider.addDataProviderListener(e -> {
            if (e instanceof DataChangeEvent.DataRefreshEvent) {
                dataCommunicator.refresh(((DataChangeEvent.DataRefreshEvent<T>) e).getItem());
            } else {
                refreshAllData(shouldForceServerSideFiltering);
            }
        });
        refreshAllData(shouldForceServerSideFiltering);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        DataProvider<T, ?> dataProvider = getDataProvider();
        if (dataProvider != null && dataProviderListener == null) {
            setupDataProviderListener(dataProvider);
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (dataProviderListener != null) {
            dataProviderListener.remove();
            dataProviderListener = null;
        }
        super.onDetach(detachEvent);
    }

    private void refreshAllData(boolean forceServerSideFiltering) {
        setClientSideFilter(!forceServerSideFiltering && getDataProvider()
            .size(new Query<>()) <= getPageSizeDouble());

        reset();
    }

    /**
     * Sets a list data provider as the data provider of this combo box.
     * <p>
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the label displayed for that item, which
     * you can configure with
     * {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     * <p>
     * Filtering will be handled in the client-side if the size of the data set
     * is less than the page size. To force client-side filtering with a larger
     * data set (at the cost of increased network traffic), you can increase the
     * page size with {@link #setPageSize(int)}.
     * <p>
     * Changing the combo box's data provider resets its current value to
     * {@code null}.
     *
     * @param listDataProvider
     *            the list data provider to use, not <code>null</code>
     */
    public void setDataProvider(ListDataProvider<T> listDataProvider) {
        if (userProvidedFilter == UserProvidedFilter.UNDECIDED) {
            userProvidedFilter = UserProvidedFilter.NO;
        }

        // Cannot use the case insensitive contains shorthand from
        // ListDataProvider since it wouldn't react to locale changes
        ItemFilter<T> defaultItemFilter = (item,
                                                    filterText) -> generateLabel(item).toLowerCase(getLocale())
            .contains(filterText.toLowerCase(getLocale()));

        setDataProvider(defaultItemFilter, listDataProvider);
    }

    /**
     * Sets a CallbackDataProvider using the given fetch items callback and a
     * size callback.
     * <p>
     * This method is a shorthand for making a {@link CallbackDataProvider} that
     * handles a partial {@link Query Query} object.
     * <p>
     * Changing the combo box's data provider resets its current value to
     * {@code null}.
     *
     * @param fetchItems
     *            a callback for fetching items
     * @param sizeCallback
     *            a callback for getting the count of items
     * @see CallbackDataProvider
     * @see #setDataProvider(DataProvider)
     */
    public void setDataProvider(FetchItemsCallback<T> fetchItems,
                                SerializableFunction<String, Integer> sizeCallback) {
        userProvidedFilter = UserProvidedFilter.YES;
        setDataProvider(new CallbackDataProvider<>(
            q -> fetchItems.fetchItems(q.getFilter().orElse(""),
                q.getOffset(), q.getLimit()),
            q -> sizeCallback.apply(q.getFilter().orElse(""))));
    }

    /**
     * Sets a list data provider with an item filter as the data provider of
     * this combo box. The item filter is used to compare each item to the
     * filter text entered by the user.
     * <p>
     * Note that defining a custom filter will force the component to make
     * server roundtrips to handle the filtering. Otherwise it can handle
     * filtering in the client-side, if the size of the data set is less than
     * the {@link #setPageSize(int) pageSize}.
     * <p>
     * Changing the combo box's data provider resets its current value to
     * {@code null}.
     *
     * @param itemFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param listDataProvider
     *            the list data provider to use, not <code>null</code>
     */
    public void setDataProvider(ItemFilter<T> itemFilter,
                                ListDataProvider<T> listDataProvider) {
        Objects.requireNonNull(listDataProvider,
            "List data provider cannot be null");

        SerializableComparator<T> tSerializableComparator = (t1, t2) -> {
            if (getValue() == null) {
                return 0;
            }
            if (getValue().contains(t1)) {
                if (!getValue().contains(t2)) {
                    return -1;
                }
            } else {
                if (getValue().contains(t2)) {
                    return 1;
                }
            }
            return 0;
        };
        listDataProvider.setSortComparator(tSerializableComparator);
        setDataProvider(listDataProvider,
            filterText -> item -> itemFilter.test(item, filterText));
    }

    /**
     * Gets the data provider used by this ComboBox.
     *
     * @return the data provider used by this ComboBox
     */
    public DataProvider<T, ?> getDataProvider() { // NOSONAR
        if (dataCommunicator != null) {
            return dataCommunicator.getDataProvider();
        }
        return null;
    }

    /**
     * Sets the item label generator that is used to produce the strings shown
     * in the combo box for each item. By default,
     * {@link String#valueOf(Object)} is used.
     * <p>
     *
     * @param itemLabelGenerator
     *            the item label provider to use, not null
     */
    public void setItemLabelGenerator(
        ItemLabelGenerator<T> itemLabelGenerator) {
        Objects.requireNonNull(itemLabelGenerator,
            "The item label generator can not be null");
        this.itemLabelGenerator = itemLabelGenerator;
        reset();
        if (getValue() != null) {
            refreshValue();
        }
    }

    /**
     * Gets the item label generator that is used to produce the strings shown
     * in the combo box for each item.
     *
     * @return the item label generator used, not null
     */
    public ItemLabelGenerator<T> getItemLabelGenerator() {
        return itemLabelGenerator;
    }

    /**
     * Sets the page size, which is the number of items requested at a time from
     * the data provider. This does not guarantee a maximum query size to the
     * backend; when the overlay has room to render more new items than the page
     * size, multiple "pages" will be requested at once.
     * <p>
     * The page size is also the largest number of items that can support
     * client-side filtering. If you provide more items than the page size, the
     * component has to fall back to server-side filtering.
     * <p>
     * Setting the page size after the ComboBox has been rendered effectively
     * resets the component, and the current page(s) and sent over again.
     * <p>
     * The default page size is 50.
     *
     * @param pageSize
     *            the maximum number of items sent per request, should be
     *            greater than zero
     */
    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException(
                "Page size should be greater than zero.");
        }
        super.setPageSize(pageSize);
        reset();
    }

    /**
     * Gets the page size, which is the number of items fetched at a time from
     * the data provider.
     * <p>
     * The page size is also the largest number of items that can support
     * client-side filtering. If you provide more items than the page size, the
     * component has to fall back to server-side filtering.
     * <p>
     * The default page size is 50.
     *
     * @return the maximum number of items sent per request
     */
    public int getPageSize() {
        return getElement().getProperty("pageSize", 50);
    }

    @Override
    public void setOpened(boolean opened) {
        super.setOpened(opened);
    }

    /**
     * Gets the states of the drop-down.
     *
     * @return {@code true} if the drop-down is opened, {@code false} otherwise
     */
    public boolean isOpened() {
        return isOpenedBoolean();
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
    }

    /**
     * Gets the validity of the combobox output.
     * <p>
     * return true, if the value is invalid.
     *
     * @return the {@code validity} property from the component
     */
    @Override
    public boolean isInvalid() {
        return isInvalidBoolean();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        super.setErrorMessage(errorMessage);
    }

    /**
     * Gets the current error message from the combobox.
     *
     * @return the current error message
     */
    @Override
    public String getErrorMessage() {
        return getErrorMessageString();
    }

    /**
     * Enables or disables the dropdown opening automatically. If {@code false}
     * the dropdown is only opened when clicking the toggle button or pressing
     * Up or Down arrow keys.
     *
     * @param autoOpen
     *            {@code false} to prevent the dropdown from opening
     *            automatically
     */
    public void setAutoOpen(boolean autoOpen) {
        getElement().setProperty(PROP_AUTO_OPEN_DISABLED, !autoOpen);
    }

    /**
     * Gets whether dropdown will open automatically or not.
     *
     * @return {@code true} if enabled, {@code false} otherwise
     */
    public boolean isAutoOpen() {
        return !getElement().getProperty(PROP_AUTO_OPEN_DISABLED, false);
    }

    /**
     * Set the combobox to be input focused when the page loads.
     *
     * @param autofocus
     *            the boolean value to set
     */
    @Override
    public void setAutofocus(boolean autofocus) {
        super.setAutofocus(autofocus);
    }

    /**
     * Get the state for the auto-focus property of the combobox.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     *
     * @return the {@code autofocus} property from the combobox
     */
    public boolean isAutofocus() {
        return isAutofocusBoolean();
    }

    @Override
    public void setPreventInvalidInput(boolean preventInvalidInput) {
        super.setPreventInvalidInput(preventInvalidInput);
    }

    /**
     * Determines whether preventing the user from inputing invalid value.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     *
     * @return the {@code preventInvalidInput} property of the combobox
     */
    public boolean isPreventInvalidInput() {
        return isPreventInvalidInputBoolean();
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequiredIndicatorVisible(required);
    }

    /**
     * Determines whether the combobox is marked as input required.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     *
     * @return {@code true} if the input is required, {@code false} otherwise
     */
    public boolean isRequired() {
        return isRequiredBoolean();
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }

    /**
     * Gets the label of the combobox.
     *
     * @return the {@code label} property of the combobox
     */
    public String getLabel() {
        return getLabelString();
    }

    @Override
    public void setPlaceholder(String placeholder) {
        super.setPlaceholder(placeholder);
    }

    /**
     * Gets the placeholder of the combobox.
     *
     * @return the {@code placeholder} property of the combobox
     */
    public String getPlaceholder() {
        return getPlaceholderString();
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
    }

    /**
     * Gets the valid input pattern
     *
     * @return the {@code pattern} property of the combobox
     */
    public String getPattern() {
        return getPatternString();
    }

    @Override
    public Set<T> getEmptyValue() {
        return null;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        super.setRequiredIndicatorVisible(requiredIndicatorVisible);
        runBeforeClientResponse(ui -> getElement().callJsFunction(
            "$connector.enableClientValidation",
            !requiredIndicatorVisible));
    }

    /**
     * Allows displaying a clear button in the combo box when a value is
     * selected.
     * <p>
     * The clear button is an icon, which can be clicked to set the combo box
     * value to {@code null}.
     *
     * @param clearButtonVisible
     *            {@code true} to display the clear button, {@code false} to
     *            hide it
     */
    @Override
    public void setClearButtonVisible(boolean clearButtonVisible) {
        super.setClearButtonVisible(clearButtonVisible);
    }

    /**
     * Gets whether this combo box displays a clear button when a value is
     * selected.
     *
     * @return {@code true} if this combo box displays a clear button,
     *         {@code false} otherwise
     * @see #setClearButtonVisible(boolean)
     */
    public boolean isClearButtonVisible() {
        return super.isClearButtonVisibleBoolean();
    }

    CompositeDataGenerator<T> getDataGenerator() {
        return dataGenerator;
    }

    private String generateLabel(T item) {
        if (item == null) {
            return "";
        }
        String label = getItemLabelGenerator().apply(item);
        if (label == null) {
            throw new IllegalStateException(String.format(
                "Got 'null' as a label value for the item '%s'. "
                    + "'%s' instance may not return 'null' values",
                item, ItemLabelGenerator.class.getSimpleName()));
        }
        return label;
    }

    private void scheduleRender() {
        if (renderScheduled || dataCommunicator == null || renderer == null) {
            return;
        }
        renderScheduled = true;
        runBeforeClientResponse(ui -> {
            if (dataGeneratorRegistration != null) {
                dataGeneratorRegistration.remove();
                dataGeneratorRegistration = null;
            }
            Rendering<T> rendering = renderer.render(getElement(),
                dataCommunicator.getKeyMapper(), template);
            if (rendering.getDataGenerator().isPresent()) {
                dataGeneratorRegistration = dataGenerator
                    .addDataGenerator(rendering.getDataGenerator().get());
            }
            reset();
        });
    }

    @ClientCallable
    private void confirmUpdate(int id) {
        dataCommunicator.confirmUpdate(id);
    }

    @ClientCallable
    private void setRequestedRange(int start, int length, String filter) {
        dataCommunicator.setRequestedRange(start, length);
        filterSlot.accept(filter);
        // Send (possibly updated) key for the selected values
        getElement().setPropertyJson(PROP_VALUE, modelToPresentation(this, getValue(), this::generateLabel));
    }

    @ClientCallable
    private void resetDataCommunicator() {
        dataCommunicator.reset();
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
            .beforeClientResponse(this, context -> command.accept(ui)));
    }

    private void initConnector() {
        getElement().executeJs(
            "window.Vaadin.Flow.comboBoxConnector.initLazy(this)");
    }

    private DataKeyMapper<T> getKeyMapper() {
        return dataCommunicator.getKeyMapper();
    }

    private void setClientSideFilter(boolean clientSideFilter) {
        getElement().setProperty("_clientSideFilter", clientSideFilter);
    }

    private void reset() {
        lastFilter = null;
        if (dataCommunicator != null) {
            dataCommunicator.setRequestedRange(0, 0);
            dataCommunicator.reset();
        }
        runBeforeClientResponse(ui -> ui.getPage().executeJs(
            // If-statement is needed because on the first attach this
            // JavaScript is called before initializing the connector.
            "if($0.$connector) $0.$connector.reset();", getElement()));
    }

    @ClientCallable
    private void selectAll() {
        setValue(getDataProvider().fetch(new Query<>()).collect(Collectors.toSet()));
    }

    public MultiComboboxMode getValueChangeMode() {
        return this.currentMode;
    }

    /**
     * Sets new value change mode for the component.
     * By default the component is in EAGER mode
     *  = propagate the changes when an item is selected
     * In LAZY_AND_CLIENT_SIDE_FILTERING mode the value is propagated when the item is closed
     * It requires a filtering on the client side
     *
     *
     * @param multiComboboxMode
     *            new value change mode, or {@code null} to disable the value
     *            synchronization
     */
    public void setComponentModeValueChangeMode(MultiComboboxMode multiComboboxMode) {
        this.currentMode = multiComboboxMode;
        if (MultiComboboxMode.EAGER == multiComboboxMode) {
            this.setSynchronizedEvent("selected-items-changed");
        } else if (MultiComboboxMode.LAZY_AND_CLIENT_SIDE_FILTERING == multiComboboxMode) {
            this.setSynchronizedEvent("on-close");
            this.setPageSize(Integer.MAX_VALUE);
        } else {
            throw new UnsupportedOperationException("valueChangeMode should be EAGER or LAZY");
        }
    }


    public enum MultiComboboxMode {
        EAGER,
        LAZY_AND_CLIENT_SIDE_FILTERING
    }

    /**
     * Gets the internationalization object previously set for this component.
     * <p>
     * Note: updating the object content that is gotten from this method will
     * not update the lang on the component if not set back using
     * {@link MultiComboBox#setI18n(MultiComboBoxI18n)}
     *
     * @return the i18n object. It will be <code>null</code>, If the i18n
     *         properties weren't set.
     */
    public MultiComboBoxI18n getI18n() {
        return i18n;
    }

    /**
     * Sets the internationalization properties for this component.
     *
     * @param i18n
     *            the internationalized properties, not <code>null</code>
     */
    public void setI18n(MultiComboBoxI18n i18n) {
        Objects.requireNonNull(i18n,
            "The I18N properties object should not be null");
        this.i18n = i18n;
        setI18nWithJS();
    }

    private void setI18nWithJS() {
        runBeforeClientResponse(ui -> {
            JsonObject i18nObject = (JsonObject) JsonSerializer.toJson(i18n);
            for (String key : i18nObject.keys()) {
                getElement().executeJs("this.set('i18n." + key + "', $0)",
                    i18nObject.get(key));
            }
        });
    }

    /**
     * The internationalization properties for {@link MultiComboBox}.
     */
    public static class MultiComboBoxI18n implements Serializable {
        private String select;
        private String clear;

        public String getSelect() {
            return select;
        }

        public MultiComboBoxI18n setSelect(String select) {
            this.select = select;
            return this;
        }

        public String getClear() {
            return clear;
        }

        public MultiComboBoxI18n setClear(String clear) {
            this.clear = clear;
            return this;
        }
    }
}
