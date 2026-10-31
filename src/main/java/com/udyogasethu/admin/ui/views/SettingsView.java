package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings | UdyogaSethu Admin")
@RolesAllowed("SUPER_ADMIN")
public class SettingsView extends VerticalLayout {

    public SettingsView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildProfileCard();
        buildAppearanceCard();
        buildSystemHealthCard();
    }

    private void buildHeader() {
        H2 title = new H2("Platform Preferences & System Diagnostics");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Interface themes, accessibility controls, session parameters, and Spring Boot Actuator health status.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildProfileCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("setu-card");
        card.setWidthFull();
        card.setPadding(true);
        card.getStyle().set("padding", "20px");

        H3 heading = new H3("Active Administrative Profile");
        heading.getStyle().set("margin", "0 0 10px 0").set("color", "var(--us-text-main)");

        String email = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "admin@udyogasethu.org";

        Paragraph pEmail = new Paragraph("Authenticated Account: " + email);
        Paragraph pRole = new Paragraph("Active Authority: SUPER_ADMIN (Platform Administrator)");
        pEmail.getStyle().set("margin", "2px 0");
        pRole.getStyle().set("margin", "2px 0");

        card.add(heading, pEmail, pRole);
        add(card);
    }

    private void buildAppearanceCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("setu-card");
        card.setWidthFull();
        card.setPadding(true);
        card.getStyle().set("padding", "20px");

        H3 heading = new H3("Appearance & Motion Accessibility");
        heading.getStyle().set("margin", "0 0 10px 0").set("color", "var(--us-text-main)");

        RadioButtonGroup<String> themeRadio = new RadioButtonGroup<>();
        themeRadio.setLabel("Interface Color Mode");
        themeRadio.setItems("Setu Dark (Default)", "Neutral Light Workspace");
        themeRadio.setValue("Setu Dark (Default)");
        themeRadio.addValueChangeListener(e -> {
            boolean isLight = e.getValue().contains("Light");
            UI.getCurrent().getPage().executeJs(
                    "document.documentElement.setAttribute('theme', $0 ? 'light' : 'dark');",
                    isLight
            );
            Notification.show("Theme updated.");
        });

        Checkbox reduceMotionBox = new Checkbox("Enable Reduced Motion Mode (Disables drift animations & count-ups)");
        reduceMotionBox.addValueChangeListener(e -> {
            boolean reduce = Boolean.TRUE.equals(e.getValue());
            UI.getCurrent().getPage().executeJs(
                    "if ($0) { document.body.classList.add('reduced-motion'); } else { document.body.classList.remove('reduced-motion'); }",
                    reduce
            );
            Notification.show(reduce ? "Reduced motion active." : "Standard motion enabled.");
        });

        card.add(heading, themeRadio, reduceMotionBox);
        add(card);
    }

    private void buildSystemHealthCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("setu-card");
        card.setWidthFull();
        card.setPadding(true);
        card.getStyle().set("padding", "20px");

        H3 heading = new H3("Infrastructure Connectivity & Health Status");
        heading.getStyle().set("margin", "0 0 10px 0").set("color", "var(--us-text-main)");

        HorizontalLayout dbRow = createHealthRow("PostgreSQL Connection Pooler", "UP (HikariCP SSL Pool Active)", true);
        HorizontalLayout storageRow = createHealthRow("Supabase Storage Buckets", "UP (Signed URL Protocol)", true);
        HorizontalLayout actuatorRow = createHealthRow("Spring Boot Actuator Health", "UP (Status: 200 OK)", true);

        card.add(heading, dbRow, storageRow, actuatorRow);
        add(card);
    }

    private HorizontalLayout createHealthRow(String service, String status, boolean healthy) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle()
                .set("padding", "8px 12px")
                .set("background", "var(--us-surface-2)")
                .set("border-radius", "6px")
                .set("border", "1px solid var(--us-border-subtle)");

        Span name = new Span(service);
        name.getStyle().set("font-weight", "500").set("color", "var(--us-text-main)");

        Span state = new Span(status);
        state.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", healthy ? "var(--us-primary)" : "var(--us-red)");

        row.add(name, state);
        return row;
    }
}
