package andrehsvictor.memorix.user;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "password" })
public class User implements Serializable {

    private static final long serialVersionUID = 5004168293045539177L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "password_hash")
    private String password;

    private String email;
    private String displayName;
    private String bio;
    private String pictureUrl;

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserProvider provider = UserProvider.LOCAL;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

}
