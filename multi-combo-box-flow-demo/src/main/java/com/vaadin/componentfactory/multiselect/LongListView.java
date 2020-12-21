package com.vaadin.componentfactory.multiselect;

import com.vaadin.componentfactory.multiselect.bean.Person;
import com.vaadin.componentfactory.multiselect.service.PersonService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Basic example with setItems
 */
@Route(value = "long", layout = MainLayout.class)
public class LongListView extends VerticalLayout {

    public LongListView() {
        MultiComboBox<String> multiComboBox = new MultiComboBox<>();

        List<String> list = IntStream.range(0, 800)
            .mapToObj(index -> "Dummy item " + index)
            .collect(Collectors.toList());
        multiComboBox.setItems(list);
        add(multiComboBox);
    }
}
