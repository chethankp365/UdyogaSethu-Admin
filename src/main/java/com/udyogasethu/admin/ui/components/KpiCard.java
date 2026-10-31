package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class KpiCard extends Div {

    public KpiCard(String title, String value, String subtitle, VaadinIcon iconType, String accentColor) {
        addClassName("setu-card");
        getStyle()
                .set("padding", "20px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "10px")
                .set("min-width", "220px")
                .set("flex", "1");

        Div headerRow = new Div();
        headerRow.getStyle().set("display", "flex").set("justify-content", "space-between").set("align-items", "center");

        Paragraph titleLabel = new Paragraph(title);
        titleLabel.getStyle()
                .set("margin", "0")
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", "var(--us-text-muted)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em");

        if (iconType != null) {
            Icon icon = iconType.create();
            icon.setSize("20px");
            icon.setColor(accentColor != null ? accentColor : "var(--us-primary)");
            headerRow.add(titleLabel, icon);
        } else {
            headerRow.add(titleLabel);
        }

        H3 number = new H3(value);
        number.addClassName("kpi-metric-number");
        number.getStyle().set("margin", "0");

        Span sub = new Span(subtitle);
        sub.getStyle()
                .set("font-size", "0.75rem")
                .set("color", "var(--us-text-muted)");

        add(headerRow, number, sub);
    }
}
