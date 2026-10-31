package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

public class UnavailableStateCard extends Div {

    public UnavailableStateCard(String title, String explanation) {
        addClassName("setu-card");
        getStyle()
                .set("padding", "24px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("text-align", "center")
                .set("gap", "10px")
                .set("border", "1px dashed var(--us-border-subtle)")
                .set("background", "var(--us-surface-2)");

        Span icon = new Span(VaadinIcon.INFO_CIRCLE_O.create());
        icon.getStyle().set("font-size", "2rem").set("color", "var(--us-amber)");

        H4 heading = new H4(title);
        heading.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph desc = new Paragraph(explanation != null ? explanation : "Not available in the current data model.");
        desc.getStyle().set("margin", "0").set("color", "var(--us-text-muted)").set("font-size", "0.85rem");

        Span badge = new Span("GROUND TRUTH POLICY ENFORCED");
        badge.getStyle()
                .set("font-size", "0.7rem")
                .set("font-weight", "700")
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("background", "rgba(255, 138, 0, 0.15)")
                .set("color", "var(--us-amber)");

        add(icon, heading, desc, badge);
    }
}
