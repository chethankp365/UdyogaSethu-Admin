package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.AuditLog;
import com.udyogasethu.admin.service.AuditLogService;
import com.udyogasethu.admin.service.DashboardService;
import com.udyogasethu.admin.ui.components.EChartComponent;
import com.udyogasethu.admin.ui.components.KpiCard;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Overview Dashboard | UdyogaSethu Admin")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final AuditLogService auditLogService;

    public DashboardView(DashboardService dashboardService, AuditLogService auditLogService) {
        this.dashboardService = dashboardService;
        this.auditLogService = auditLogService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildKpiRow();
        buildChartsRow();
        buildActivityRow();
    }

    private void buildHeader() {
        VerticalLayout titleBox = new VerticalLayout();
        titleBox.setPadding(false);
        titleBox.setSpacing(false);

        H2 heading = new H2("System Overview & Live Pulse");
        heading.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Live telemetry and cross-domain operational metrics across Karnataka & Tamil Nadu clusters.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        titleBox.add(heading, sub);
        add(titleBox);
    }

    private void buildKpiRow() {
        DashboardService.DashboardKpis kpis = dashboardService.getDashboardKpis();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidthFull();
        row1.setSpacing(true);

        row1.add(new KpiCard("Total Users", String.valueOf(kpis.getTotalUsers()), "All registered accounts", VaadinIcon.USERS, "var(--us-primary)"));
        row1.add(new KpiCard("Active Workers", String.valueOf(kpis.getActiveWorkers()), "Seekers, informal & students", VaadinIcon.USER_CARD, "var(--us-primary)"));
        row1.add(new KpiCard("Providers", String.valueOf(kpis.getServiceProviders()), "Employers & households", VaadinIcon.BRIEFCASE, "var(--us-setu-blue)"));
        row1.add(new KpiCard("Farmers", String.valueOf(kpis.getFarmers()), "Producers & dairy clusters", VaadinIcon.HOME, "#10B981"));

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.setSpacing(true);

        row2.add(new KpiCard("Open Jobs", String.valueOf(kpis.getOpenJobs()), "Actively recruiting slots", VaadinIcon.WORKPLACE, "var(--us-amber)"));
        row2.add(new KpiCard("Delivered Orders", String.valueOf(kpis.getCompletedOrders()), "Fulfilled produce batches", VaadinIcon.PACKAGE, "var(--us-primary)"));
        row2.add(new KpiCard("Gross Marketplace Volume", currencyFormat.format(kpis.getGrossMarketplaceVolumeInr()), "Excluding demo sandbox", VaadinIcon.MONEY_DEPOSIT, "#38BDF8"));
        row2.add(new KpiCard("Action Items", String.valueOf(kpis.getPendingVerifications() + kpis.getOpenDisputes()), "Pending KYC & open disputes", VaadinIcon.EXCLAMATION_CIRCLE_O, "var(--us-red)"));

        add(row1, row2);
    }

    private void buildChartsRow() {
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        chartsLayout.setSpacing(true);

        // Chart 1: Platform Growth & Monthly Volume
        String lineChartJson = """
        {
            "title": {"text": "30-Day Platform Activity Trajectory", "textStyle": {"color": "#0F172A", "fontSize": 14}},
            "tooltip": {"trigger": "axis"},
            "grid": {"left": "3%", "right": "4%", "bottom": "3%", "containLabel": true},
            "xAxis": {"type": "category", "boundaryGap": false, "data": ["Week 1", "Week 2", "Week 3", "Week 4", "Current"], "axisLine": {"lineStyle": {"color": "#94A3B8"}}},
            "yAxis": {"type": "value", "splitLine": {"lineStyle": {"color": "#E2E8F0"}}, "axisLine": {"lineStyle": {"color": "#94A3B8"}}},
            "series": [
                {
                    "name": "Job Applications",
                    "type": "line",
                    "smooth": true,
                    "itemStyle": {"color": "#16A34A"},
                    "areaStyle": {"color": "rgba(22, 163, 74, 0.15)"},
                    "data": [12, 28, 45, 62, 85]
                },
                {
                    "name": "Produce Orders",
                    "type": "line",
                    "smooth": true,
                    "itemStyle": {"color": "#0284C7"},
                    "areaStyle": {"color": "rgba(2, 132, 199, 0.15)"},
                    "data": [5, 14, 26, 42, 60]
                }
            ]
        }
        """;
        EChartComponent lineChart = new EChartComponent(lineChartJson, "320px");
        lineChart.getStyle().set("flex", "2");

        // Chart 2: User Role Distribution Donut
        DashboardService.DashboardKpis kpis = dashboardService.getDashboardKpis();
        String donutChartJson = String.format("""
        {
            "title": {"text": "Ecosystem User Split", "textStyle": {"color": "#0F172A", "fontSize": 14}},
            "tooltip": {"trigger": "item"},
            "series": [
                {
                    "name": "User Role",
                    "type": "pie",
                    "radius": ["50%%", "75%%"],
                    "avoidLabelOverlap": false,
                    "itemStyle": {"borderRadius": 8, "borderColor": "#FFFFFF", "borderWidth": 2},
                    "label": {"show": false, "position": "center"},
                    "data": [
                        {"value": %d, "name": "Job Seekers / Workers", "itemStyle": {"color": "#16A34A"}},
                        {"value": %d, "name": "Service Providers", "itemStyle": {"color": "#0284C7"}},
                        {"value": %d, "name": "Farmers & Agri", "itemStyle": {"color": "#F59E0B"}}
                    ]
                }
            ]
        }
        """, Math.max(kpis.getActiveWorkers(), 1), Math.max(kpis.getServiceProviders(), 1), Math.max(kpis.getFarmers(), 1));
        EChartComponent donutChart = new EChartComponent(donutChartJson, "320px");
        donutChart.getStyle().set("flex", "1");

        chartsLayout.add(lineChart, donutChart);
        add(chartsLayout);
    }

    private void buildActivityRow() {
        VerticalLayout activityCard = new VerticalLayout();
        activityCard.addClassName("setu-card");
        activityCard.setWidthFull();
        activityCard.setPadding(true);
        activityCard.getStyle().set("padding", "20px");

        H3 title = new H3("Recent Real-Time Administrative Audit Trail");
        title.getStyle().set("margin", "0 0 12px 0").set("font-size", "1.1rem").set("color", "var(--us-text-main)");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(true);

        auditLogService.getAuditLogs("", PageRequest.of(0, 5)).getContent().forEach(log -> {
            HorizontalLayout item = new HorizontalLayout();
            item.setWidthFull();
            item.setAlignItems(FlexComponent.Alignment.CENTER);
            item.getStyle()
                    .set("padding", "8px 12px")
                    .set("border-radius", "6px")
                    .set("background", "var(--us-surface-2)")
                    .set("border", "1px solid var(--us-border-subtle)");

            StatusChip chip = StatusChip.create(log.getAction());
            Span text = new Span(log.getAdminEmail() + " performed " + log.getAction() + " on " + log.getTargetType() + " (" + log.getTargetId() + ")");
            text.getStyle().set("font-size", "0.85rem").set("color", "var(--us-text-main)");

            Span time = new Span(log.getCreatedAt().toString());
            time.getStyle().set("margin-left", "auto").set("font-size", "0.75rem").set("color", "var(--us-text-muted)");

            item.add(chip, text, time);
            list.add(item);
        });

        if (list.getComponentCount() == 0) {
            Paragraph empty = new Paragraph("No administrative actions recorded yet.");
            empty.getStyle().set("color", "var(--us-text-muted)");
            list.add(empty);
        }

        activityCard.add(title, list);
        add(activityCard);
    }
}
