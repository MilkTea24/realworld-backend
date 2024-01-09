package com.milktea.main.util;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimestampEntity {
    @CreatedDate
    protected LocalDateTime createdAt;

    @CreatedDate
    protected LocalDateTime updatedAt;
}
