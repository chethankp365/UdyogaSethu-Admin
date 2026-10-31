package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.AuditLog;
import com.udyogasethu.admin.service.AuditLogService;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

@Route(value = "audit", layout = MainLayout.class)
@PageTitle("Audit Logs | UdyogaSethu Admin")
@RolesAllowed({"SUPER_ADMIN", "ADMIN"})
public class AuditView extends VerticalLayout {

    private final AuditLogService auditLogService;
    private final Grid<AuditLog> grid = new Grid<>(AuditLog.class, false);
    private final TextField searchField = new TextField();
    private final DetailDrawer detailDrawer = new DetailDrawer("Security Audit Event Inspection");

    public AuditView(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildFilterBar();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("Immutable Security & Operational Audit Log");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Append-only cryptographic timeline. Trigger `trg_prevent_audit_mutation` mathematically blocks all UPDATE and DELETE attempts.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        searchField.setPlaceholder("Filter by admin email, action code, or target entity...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("380px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout bar = new HorizontalLayout(searchField);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(AuditLog::getAdminEmail).setHeader("Administrator").setAutoWidth(true);
        grid.addComponentColumn(l -> StatusChip.create(l.getAction())).setHeader("Action Code").setAutoWidth(true);
        grid.addColumn(AuditLog::getTargetType).setHeader("Target Type").setAutoWidth(true);
        grid.addColumn(AuditLog::getTargetId).setHeader("Target ID").setAutoWidth(true);
        grid.addColumn(l -> l.getReason() != null ? l.getReason() : "—").setHeader("Mandatory Audit Reason").setAutoWidth(true);
        grid.addColumn(AuditLog::getCreatedAt).setHeader("Timestamp (UTC)").setAutoWidth(true);

        grid.addComponentColumn(l -> {
            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(l));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return inspectBtn;
        }).setHeader("Inspect").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String search = searchField.getValue();
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return auditLogService.getAuditLogs(search, PageRequest.of(page, size)).stream();
                },
                query -> (int) auditLogService.getAuditLogs(search, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(AuditLog l) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Audit Entry UUID: " + l.getId()));
        content.add(new Paragraph("Admin User: " + l.getAdminEmail()));
        content.add(new Paragraph("Action: " + l.getAction()));
        content.add(new Paragraph("Target Entity: " + l.getTargetType() + " / " + l.getTargetId()));
        content.add(new Paragraph("Reason: " + l.getReason()));
        content.add(new Paragraph("Timestamp: " + l.getCreatedAt()));

        H4 diffHeading = new H4("State Mutation Diff (JSON)");
        content.add(diffHeading);

        Pre pre = new Pre(l.getDiffJson() != null ? l.getDiffJson() : "No state mutation diff captured.");
        pre.getStyle()
                .set("background", "rgba(0, 0, 0, 0.4)")
                .set("padding", "12px")
                .set("border-radius", "6px")
                .set("border", "1px solid var(--us-border-subtle)")
                .set("font-family", "var(--font-mono)")
                .set("font-size", "0.8rem")
                .set("white-space", "pre-wrap");
        content.add(pre);

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
