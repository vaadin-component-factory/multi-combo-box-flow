# 

## Licence

This component is part of Vaadin Pro. Still, open source you need to have a valid CVAL license in order to use it. Read more at: [vaadin.com/pricing](https://vaadin.com/pricing)

## Development instructions

Build the project and install the add-on locally:
```
mvn clean install
```
Starting the demo server:

Go to multi-combo-box-flow-demo and run:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

## Description 

The Multicombobox component provides support to select multiple items for a dropdown.

It's optimized to be used with a keyboard:
* Filtering selects the first non-selected item
* Enter toggles the selection
* Arrow up/down to navigate to the next/previous item.

"Select All" selects all the items.

"Clear" removes the selection

It's recommended to use the page size to activate the filtering on client side.

## How to use it

Create a new component MultiComboBox and use it like a ComboBox. The API is quite similar.

The value returned is a Set of beans.

## Examples

### Basic example

```
    public SimpleView() {
        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        add(combobox);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
```

### Item label Generator

```
    public LabelGeneratorView() {
        List<Person> personList = getItems();
        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons Phone");
        combobox.setItemLabelGenerator(person -> {
            if (person.getEmail() != null) {
                return person.getPhoneNumber();
            } else {
                return "No phone for " + person.getId();
            }
        });
        combobox.setItems(personList);
        add(combobox);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
```

### Client side filtering

You can set a page size > your list size to enable the filtering on the client side.

```

    private Span itemsSelected = new Span();

    public InMemoryView() {
        MultiComboBox<Person> combobox = new MultiComboBox<>(1000);
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        add(combobox);

        HashSet<Person> value = new HashSet<>();
        value.add(personList.get(0));
        value.add(personList.get(5));
        combobox.setValue(value);
        combobox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                itemsSelected.setText("Items selected:" + e.getValue().toString());
            } else {
                itemsSelected.setText("No item selected");
            }

        });
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
```

### Binding and validation

```
    private BackendBean backendBean = new BackendBean();
    private Binder<BackendBean> binder = new Binder<>();

    public BindingView() {

        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        HashSet<Person> value = new HashSet<>();
        value.add(personList.get(0));
        value.add(personList.get(5));
        backendBean.setPersons(value);
        add(combobox);
        binder.setBean(backendBean);
        binder.forField(combobox).asRequired().withValidator(val -> {
            return (val != null) && (val.size() == 2);
        }, "You have to select exactly 2 persons")
            .bind(BackendBean::getPersons,BackendBean::setPersons);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }

    public static class BackendBean {
        private Set<Person> persons;

        public Set<Person> getPersons() {
            return persons;
        }

        public void setPersons(Set<Person> persons) {
            this.persons = persons;
        }
    }
```

## Limitations

* Renderer does not work.
* Custom value does not work, you can't add value that are not in the list.

## Demo

You can check the demo here: https://incubator.app.fi/multi-combo-box-flow-demo/

## Missing features or bugs

You can report any issue or missing feature on github: https://github.com/vaadin-component-factory/multi-combo-box-flow