package com.chatapp.repository;

import com.chatapp.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser_Id(UUID userId);
    Optional<UserProfile> findByUser_Email(String email);

    @Query("SELECT p FROM UserProfile p WHERE " +
           "LOWER(p.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserProfile> searchProfiles(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM UserProfile p WHERE p.profilePrivacy = 'PUBLIC'")
    List<UserProfile> findPublicProfiles();

    List<UserProfile> findByInterestsContaining(String interest);
}