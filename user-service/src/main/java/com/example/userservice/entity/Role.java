package com.example.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Roles")
public class Role {
    public Role(Long roleId, String name, String description) {
        this.role_id=roleId;
        this.name = name;
        this.description = description;
        this.created_At=new Date(System.currentTimeMillis());
    }

    @Id
    @SequenceGenerator(name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "role_sequence"
    )
    private Long role_id;
    @Column(unique = true)
    private String name;
    private String description;
    private Date created_At;
    @ManyToMany(mappedBy = "roles")
    @Fetch(value = FetchMode.SELECT)
    @JsonIgnore
    private Set<User> user=new HashSet<>();

    public Role(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public Role(String name) {
        this.name = name;
    }}