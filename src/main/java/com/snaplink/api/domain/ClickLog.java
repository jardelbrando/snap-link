package com.snaplink.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "click_log_sequence")
    @SequenceGenerator(name = "click_log_sequence", sequenceName = "click_log_sequence")
    @Column(name = "click_log_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "access_date")
    private LocalDateTime accessDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    private Url url;
}
