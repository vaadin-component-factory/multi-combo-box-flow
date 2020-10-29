package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Basic example with setItems
 */
@Route(value = "binding", layout = MainLayout.class)
public class BindingView extends VerticalLayout {

    private BackendBean backendBean = new BackendBean();
    private Binder<BackendBean> binder = new Binder<>();

    private BackendBean backendBeanLazy = new BackendBean();
    private Binder<BackendBean> binderLazy = new Binder<>();

    public BindingView() {

        MultiComboBox<Person> combobox = new MultiComboBox<>();
        combobox.setLabel("Persons in EAGER Mode");
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

        MultiComboBox<Person> comboboxLazy = new MultiComboBox<>();
        comboboxLazy.setLabel("Persons in LAZY Mode");
        comboboxLazy.setItems(personList);
        add(comboboxLazy);
        comboboxLazy.setComponentModeValueChangeMode(MultiComboBox.MultiComboboxMode.LAZY_AND_CLIENT_SIDE_FILTERING);
        binderLazy.setBean(backendBeanLazy);
        binderLazy.forField(comboboxLazy).asRequired().withValidator(val -> {
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
}
