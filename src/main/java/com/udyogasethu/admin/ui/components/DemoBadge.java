package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DemoBadge extends Span {

    public DemoBadge() {
        this("DEMO SIMULATION");
    }

    public DemoBadge(String label) {
        addClassName("demo-ribbon");
        Span icon = new Span(VaadinIcon.WARNING.create());
        icon.getStyle().set("font-size", "0.75rem");
        Span text = new Span(label);
        add(icon, text);
    }
}
