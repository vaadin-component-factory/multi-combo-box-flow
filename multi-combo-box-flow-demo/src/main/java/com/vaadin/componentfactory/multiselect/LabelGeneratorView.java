package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Basic example with setItems
 */
@Route(value = "label-generator", layout = MainLayout.class)
public class LabelGeneratorView extends VerticalLayout {

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
}
