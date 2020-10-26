package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "lazy", layout = MainLayout.class)
public class LazyDataProviderView extends VerticalLayout {

    private Span itemsSelected = new Span();
    private PersonService personService = new PersonService();

    public LazyDataProviderView() {
        MultiComboBox<Person> combobox = new MultiComboBox<>(1000);
        combobox.setLabel("Persons");
        combobox.setDataProvider(personService::fetch, personService::count);
        add(combobox);
        combobox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                itemsSelected.setText("Items selected:" + e.getValue().toString());
            } else {
                itemsSelected.setText("No item selected");
            }

        });
    }

}
