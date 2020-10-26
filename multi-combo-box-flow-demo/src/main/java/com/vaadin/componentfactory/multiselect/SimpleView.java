package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "", layout = MainLayout.class)
public class SimpleView extends VerticalLayout {


    public SimpleView() {
        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        add(combobox);
        MultiComboBox<Person> combobox2 = new MultiComboBox<>();
        combobox2.setLabel("Persons Id");
        combobox2.setItemLabelGenerator(person -> person.getId() + "");
        combobox2.setItems(personList);
        add(combobox2);
        HashSet<Person> value = new HashSet<>();
        value.add(personList.get(0));
        value.add(personList.get(5));
        combobox2.setValue(value);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
