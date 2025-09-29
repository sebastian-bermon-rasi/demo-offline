// Paciente.java
package com.rasi.med.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "paciente")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paciente {

    @Id
    @Column(name = "public_id", columnDefinition = "uuid")
    private UUID publicId;

    @NotBlank
    @Column(name = "tipo_doc", length = 10)
    private String tipoDoc;

    @NotBlank
    @Column(name = "num_doc", length = 50)
    private String numDoc;

    @NotBlank
    @Column(name = "nombre", length = 100)
    private String nombre;

    @NotBlank
    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Email
    @Column(name = "email", length = 150)
    private String email;

    @Version
    private Integer version;

    @Column(name = "updated_at", columnDefinition = "timestamptz")
    private OffsetDateTime updatedAt;

    private String updatedBy;

    @Column(name = "branch_id", nullable = false)
    private String branchId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @PrePersist @PreUpdate
    public void touch() {
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        else updatedAt = OffsetDateTime.now();
    }
}
