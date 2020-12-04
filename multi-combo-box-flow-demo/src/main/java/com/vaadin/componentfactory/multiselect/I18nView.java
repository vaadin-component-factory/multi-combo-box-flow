package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;

/**
 * Basic example with i18n
 */
@Route(value = "i18n", layout = MainLayout.class)
public class I18nView extends VerticalLayout {

    public I18nView() {
        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        add(combobox);
        combobox.setI18n(new MultiComboBox.MultiComboBoxI18n()
            .setClear("Vider")
            .setSelect("Tout Sélectionner")
        );
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
