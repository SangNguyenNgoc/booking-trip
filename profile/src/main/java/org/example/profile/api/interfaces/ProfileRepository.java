package org.example.profile.api.interfaces;

import org.example.profile.api.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    @Query("select (count(p) > 0) from Profile p where p.email = ?1")
    boolean existsByEmail(String email);
}
