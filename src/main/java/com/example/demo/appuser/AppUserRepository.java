package com.example.demo.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.fname = ?2, a.lname = ?3, a.profilePictureUrl = ?4 WHERE a.email = ?1")
    int updateUserProfile(String email, String firstName, String lastName, String profilePictureUrl);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.password = ?2 WHERE a.email = ?1")
    int updateUserPassword(String email, String newPassword);

}