package com.msquare.flabook.models;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@ToString
@NoArgsConstructor
public class Badge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description1;

    private String description2;

    private Boolean active;

    private int orderCount;

    @AttributeOverride(name = "filekey", column = @Column(name = "trueImageFileKey"))
    @AttributeOverride(name = "filename", column = @Column(name = "trueImageFileName"))
    private ImageResource trueImageResource;

    @AttributeOverride(name = "filekey", column = @Column(name = "falseImageFileKey"))
    @AttributeOverride(name = "filename", column = @Column(name = "falseImageFileName"))
    private ImageResource falseImageResource;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserBadge> users;
}
