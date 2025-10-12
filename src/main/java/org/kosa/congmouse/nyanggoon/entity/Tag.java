package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;

// 태그 entity
@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Tag(String name) {
        this.name=name;
    }
}
