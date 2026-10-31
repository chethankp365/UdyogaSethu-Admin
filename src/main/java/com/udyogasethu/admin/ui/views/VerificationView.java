package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.KycRecord;
import com.udyogasethu.admin.service.VerificationService;
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
import com.vaadin.flow.component.html.Span;
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

@Route(value = "verification", layout = MainLayout.class)
@PageTitle("Identity Verification | UdyogaSethu Admin")
@PermitAll
public class VerificationView extends VerticalLayout {

    private final VerificationService verificationService;
    private final Grid<KycRecord> grid = new Grid<>(KycRecord.class, false);
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("KYC Document Review");

    public VerificationView(VerificationService verificationService) {
        this.verificationService = verificationService;

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
        H2 title = new H2("Identity & KYC Document Verification Queue");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Aadhaar, PAN, and Driving Licence verification queue. Biometric embeddings are strictly guarded and never rendered.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        statusFilter.setPlaceholder("Filter Status");
        statusFilter.setItems("ALL", "pending", "verified", "rejected", "review_required");
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

        grid.addColumn(KycRecord::getUserId).setHeader("User UUID").setAutoWidth(true);
        grid.addComponentColumn(k -> StatusChip.create(k.getDocType())).setHeader("Document Type").setAutoWidth(true);
        grid.addColumn(KycRecord::getDocNumberMasked).setHeader("Masked Identification").setAutoWidth(true);
        grid.addComponentColumn(k -> StatusChip.create(k.getStatus())).setHeader("Status").setAutoWidth(true);
        grid.addColumn(k -> k.getRejectionReason() != null ? k.getRejectionReason() : "—").setHeader("Rejection Notes").setAutoWidth(true);

        grid.addComponentColumn(k -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(k));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button approveBtn = new Button("Approve", VaadinIcon.CHECK.create(), e -> {
                new ConfirmReasonDialog(
                        "Approve Document Verification",
                        "Verify and grant verified trust badge to User ID: " + k.getUserId(),
                        "Confirm Approval",
                        false,
                        reason -> {
                            verificationService.reviewKyc(k.getId(), true, reason);
                            Notification.show("Document verified.");
                            refreshGrid();
                        }
                ).open();
            });
            approveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);

            Button rejectBtn = new Button("Reject", VaadinIcon.CLOSE.create(), e -> {
                new ConfirmReasonDialog(
                        "Reject Document Verification",
                        "Reject document for User ID: " + k.getUserId() + ". Provide explicit feedback reason.",
                        "Confirm Rejection",
                        true,
                        reason -> {
                            verificationService.reviewKyc(k.getId(), false, reason);
                            Notification.show("Document rejected.");
                            refreshGrid();
                        }
                ).open();
            });
            rejectBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            actions.add(inspectBtn, approveBtn, rejectBtn);
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
                    return verificationService.getKycRecords(status, PageRequest.of(page, size)).stream();
                },
                query -> (int) verificationService.getKycRecords(status, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(KycRecord k) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Record ID: " + k.getId()));
        content.add(new Paragraph("User ID: " + k.getUserId()));
        content.add(new Paragraph("Document Type: " + k.getDocType()));
        content.add(new Paragraph("Masked Number: " + k.getDocNumberMasked()));
        content.add(new Paragraph("Status: " + k.getStatus()));
        content.add(new Paragraph("Submitted: " + k.getCreatedAt()));

        Span policyNotice = new Span("BIOMETRIC COMPLIANCE: Raw face embeddings and biometric vectors are strictly isolated and never transmitted to the browser console.");
        policyNotice.getStyle().set("color", "var(--us-amber)").set("font-size", "0.75rem").set("font-weight", "600");
        content.add(policyNotice);

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
