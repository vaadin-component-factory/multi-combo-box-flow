package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "", layout = MainLayout.class)
public class SimpleView extends VerticalLayout {


    public SimpleView() {
        MultiSelectCombobox<Person> combobox = new MultiSelectCombobox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        add(combobox);
        MultiSelectCombobox<Person> combobox2 = new MultiSelectCombobox<>();
        combobox2.setLabel("Persons email");
        combobox2.setItemLabelGenerator(person -> person.getId() + "");
        combobox2.setItems(personList);
        add(combobox2);
        HashSet<Person> value = new HashSet<>();
        value.add(personList.get(0));
        value.add(personList.get(5));
        combobox2.setValue(value);
        combobox.addValueChangeListener(e -> {
            if (e.getOldValue() != null) {
                System.out.println("Old value " + e.getOldValue().toString());
            } else {
                System.out.println("Old value NULL ");
            }

            if (e.getValue() != null) {
                System.out.println("New value " + e.getValue().toString());
            } else {
                System.out.println("New value NULL ");
            }
        });
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
