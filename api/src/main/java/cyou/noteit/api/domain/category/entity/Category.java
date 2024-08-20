package cyou.noteit.api.domain.category.entity;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.post.entity.Post;
import cyou.noteit.api.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_category_name", columnNames = {"categoryName"})
        },
        indexes = {
                @Index(name = "index_category_name", columnList = "categoryName")
        }
)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCategoryId")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> childCategories;
}
