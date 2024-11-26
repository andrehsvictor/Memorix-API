package andrehsvictor.memorix.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "password" })
public class User implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 3571175413126174619L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String displayName;
    private String username;
    private String email;
    private boolean emailVerified = false;

    @Column(name = "password_hash")
    private String password;

    private boolean enabled = true;

    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

}
