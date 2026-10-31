package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.RatingReview;
import com.udyogasethu.admin.repository.RatingReviewRepository;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

@Route(value = "reviews", layout = MainLayout.class)
@PageTitle("Reviews & Ratings | UdyogaSethu Admin")
@PermitAll
public class ReviewsView extends VerticalLayout {

    private final RatingReviewRepository ratingReviewRepository;
    private final Grid<RatingReview> grid = new Grid<>(RatingReview.class, false);

    public ReviewsView(RatingReviewRepository ratingReviewRepository) {
        this.ratingReviewRepository = ratingReviewRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("Reviews, Feedback & Mutual Peer Ratings");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Inspection of 1-5 star ratings and feedback tags between workers, providers, and farmers.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(r -> r.getRating() + " ★").setHeader("Star Rating").setAutoWidth(true);
        grid.addColumn(RatingReview::getComment).setHeader("Review Feedback").setAutoWidth(true);
        grid.addColumn(RatingReview::getReviewerId).setHeader("Reviewer ID").setAutoWidth(true);
        grid.addColumn(RatingReview::getRevieweeId).setHeader("Reviewee ID").setAutoWidth(true);
        grid.addColumn(RatingReview::getCreatedAt).setHeader("Submitted").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return ratingReviewRepository.findAll(PageRequest.of(page, size)).stream();
                },
                query -> (int) ratingReviewRepository.count()
        ));
    }
}
