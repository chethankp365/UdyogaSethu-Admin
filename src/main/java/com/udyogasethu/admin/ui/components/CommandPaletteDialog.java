package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.List;

public class CommandPaletteDialog extends Dialog {

    private record CommandItem(String title, String category, String route, VaadinIcon icon) {}

    private final List<CommandItem> allCommands = new ArrayList<>();
    private final VerticalLayout resultsLayout = new VerticalLayout();
    private final TextField searchField = new TextField();

    public CommandPaletteDialog() {
        addClassName("command-palette-dialog");
        setWidth("560px");
        setCloseOnOutsideClick(true);

        initCommands();

        searchField.setWidthFull();
        searchField.setPlaceholder("Type a module name or command (e.g., 'jobs', 'users', 'disputes')...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> filterCommands(e.getValue()));

        resultsLayout.setPadding(false);
        resultsLayout.setSpacing(true);
        resultsLayout.setWidthFull();

        VerticalLayout container = new VerticalLayout(searchField, resultsLayout);
        container.setPadding(true);
        container.setSpacing(true);
        add(container);

        filterCommands("");
    }

    private void initCommands() {
        allCommands.add(new CommandItem("Overview Dashboard", "Analytics", "", VaadinIcon.DASHBOARD));
        allCommands.add(new CommandItem("User Management", "Identity", "users", VaadinIcon.USERS));
        allCommands.add(new CommandItem("Worker Management", "Labour", "workers", VaadinIcon.USER_CARD));
        allCommands.add(new CommandItem("Service Providers", "Labour", "providers", VaadinIcon.BRIEFCASE));
        allCommands.add(new CommandItem("Job Management", "Labour", "jobs", VaadinIcon.WORKPLACE));
        allCommands.add(new CommandItem("Farmer Management", "Agriculture", "farmers", VaadinIcon.HOME));
        allCommands.add(new CommandItem("Produce Marketplace", "Agriculture", "marketplace", VaadinIcon.CART));
        allCommands.add(new CommandItem("Orders Management", "Commerce", "orders", VaadinIcon.PACKAGE));
        allCommands.add(new CommandItem("Payments & Transactions", "Financials", "payments", VaadinIcon.MONEY_DEPOSIT));
        allCommands.add(new CommandItem("Identity Verification", "Security", "verification", VaadinIcon.DIPLOMA_SCROLL));
        allCommands.add(new CommandItem("Reviews & Ratings", "Trust", "reviews", VaadinIcon.STAR));
        allCommands.add(new CommandItem("Complaints & Disputes", "Trust", "disputes", VaadinIcon.EXCLAMATION_CIRCLE_O));
        allCommands.add(new CommandItem("Voice Assistant Monitoring", "AI & Voice", "voice", VaadinIcon.MICROPHONE));
        allCommands.add(new CommandItem("Notifications", "Communication", "notifications", VaadinIcon.BELL));
        allCommands.add(new CommandItem("Reports & Analytics", "Analytics", "reports", VaadinIcon.CHART));
        allCommands.add(new CommandItem("Admin Management", "Administration", "admins", VaadinIcon.SHIELD));
        allCommands.add(new CommandItem("Audit Logs", "Administration", "audit", VaadinIcon.FILE_TEXT_O));
        allCommands.add(new CommandItem("Platform Settings", "Administration", "settings", VaadinIcon.COG_O));
    }

    private void filterCommands(String query) {
        resultsLayout.removeAll();
        String q = query.toLowerCase().trim();

        List<CommandItem> filtered = allCommands.stream()
                .filter(c -> c.title.toLowerCase().contains(q) || c.category.toLowerCase().contains(q))
                .limit(7)
                .toList();

        if (filtered.isEmpty()) {
            Paragraph none = new Paragraph("No matching modules found.");
            none.getStyle().set("color", "var(--us-text-muted)").set("margin", "10px");
            resultsLayout.add(none);
            return;
        }

        for (CommandItem item : filtered) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.getStyle()
                    .set("padding", "10px 14px")
                    .set("border-radius", "8px")
                    .set("cursor", "pointer")
                    .set("transition", "background 0.15s ease");

            Icon icon = item.icon.create();
            icon.setSize("18px");
            icon.setColor("var(--us-primary)");

            Span title = new Span(item.title);
            title.getStyle().set("font-weight", "600").set("color", "var(--us-text-main)");

            Span category = new Span(item.category);
            category.getStyle()
                    .set("margin-left", "auto")
                    .set("font-size", "0.75rem")
                    .set("color", "var(--us-text-muted)")
                    .set("background", "var(--us-surface-2)")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px");

            row.add(icon, title, category);
            row.addClickListener(e -> {
                close();
                UI.getCurrent().navigate(item.route);
            });

            resultsLayout.add(row);
        }
    }
}
