package com.tech2nxt.aiofbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id"})
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId; // One journal per session

    @Column(name = "image_url", length = 500)
    private String imageUrl; // Path or URL to stored image

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // User's journal entry text

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Get formatted image URL for frontend
     */
    public String getFullImageUrl(String baseUrl) {
        if (imageUrl == null) return null;
        // If already a full URL, return as is
        if (imageUrl.startsWith("http")) return imageUrl;
        // Otherwise, construct URL
        return baseUrl + "/uploads/" + imageUrl;
    }
}