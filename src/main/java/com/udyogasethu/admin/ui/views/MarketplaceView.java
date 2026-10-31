package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.ProduceListing;
import com.udyogasethu.admin.service.MarketplaceService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

@Route(value = "marketplace", layout = MainLayout.class)
@PageTitle("Produce Marketplace | UdyogaSethu Admin")
@PermitAll
public class MarketplaceView extends VerticalLayout {

    private final MarketplaceService marketplaceService;
    private final Grid<ProduceListing> grid = new Grid<>(ProduceListing.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("Produce Listing Inspection");

    public MarketplaceView(MarketplaceService marketplaceService) {
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
        H2 title = new H2("Produce Marketplace & Crop Batches");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Farmer crop listings, stock quantities, Vedic/organic farming methods, and lot quality traceability.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        searchField.setPlaceholder("Search crop by name...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());

        statusFilter.setPlaceholder("Filter Status");
        statusFilter.setItems("ALL", "active", "low_stock", "sold_out", "paused");
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

        grid.addColumn(ProduceListing::getCropName).setHeader("Crop Name").setAutoWidth(true);
        grid.addComponentColumn(l -> StatusChip.create(l.getCategory())).setHeader("Category").setAutoWidth(true);
        grid.addColumn(l -> "₹" + (l.getPricePerUnitPaise() != null ? l.getPricePerUnitPaise() / 100.0 : 0) + " / " + l.getUnit()).setHeader("Price").setAutoWidth(true);
        grid.addColumn(l -> (l.getStockQty() != null ? l.getStockQty() : 0) + " " + l.getUnit()).setHeader("In Stock").setAutoWidth(true);
        grid.addColumn(l -> (l.getReservedQty() != null ? l.getReservedQty() : 0) + " " + l.getUnit()).setHeader("Reserved").setAutoWidth(true);
        grid.addComponentColumn(l -> StatusChip.create(l.getStatus())).setHeader("Status").setAutoWidth(true);

        grid.addComponentColumn(l -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(l));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button pauseBtn = new Button("Pause", VaadinIcon.PAUSE.create(), e -> {
                new ConfirmReasonDialog(
                        "Pause Crop Listing",
                        "Temporarily halt purchases for: " + l.getCropName(),
                        "Confirm Pause",
                        false,
                        reason -> {
                            marketplaceService.updateListingStatus(l.getId(), "paused", reason);
                            Notification.show("Listing paused.");
                            refreshGrid();
                        }
                ).open();
            });
            pauseBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            actions.add(inspectBtn, pauseBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String search = searchField.getValue();
        String status = statusFilter.getValue();

        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return marketplaceService.getListings(search, status, PageRequest.of(page, size)).stream();
                },
                query -> (int) marketplaceService.getListings(search, status, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(ProduceListing l) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Listing ID: " + l.getId()));
        content.add(new Paragraph("Crop Name: " + l.getCropName()));
        content.add(new Paragraph("Category: " + l.getCategory()));
        content.add(new Paragraph("Farming Method: " + l.getFarmingMethod()));
        content.add(new Paragraph("Stock: " + l.getStockQty() + " " + l.getUnit()));
        content.add(new Paragraph("Harvest Date: " + l.getHarvestDate()));

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
