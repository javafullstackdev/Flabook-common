package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseEntity implements Serializable {

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = this.modifiedAt = ZonedDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.modifiedAt = ZonedDateTime.now();
    }
}
