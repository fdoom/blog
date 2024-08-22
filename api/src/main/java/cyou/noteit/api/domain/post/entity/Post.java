package cyou.noteit.api.domain.post.entity;

import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.comment.entity.Comment;
import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(name = "index_post_name", columnList = "postTitle")
        }
)
public class Post {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
