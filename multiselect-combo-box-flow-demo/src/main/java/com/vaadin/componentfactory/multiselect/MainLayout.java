package com.vaadin.componentfactory.multiselect;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        final RouterLink simple = new RouterLink("Simple view", SimpleView.class);
        final RouterLink renderer = new RouterLink("Renderer view", RendererView.class);
        final VerticalLayout menuLayout = new VerticalLayout(simple,renderer);
        addToDrawer(menuLayout);
        addToNavbar(drawerToggle);
    }

}