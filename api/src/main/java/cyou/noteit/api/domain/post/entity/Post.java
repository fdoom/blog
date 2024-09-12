package cyou.noteit.api.domain.post.entity;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.comment.entity.Comment;
import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import cyou.noteit.api.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(
        indexes = {
                @Index(name = "index_post_name", columnList = "postTitle")
        }
)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String postTitle;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String postContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareStatus shareStatus;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
