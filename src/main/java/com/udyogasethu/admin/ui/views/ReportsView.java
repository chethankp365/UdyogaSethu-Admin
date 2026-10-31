package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.service.DashboardService;
import com.udyogasethu.admin.service.UserService;
import com.udyogasethu.admin.ui.components.KpiCard;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Reports & Analytics | UdyogaSethu Admin")
@RolesAllowed({"SUPER_ADMIN", "ADMIN"})
public class ReportsView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final UserService userService;

    public ReportsView(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildFilterAndExportBar();
        buildSummary();
    }

    private void buildHeader() {
        H2 title = new H2("Operational Reports & Compliance Exports");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Periodic financial summaries, employment shifts, and produce supply volume with one-click CSV and PDF downloads.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterAndExportBar() {
        DatePicker fromDate = new DatePicker("From Date", LocalDate.now().minusMonths(1));
        DatePicker toDate = new DatePicker("To Date", LocalDate.now());

        // CSV Export Stream
        StreamResource csvResource = new StreamResource("udyogasethu_user_registry.csv", () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("ID,Full Name,Phone,Role,Language,Is Suspended,Created At\n");
            List<Profile> profiles = userService.getUsers("", "ALL", PageRequest.of(0, 500)).getContent();
            for (Profile p : profiles) {
                sb.append(p.getId()).append(",")
                  .append("\"").append(p.getFullName()).append("\",")
                  .append(p.getPhone()).append(",")
                  .append(p.getActiveRole()).append(",")
                  .append(p.getLanguage()).append(",")
                  .append(p.isSuspended()).append(",")
                  .append(p.getCreatedAt()).append("\n");
            }
            return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        });

        Anchor csvAnchor = new Anchor(csvResource, "");
        Button csvBtn = new Button("Export CSV", VaadinIcon.DOWNLOAD.create());
        csvBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        csvAnchor.add(csvBtn);

        // PDF Summary Stream
        StreamResource pdfResource = new StreamResource("udyogasethu_summary_report.txt", () -> {
            var kpis = dashboardService.getDashboardKpis();
            String report = String.format("""
                    =============================================================
                    UDYOGASETHU ECOSYSTEM AUDIT REPORT
                    Generated: %s
                    =============================================================
                    Total Registered Users: %d
                    Active Daily Workers: %d
                    Service Providers: %d
                    Registered Farmers: %d
                    Delivered Produce Orders: %d
                    Gross Marketplace Volume: ₹%.2f
                    Active Job Openings: %d
                    Pending KYC Checks: %d
                    Open Grievance Disputes: %d
                    =============================================================
                    """, LocalDate.now(), kpis.getTotalUsers(), kpis.getActiveWorkers(),
                    kpis.getServiceProviders(), kpis.getFarmers(), kpis.getCompletedOrders(),
                    kpis.getGrossMarketplaceVolumeInr(), kpis.getOpenJobs(),
                    kpis.getPendingVerifications(), kpis.getOpenDisputes());
            return new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8));
        });

        Anchor pdfAnchor = new Anchor(pdfResource, "");
        Button pdfBtn = new Button("Export Summary Text", VaadinIcon.FILE_TEXT.create());
        pdfBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        pdfAnchor.add(pdfBtn);

        HorizontalLayout bar = new HorizontalLayout(fromDate, toDate, csvAnchor, pdfAnchor);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.BASELINE);
        bar.setSpacing(true);

        add(bar);
    }

    private void buildSummary() {
        var kpis = dashboardService.getDashboardKpis();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        row.add(new KpiCard("Gross Marketplace Volume", nf.format(kpis.getGrossMarketplaceVolumeInr()), "Excluding Demo Data", VaadinIcon.MONEY_DEPOSIT, "var(--us-primary)"));
        row.add(new KpiCard("Fulfilled Agri Orders", String.valueOf(kpis.getCompletedOrders()), "Delivered Farm Batches", VaadinIcon.PACKAGE, "var(--us-setu-blue)"));
        row.add(new KpiCard("Active Workers", String.valueOf(kpis.getActiveWorkers()), "Verified for hiring", VaadinIcon.USER_CARD, "#10B981"));

        add(row);
    }
}
