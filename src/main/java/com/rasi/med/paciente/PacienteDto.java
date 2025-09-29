package com.rasi.med.paciente;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PacienteDto {
    public UUID publicId;
    public Integer version;
    @NotBlank
    public String tipoDoc;
    @NotBlank
    public String numDoc;
    @NotBlank
    public String nombres;
    @NotBlank
    public String apellidos;
    @Email
    public String email;
    public OffsetDateTime updatedAt;
    public OffsetDateTime deletedAt;
}
