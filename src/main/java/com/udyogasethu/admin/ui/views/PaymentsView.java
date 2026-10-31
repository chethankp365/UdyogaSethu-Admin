package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.DemoTransaction;
import com.udyogasethu.admin.domain.LedgerEntry;
import com.udyogasethu.admin.domain.Payment;
import com.udyogasethu.admin.service.FinancialService;
import com.udyogasethu.admin.ui.components.ConfirmReasonDialog;
import com.udyogasethu.admin.ui.components.DemoBadge;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Payments & Financial Vault | UdyogaSethu Admin")
@RolesAllowed({"SUPER_ADMIN", "ADMIN"})
public class PaymentsView extends VerticalLayout {

    private final FinancialService financialService;
    private final Grid<Payment> paymentsGrid = new Grid<>(Payment.class, false);
    private final Grid<LedgerEntry> ledgerGrid = new Grid<>(LedgerEntry.class, false);
    private final Grid<DemoTransaction> demoGrid = new Grid<>(DemoTransaction.class, false);
    private final VerticalLayout tabContent = new VerticalLayout();

    public PaymentsView(FinancialService financialService) {
        this.financialService = financialService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildTabs();
        buildGrids();
        showPaymentsTab();
    }

    private void buildHeader() {
        H2 title = new H2("Financial Vault & Append-Only Ledger");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Gateway transactions, double-entry immutable journal entries, and strictly isolated sandbox demo payments.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildTabs() {
        Tab tabPayments = new Tab("Gateway Payments");
        Tab tabLedger = new Tab("Immutable Ledger Journal");
        Tab tabDemo = new Tab("Isolated Demo Transactions");

        Tabs tabs = new Tabs(tabPayments, tabLedger, tabDemo);
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(e -> {
            Tab sel = e.getSelectedTab();
            if (sel == tabPayments) {
                showPaymentsTab();
            } else if (sel == tabLedger) {
                showLedgerTab();
            } else {
                showDemoTab();
            }
        });

        add(tabs, tabContent);
    }

    private void buildGrids() {
        // Payments Grid
        paymentsGrid.addClassName("setu-card");
        paymentsGrid.setSizeFull();
        paymentsGrid.addColumn(Payment::getReceiptNo).setHeader("Receipt No").setAutoWidth(true);
        paymentsGrid.addColumn(p -> "₹" + (p.getAmountPaise() != null ? p.getAmountPaise() / 100.0 : 0)).setHeader("Amount").setAutoWidth(true);
        paymentsGrid.addColumn(p -> "₹" + (p.getPlatformFeePaise() != null ? p.getPlatformFeePaise() / 100.0 : 0)).setHeader("Platform Fee").setAutoWidth(true);
        paymentsGrid.addColumn(Payment::getRazorpayPaymentId).setHeader("Gateway Payment ID").setAutoWidth(true);
        paymentsGrid.addComponentColumn(p -> StatusChip.create(p.getStatus())).setHeader("Status").setAutoWidth(true);

        // Ledger Grid
        ledgerGrid.addClassName("setu-card");
        ledgerGrid.setSizeFull();
        ledgerGrid.addColumn(LedgerEntry::getId).setHeader("Journal ID").setAutoWidth(true);
        ledgerGrid.addComponentColumn(l -> {
            boolean isCredit = "credit".equalsIgnoreCase(l.getDirection());
            return isCredit ? new StatusChip("CREDIT", "active") : new StatusChip("DEBIT", "suspended");
        }).setHeader("Direction").setAutoWidth(true);
        ledgerGrid.addColumn(LedgerEntry::getBucket).setHeader("Bucket").setAutoWidth(true);
        ledgerGrid.addColumn(l -> "₹" + (l.getAmountPaise() != null ? l.getAmountPaise() / 100.0 : 0)).setHeader("Amount").setAutoWidth(true);
        ledgerGrid.addColumn(l -> "₹" + (l.getBalanceAfterPaise() != null ? l.getBalanceAfterPaise() / 100.0 : 0)).setHeader("Balance After").setAutoWidth(true);
        ledgerGrid.addColumn(LedgerEntry::getCreatedAt).setHeader("Timestamp").setAutoWidth(true);

        // Demo Grid
        demoGrid.addClassName("setu-card");
        demoGrid.setSizeFull();
        demoGrid.addColumn(DemoTransaction::getTransactionCode).setHeader("Transaction Code").setAutoWidth(true);
        demoGrid.addColumn(DemoTransaction::getScenarioName).setHeader("Scenario").setAutoWidth(true);
        demoGrid.addColumn(d -> "₹" + (d.getAmountPaise() != null ? d.getAmountPaise() / 100.0 : 0)).setHeader("Simulated Amount").setAutoWidth(true);
        demoGrid.addComponentColumn(d -> new DemoBadge("DEMO SIMULATION")).setHeader("Isolation Flag").setAutoWidth(true);
        demoGrid.addColumn(DemoTransaction::getCreatedAt).setHeader("Created At").setAutoWidth(true);
    }

    private void showPaymentsTab() {
        tabContent.removeAll();
        paymentsGrid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return financialService.getPayments(null, "ALL", PageRequest.of(page, size)).stream();
                },
                query -> (int) financialService.getPayments(null, "ALL", PageRequest.of(0, 1)).getTotalElements()
        ));
        tabContent.add(paymentsGrid);
    }

    private void showLedgerTab() {
        tabContent.removeAll();
        ledgerGrid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return financialService.getLedgerEntries(PageRequest.of(page, size)).stream();
                },
                query -> (int) financialService.getLedgerEntries(PageRequest.of(0, 1)).getTotalElements()
        ));
        tabContent.add(ledgerGrid);
    }

    private void showDemoTab() {
        tabContent.removeAll();

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 label = new H3("Sandbox Transactions (Strictly Excluded from Financial Totals)");
        label.getStyle().set("margin", "0").set("color", "var(--us-amber)");

        Button clearBtn = new Button("Clear Demo Data", VaadinIcon.TRASH.create(), e -> {
            new ConfirmReasonDialog(
                    "Purge Sandbox Demo Transactions",
                    "Permanently delete all simulated demo records from admin.demo_transactions. Real financial records will remain untouched.",
                    "Confirm Purge",
                    true,
                    reason -> {
                        financialService.clearAllDemoTransactions(reason);
                        Notification.show("Sandbox demo records purged.");
                        showDemoTab();
                    }
            ).open();
        });
        clearBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        toolbar.add(label, clearBtn);

        demoGrid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return financialService.getDemoTransactions(null, PageRequest.of(page, size)).stream();
                },
                query -> (int) financialService.getDemoTransactions(null, PageRequest.of(0, 1)).getTotalElements()
        ));

        tabContent.add(toolbar, demoGrid);
    }
}
