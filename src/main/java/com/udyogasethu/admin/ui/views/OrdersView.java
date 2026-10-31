package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Order;
import com.udyogasethu.admin.domain.OrderItem;
import com.udyogasethu.admin.service.MarketplaceService;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Route(value = "orders", layout = MainLayout.class)
@PageTitle("Orders Management | UdyogaSethu Admin")
@PermitAll
public class OrdersView extends VerticalLayout {

    private final MarketplaceService marketplaceService;
    private final Grid<Order> grid = new Grid<>(Order.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("Order Details & Items");

    public OrdersView(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;

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
        H2 title = new H2("Agri-Commerce Orders & Delivery Tracking");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("7-stage order lifecycle tracking, 4-digit handover PIN verification, and multi-farmer delivery batches.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        searchField.setPlaceholder("Search by order code...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());

        statusFilter.setPlaceholder("Filter Status");
        statusFilter.setItems("ALL", "pending", "accepted", "packed", "out_for_delivery", "delivered", "cancelled", "disputed");
        statusFilter.setValue("ALL");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout bar = new HorizontalLayout(searchField, statusFilter);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(Order::getOrderCode).setHeader("Order Code").setAutoWidth(true);
        grid.addColumn(o -> "₹" + (o.getTotalPaise() != null ? o.getTotalPaise() / 100.0 : 0)).setHeader("Total Amount").setAutoWidth(true);
        grid.addColumn(o -> "₹" + (o.getPlatformFeePaise() != null ? o.getPlatformFeePaise() / 100.0 : 0)).setHeader("Platform Fee").setAutoWidth(true);
        grid.addColumn(o -> o.getHandoverPin() != null ? "PIN: " + o.getHandoverPin() : "N/A").setHeader("Handover PIN").setAutoWidth(true);
        grid.addComponentColumn(o -> StatusChip.create(o.getStatus())).setHeader("Status").setAutoWidth(true);

        grid.addComponentColumn(o -> {
            Button btn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(o));
            btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return btn;
        }).setHeader("Inspect").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String search = searchField.getValue();
        String status = statusFilter.getValue();

        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return marketplaceService.getOrders(search, status, PageRequest.of(page, size)).stream();
                },
                query -> (int) marketplaceService.getOrders(search, status, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(Order o) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Order Code: " + o.getOrderCode()));
        content.add(new Paragraph("Customer ID: " + o.getCustomerId()));
        content.add(new Paragraph("Farmer ID: " + o.getFarmerId()));
        content.add(new Paragraph("Total: ₹" + (o.getTotalPaise() != null ? o.getTotalPaise() / 100.0 : 0)));
        content.add(new Paragraph("Status: " + o.getStatus()));
        content.add(new Paragraph("Handover PIN: " + o.getHandoverPin()));

        H4 itemsHeading = new H4("Order Line Items");
        content.add(itemsHeading);

        List<OrderItem> items = marketplaceService.getOrderItems(o.getId());
        if (items.isEmpty()) {
            content.add(new Paragraph("No line items found."));
        } else {
            for (OrderItem it : items) {
                content.add(new Paragraph("Listing ID: " + it.getListingId() + " · Qty: " + it.getQuantity() + " · Price: ₹" + (it.getTotalPricePaise() / 100.0)));
            }
        }

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
