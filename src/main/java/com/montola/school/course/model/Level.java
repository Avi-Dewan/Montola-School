package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Curriculum level (JSC / SSC / HSC). Groups classes for browsing.
 *
 * @author avidewan
 */
@Entity
@Table(name = "levels")
@Getter
@Setter
@SequenceGenerator(name = "levels_seq", sequenceName = "levels_seq", allocationSize = 1)
public class Level extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "levels_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    /**
     * When true the level is browsed per class (JSC: Class 6/7/8).
     * When false the level is browsed as a whole (SSC, HSC).
     */
    @Column(name = "split_into_classes", nullable = false)
    private boolean splitIntoClasses = false;
}
