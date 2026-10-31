package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.FarmerProfile;
import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.service.UserService;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Route(value = "farmers", layout = MainLayout.class)
@PageTitle("Farmer Management | UdyogaSethu Admin")
@PermitAll
public class FarmersView extends VerticalLayout {

    private final UserService userService;
    private final Grid<FarmerProfile> grid = new Grid<>(FarmerProfile.class, false);
    private final DetailDrawer detailDrawer = new DetailDrawer("Farmer Profile & Land Records");

    public FarmersView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("Farmer & Agricultural Producers");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Inspection of farm clusters, acreage, certified organic documentation, and delivery zones.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(f -> {
            Optional<Profile> p = userService.getUserById(f.getUserId());
            return p.map(Profile::getFullName).orElse(f.getUserId().toString());
        }).setHeader("Farmer Name").setAutoWidth(true);

        grid.addColumn(FarmerProfile::getFarmName).setHeader("Farm Name").setAutoWidth(true);
        grid.addColumn(FarmerProfile::getClusterName).setHeader("Agricultural Cluster").setAutoWidth(true);
        grid.addColumn(f -> f.getTotalLandAcres() != null ? f.getTotalLandAcres() + " Acres" : "N/A").setHeader("Land Holding").setAutoWidth(true);
        grid.addColumn(f -> f.getDeliveryRadiusKm() != null ? f.getDeliveryRadiusKm() + " km" : "N/A").setHeader("Delivery Radius").setAutoWidth(true);

        grid.addComponentColumn(f -> {
            boolean hasCert = f.getOrganicCertNumber() != null && !f.getOrganicCertNumber().isEmpty();
            return hasCert ? new StatusChip("ORGANIC", "active") : new StatusChip("CONVENTIONAL", "pending");
        }).setHeader("Certification").setAutoWidth(true);

        grid.addComponentColumn(f -> {
            Button btn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(f));
            btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return btn;
        }).setHeader("Inspect").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return userService.getFarmerProfiles(PageRequest.of(page, size)).stream();
                },
                query -> (int) userService.getFarmerProfiles(PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(FarmerProfile f) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("User ID: " + f.getUserId()));
        content.add(new Paragraph("Farm Name: " + f.getFarmName()));
        content.add(new Paragraph("Cluster: " + f.getClusterName()));
        content.add(new Paragraph("Land Holding: " + f.getTotalLandAcres() + " Acres"));
        content.add(new Paragraph("Delivery Radius: " + f.getDeliveryRadiusKm() + " km"));
        content.add(new Paragraph("Pickup Available: " + f.getPickupAvailable()));
        content.add(new Paragraph("Organic Certificate Number: " + (f.getOrganicCertNumber() != null ? f.getOrganicCertNumber() : "None registered")));

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
