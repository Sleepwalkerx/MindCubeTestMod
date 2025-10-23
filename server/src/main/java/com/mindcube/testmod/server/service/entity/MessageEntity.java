package com.mindcube.testmod.server.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false)
    private UUID uuid;

    @NonNull
    @Column(nullable = false, length = 256)
    private String text;
}
