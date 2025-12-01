package SpringBootEcom.repository;

import SpringBootEcom.model.AppRole;
import SpringBootEcom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
