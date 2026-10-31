package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(name = "title_key", nullable = false)
    private String title;

    @Column(name = "body_key", nullable = false, columnDefinition = "TEXT")
    private String body;

    private String type;

    @Column(name = "read_at")
    private Instant readAt;

    private Instant createdAt = Instant.now();

    @Transient
    public boolean isRead() {
        return readAt != null;
    }

    public void setRead(boolean read) {
        if (read && readAt == null) {
            readAt = Instant.now();
        } else if (!read) {
            readAt = null;
        }
    }
}
