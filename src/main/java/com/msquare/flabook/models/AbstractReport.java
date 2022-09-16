package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.msquare.flabook.enumeration.ReportStatus;
import com.msquare.flabook.json.Views;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@RequiredArgsConstructor
@MappedSuperclass
public class AbstractReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Accessors(chain = true)
    @NonNull
    @Column(columnDefinition = "varchar(32)")
    private String title;

    @Accessors(chain = true)
    @NonNull
    @Column(columnDefinition = "text", length=4096)
    private String text;

    private ZonedDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

    @Accessors(chain = true)
    @NonNull
    @Column(columnDefinition = "tinyint default 0")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.WebAdminJsonView.class)
    private ReportStatus reportStatus;
}
