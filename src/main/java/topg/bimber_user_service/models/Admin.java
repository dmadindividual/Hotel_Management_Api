package topg.bimber_user_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private Date createdAt;
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;


}
