package com.vaadin.componentfactory.multiselect;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        final RouterLink simple = new RouterLink("Simple view", SimpleView.class);
        final RouterLink i18n = new RouterLink("I18n view", I18nView.class);
        final RouterLink inMemoryView = new RouterLink("InMemory view", InMemoryView.class);
        final RouterLink bindingView = new RouterLink("Binding view", BindingView.class);
        final RouterLink rendererView = new RouterLink("Renderer view", RendererView.class);
        final RouterLink labelGeneratorView = new RouterLink("LabelGenerator view", LabelGeneratorView.class);
        final RouterLink lazyDataProviderView = new RouterLink("LazyDataProvider view", LazyDataProviderView.class);
        final VerticalLayout menuLayout = new VerticalLayout(simple, i18n, inMemoryView, bindingView, rendererView,
            labelGeneratorView, lazyDataProviderView);
        addToDrawer(menuLayout);
        addToNavbar(drawerToggle);
    }

}