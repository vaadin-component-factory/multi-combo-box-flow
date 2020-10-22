package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;

/**
 * Basic example with setItems
 */
@Uses(Button.class)
@Route(value = "renderer", layout = MainLayout.class)
public class RendererView extends Div {


    public RendererView() {
        MultiSelectCombobox<Person> combobox = new MultiSelectCombobox<>();
        combobox.setLabel("Persons");
        List<Person> personList = getItems();
        combobox.setItems(personList);
        combobox.setRenderer(TemplateRenderer.<Person>of("<vaadin-button>[[item.name]]</vaadin-button>")
            .withProperty("name", Person::getLastName));
        add(combobox);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
