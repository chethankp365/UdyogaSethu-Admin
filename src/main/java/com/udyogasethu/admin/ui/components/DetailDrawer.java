package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DetailDrawer extends Dialog {

    private final VerticalLayout contentContainer = new VerticalLayout();
    private final H3 titleHeading = new H3();

    public DetailDrawer(String title) {
        setModal(true);
        setCloseOnOutsideClick(true);
        setWidth("520px");
        setHeight("100%");
        getElement().getThemeList().add("detail-drawer");
        addClassName("setu-detail-drawer");

        titleHeading.setText(title);
        titleHeading.getStyle().set("margin", "0").set("font-size", "1.2rem");

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout header = new HorizontalLayout(titleHeading, closeBtn);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "16px 20px").set("border-bottom", "1px solid var(--us-border-subtle)");

        contentContainer.setPadding(true);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();

        Scroller scroller = new Scroller(contentContainer);
        scroller.setSizeFull();

        VerticalLayout wrapper = new VerticalLayout(header, scroller);
        wrapper.setSizeFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);

        add(wrapper);
    }

    public void setDrawerTitle(String title) {
        titleHeading.setText(title);
    }

    public void setContent(Component component) {
        contentContainer.removeAll();
        contentContainer.add(component);
    }
}
