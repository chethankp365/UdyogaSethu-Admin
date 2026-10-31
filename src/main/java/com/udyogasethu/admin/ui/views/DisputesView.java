package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Dispute;
import com.udyogasethu.admin.service.DisputeService;
import com.udyogasethu.admin.ui.components.ConfirmReasonDialog;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

@Route(value = "disputes", layout = MainLayout.class)
@PageTitle("Complaints & Disputes | UdyogaSethu Admin")
@PermitAll
public class DisputesView extends VerticalLayout {

    private final DisputeService disputeService;
    private final Grid<Dispute> grid = new Grid<>(Dispute.class, false);
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("Dispute & Grievance Investigation");

    public DisputesView(DisputeService disputeService) {
        this.disputeService = disputeService;

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
        H2 title = new H2("Complaints, Disputes & Escrow Holds");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Formal grievance resolution pipeline: Open → Under Review → Escalated → Resolved with escrow adjustments.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        statusFilter.setPlaceholder("Filter Status");
        statusFilter.setItems("ALL", "open", "under_review", "escalated", "resolved", "rejected");
        statusFilter.setValue("ALL");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout bar = new HorizontalLayout(statusFilter);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(Dispute::getCategory).setHeader("Dispute Category").setAutoWidth(true);
        grid.addColumn(Dispute::getReason).setHeader("Grievance Reason").setAutoWidth(true);
        grid.addComponentColumn(d -> StatusChip.create(d.getStatus())).setHeader("Status").setAutoWidth(true);
        grid.addColumn(d -> d.getRefundAmountPaise() != null ? "₹" + (d.getRefundAmountPaise() / 100.0) : "₹0").setHeader("Refund").setAutoWidth(true);
        grid.addColumn(Dispute::getCreatedAt).setHeader("Filed At").setAutoWidth(true);

        grid.addComponentColumn(d -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(d));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            if (!"resolved".equalsIgnoreCase(d.getStatus())) {
                Button resolveBtn = new Button("Resolve", VaadinIcon.CHECK_CIRCLE.create(), e -> {
                    new ConfirmReasonDialog(
                            "Resolve Dispute Case",
                            "Enter formal resolution findings and settlement outcome.",
                            "Confirm Resolution",
                            false,
                            reason -> {
                                disputeService.resolveDispute(d.getId(), "resolved", reason, 0L);
                                Notification.show("Dispute resolved.");
                                refreshGrid();
                            }
                    ).open();
                });
                resolveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);
                actions.add(inspectBtn, resolveBtn);
            } else {
                actions.add(inspectBtn);
            }

            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String status = statusFilter.getValue();
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return disputeService.getDisputes(status, PageRequest.of(page, size)).stream();
                },
                query -> (int) disputeService.getDisputes(status, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(Dispute d) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Dispute ID: " + d.getId()));
        content.add(new Paragraph("Category: " + d.getCategory()));
        content.add(new Paragraph("Complainant: " + d.getComplainantId()));
        content.add(new Paragraph("Respondent: " + d.getRespondentId()));
        content.add(new Paragraph("Reason: " + d.getReason()));
        content.add(new Paragraph("Status: " + d.getStatus()));
        content.add(new Paragraph("Resolution Notes: " + (d.getResolutionNotes() != null ? d.getResolutionNotes() : "Under active investigation.")));

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
