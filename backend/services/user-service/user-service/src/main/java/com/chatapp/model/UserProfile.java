package com.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Column(length = 200)
    private String location;

    @Column
    private LocalDate birthDate;

    @Column(length = 200)
    private String avatarUrl;

    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "interest")
    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_friends", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "friend_id")
    @Builder.Default
    private List<Long> friends = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PrivacyLevel profilePrivacy = PrivacyLevel.PUBLIC;

    public enum PrivacyLevel {
        PRIVATE,
        FRIENDS_ONLY,
        PUBLIC
    }

    public void addFriend(Long friendId) {
        if (!this.friends.contains(friendId)) {
            this.friends.add(friendId);
        }
    }

    public void removeFriend(Long friendId) {
        this.friends.remove(friendId);
    }
}