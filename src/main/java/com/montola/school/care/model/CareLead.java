package com.montola.school.care.model;

import com.montola.school.care.enums.CareLeadStatus;
import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * An Academic Care enquiry submitted from the public landing page.
 * <p>
 * Holds personal data (a parent's name and phone number): access is restricted to
 * ADMIN/MANAGER and the data should not be exposed publicly.
 * </p>
 *
 * @author avidewan
 */
@Entity
@Table(name = "care_leads", indexes = {
        @Index(name = "idx_care_lead_status", columnList = "status"),
        @Index(name = "idx_care_lead_created", columnList = "created_at")
})
@Getter
@Setter
@SequenceGenerator(name = "care_leads_seq", sequenceName = "care_leads_seq", allocationSize = 1)
public class CareLead extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "care_leads_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(length = 100)
    private String level;

    private String area;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CareLeadStatus status = CareLeadStatus.NEW;
}
