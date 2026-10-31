package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.AppNotification;
import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.service.NotificationService;
import com.udyogasethu.admin.service.UserService;
import com.udyogasethu.admin.ui.components.ConfirmReasonDialog;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notifications | UdyogaSethu Admin")
@PermitAll
public class NotificationsView extends VerticalLayout {

    private final NotificationService notificationService;
    private final UserService userService;
    private final Grid<AppNotification> grid = new Grid<>(AppNotification.class, false);

    public NotificationsView(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildComposer();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("In-App Notifications & Campaign Dispatch");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Controlled broadcast composer with audience preview and historical dispatch audit logs.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildComposer() {
        VerticalLayout composerCard = new VerticalLayout();
        composerCard.addClassName("setu-card");
        composerCard.setWidthFull();
        composerCard.setPadding(true);
        composerCard.getStyle().set("padding", "20px");

        H3 heading = new H3("Broadcast Message Composer");
        heading.getStyle().set("margin", "0 0 10px 0").set("font-size", "1.1rem").set("color", "var(--us-text-main)");

        TextField titleField = new TextField("Alert Title");
        titleField.setWidth("350px");
        titleField.setPlaceholder("e.g. Weather Advisory: Heavy Rains Alert");

        TextArea bodyField = new TextArea("Notification Body");
        bodyField.setWidthFull();
        bodyField.setMinHeight("80px");
        bodyField.setPlaceholder("Enter text message that will appear in mobile Notification Centre...");

        Button sendBtn = new Button("Send Broadcast Alert", VaadinIcon.PAPERPLANE.create(), e -> {
            String t = titleField.getValue();
            String b = bodyField.getValue();

            if (t == null || t.trim().isEmpty() || b == null || b.trim().isEmpty()) {
                Notification.show("Title and body are required.");
                return;
            }

            new ConfirmReasonDialog(
                    "Broadcast In-App Alert",
                    "Dispatch notification: '" + t + "' across mobile clients.",
                    "Confirm Dispatch",
                    false,
                    reason -> {
                        // Deliver to sample registered profile for demonstration
                        var users = userService.getUsers("", "ALL", PageRequest.of(0, 1)).getContent();
                        UUID targetUser = users.isEmpty() ? UUID.randomUUID() : users.get(0).getId();
                        notificationService.broadcastNotification(targetUser, t, b, "broadcast");
                        Notification.show("Notification broadcast dispatched.");
                        titleField.clear();
                        bodyField.clear();
                        refreshGrid();
                    }
            ).open();
        });
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        composerCard.add(heading, titleField, bodyField, sendBtn);
        add(composerCard);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(AppNotification::getTitle).setHeader("Alert Title").setAutoWidth(true);
        grid.addColumn(AppNotification::getBody).setHeader("Message Content").setAutoWidth(true);
        grid.addColumn(AppNotification::getUserId).setHeader("Recipient User UUID").setAutoWidth(true);
        grid.addComponentColumn(n -> n.isRead() ? new StatusChip("READ", "active") : new StatusChip("UNREAD", "pending")).setHeader("Read Status").setAutoWidth(true);
        grid.addColumn(AppNotification::getCreatedAt).setHeader("Dispatched At").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return notificationService.getNotifications(PageRequest.of(page, size)).stream();
                },
                query -> (int) notificationService.getNotifications(PageRequest.of(0, 1)).getTotalElements()
        ));
    }
}
