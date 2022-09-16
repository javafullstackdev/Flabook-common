package com.msquare.flabook.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.enumeration.UserRole;
import com.msquare.flabook.enumeration.UserStatus;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.service.elasticsearch.CacheValues;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.*;
import javax.persistence.*;
import javax.persistence.Index;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id", "nickname"}, callSuper = false)
@Indexed(index = ElasticsearchConfig.INDEX_NAME)
@Entity
@NoArgsConstructor
@DynamicUpdate
@ToString(of={"id", "nickname", "role", "level"})
@org.hibernate.annotations.Cache(region = CacheValues.USERS, usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Table(
	indexes = {@Index(name = "IDK_ROLE", columnList = "role"),
        @Index(name = "IDK_STATUS", columnList = "status"),
		@Index(name = "IDK_NICKNAME", columnList = "nickname"),
		@Index(name = "IDK_SECIRITY_ID", columnList = "security_id"),
        @Index(name = "IDK_SETTING_ID", columnList = "user_setting_id"),
		@Index(name = "IDK_LOGIN", columnList = "provider, providerId"),
        @Index(name = "IDK_UMPH_ID", columnList = "user_modify_provider_history_id"),
        @Index(name = "IDK_EVIF_ID", columnList = "event_info_id")
    })
public class User extends BaseEntity{

    @Id
    @NumericField
    @SortableField
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.WebAdminJsonView.class)
    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition="VARBINARY(60)")
    private String nickname;

    @JsonView(Views.WebAdminJsonView.class)
    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "smallint")
    @Enumerated(EnumType.ORDINAL)
    private UserRole role;

    @JsonView(Views.WebAdminJsonView.class)
    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "smallint default 0")
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status; //정상, 차단, 탈퇴 여부

    @Column(columnDefinition = "varchar(128) comment '쇼핑몰ID'")
    private String shopUserId;

	/*
	 *  Oauth 제공자
	 */
    @Column(length = 10)
    private String provider;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(length = 100)
    private String providerId;

    @AttributeOverride(name = "dimension", column = @Column(name = "dimension"))
    private ImageResource imageResource;

    /**
     * 비밀번호
     */
    //@Embedded
    //@NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "security_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserSecurity security;

    /**
     * 배송지 정보
     */
    //@Embedded
    //@NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "event_info_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserEventInfo eventInfo;

    /**
     * 활동내역
     */
    @Basic(fetch = FetchType.LAZY)
    @Embedded
    @IndexedEmbedded(includeEmbeddedObjectId = true, includePaths = {"commentCount", "postingCount"})
    private UserActivity activity = new UserActivity();

    @JoinColumn(name = "user_setting_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserSetting userSetting = new UserSetting();

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "tinyint default 1")
    private int level = 1;

    @BatchSize(size = 100)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY)
    private List<Posting> postings;

    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY)
    private List<UserPushToken> userPushTokens;

    @Field(store = Store.YES, analyze = Analyze.YES)
    @DateBridge(resolution = Resolution.SECOND)
    @Override
    public ZonedDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    //알림 보낼때 확인
    public boolean isActive() {
        return UserStatus.NORMAL.equals(this.status) && UserRole.ADMIN.equals(this.role) || UserRole.USER.equals(this.role) || UserRole.EXPERT.equals(this.role);
    }

//	프로필 관리 - 휴대폰 번호인증 내역
    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserModifyPhoneHistory> userModifyPhoneHistory;

//    프로필 관리 - 이메일 번호인증 내역
    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserModifyEmailHistory> userModifyEmailHistory;

//    계정 - 비밀번호 찾기 이메일 발송 내역
    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserModifyPasswordHistory> userModifyPasswordHistory;

//    계정 연동 내역
    @JoinColumn(name = "user_modify_provider_history_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserModifyProviderHistory userModifyProviderHistory;

//    계정 연동 - 이메일 번호인증 내역
    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserModifyProviderAuth> userModifyProviderAuth;

    @JsonView(Views.WebAdminJsonView.class)
    @BatchSize(size = 100)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<UserExpertGroup> expertGroup;


    /**
     * 뱃지
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserBadge> myBadges;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Badge representBadge;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int badgeCount;

    /**
     * 포토
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoAlbum> photoAlbums = new ArrayList<>();

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int totalPhotosCnt;

    @Field(index = org.hibernate.search.annotations.Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(columnDefinition = "int default 0")
    private int totalPhotosLikeCnt;
}
