package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "in-memory", layout = MainLayout.class)
public class InMemoryView extends VerticalLayout {

    private Span itemsSelected = new Span();

    public InMemoryView() {
        MultiSelectCombobox<Person> combobox = new MultiSelectCombobox<>(1000);
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
}
