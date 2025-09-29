package com.rasi.med.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="inbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inbox {
    @Id
    private UUID id;

    @Column(nullable=false)
    private String branchId;

    @Column(nullable=false)
    private boolean processed;

    private String error;
}
