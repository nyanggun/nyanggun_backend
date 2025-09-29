package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="profile_pictures")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProfilePicture {
}
