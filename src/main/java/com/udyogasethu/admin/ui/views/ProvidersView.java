package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.domain.ProviderProfile;
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

@Route(value = "providers", layout = MainLayout.class)
@PageTitle("Service Providers | UdyogaSethu Admin")
@PermitAll
public class ProvidersView extends VerticalLayout {

    private final UserService userService;
    private final Grid<ProviderProfile> grid = new Grid<>(ProviderProfile.class, false);
    private final DetailDrawer detailDrawer = new DetailDrawer("Provider Profile Inspection");

    public ProvidersView(UserService userService) {
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
        H2 title = new H2("Service Providers & Employers");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Contractors, small businesses, and household employers publishing jobs on the network.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(p -> {
            Optional<Profile> pr = userService.getUserById(p.getUserId());
            return pr.map(Profile::getFullName).orElse(p.getUserId().toString());
        }).setHeader("Provider Name").setAutoWidth(true);

        grid.addComponentColumn(p -> StatusChip.create(p.getProviderType())).setHeader("Type").setAutoWidth(true);
        grid.addColumn(ProviderProfile::getTypicalCrewSize).setHeader("Typical Crew Size").setAutoWidth(true);

        grid.addComponentColumn(p -> {
            boolean isVerified = Boolean.TRUE.equals(p.getIsVerifiedBadge());
            return isVerified ? new StatusChip("VERIFIED", "active") : new StatusChip("UNVERIFIED", "pending");
        }).setHeader("Trust Badge").setAutoWidth(true);

        grid.addComponentColumn(p -> {
            Button btn = new Button(VaadinIcon.EYE.create(), e -> openDrawer(p));
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
                    return userService.getProviderProfiles(PageRequest.of(page, size)).stream();
                },
                query -> (int) userService.getProviderProfiles(PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openDrawer(ProviderProfile p) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("User ID: " + p.getUserId()));
        content.add(new Paragraph("Provider Type: " + p.getProviderType()));
        content.add(new Paragraph("Crew Size: " + p.getTypicalCrewSize()));
        content.add(new Paragraph("Verified Badge: " + p.getIsVerifiedBadge()));

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
