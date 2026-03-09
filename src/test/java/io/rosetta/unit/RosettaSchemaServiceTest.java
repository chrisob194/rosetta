package io.rosetta.unit;

import io.rosetta.annotation.Glyph;
import io.rosetta.annotation.Inscription;
import io.rosetta.model.RosettaEntity;
import io.rosetta.model.RosettaField;
import io.rosetta.model.RosettaSchema;
import io.rosetta.service.RosettaSchemaService;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RosettaSchemaServiceTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private Metamodel metamodel;

    @Mock
    private EntityType<SampleEntity> entityType;

    @Mock
    @SuppressWarnings("rawtypes")
    private Attribute idAttribute;

    @Mock
    @SuppressWarnings("rawtypes")
    private Attribute statusAttribute;

    @Mock
    @SuppressWarnings("rawtypes")
    private Attribute relatedAttribute;

    private RosettaSchemaService service;

    @BeforeEach
    void setUp() {
        when(emf.getMetamodel()).thenReturn(metamodel);
        service = new RosettaSchemaService(emf);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getSchema_mapsEntityCorrectly() {
        when(metamodel.getEntities()).thenReturn(Set.of(entityType));
        when(entityType.getJavaType()).thenReturn((Class) SampleEntity.class);
        when(entityType.getName()).thenReturn("SampleEntity");
        when(entityType.getAttributes()).thenReturn(Set.of(idAttribute, statusAttribute));

        when(idAttribute.getName()).thenReturn("id");
        when(idAttribute.getJavaType()).thenReturn((Class) Long.class);

        when(statusAttribute.getName()).thenReturn("status");
        when(statusAttribute.getJavaType()).thenReturn((Class) String.class);

        RosettaSchema schema = service.getSchema();

        assertThat(schema.getEntities()).hasSize(1);
        RosettaEntity entity = schema.getEntities().get(0);

        assertThat(entity.getName()).isEqualTo("SampleEntity");
        assertThat(entity.getTable()).isEqualTo("sample_table");
        assertThat(entity.getInscription()).isEqualTo("A sample entity for testing");

        RosettaField idField = entity.getFields().stream()
                .filter(f -> f.getName().equals("id"))
                .findFirst().orElseThrow();
        assertThat(idField.isPrimaryKey()).isTrue();
        assertThat(idField.isNullable()).isFalse();
        assertThat(idField.getColumn()).isEqualTo("id");
        assertThat(idField.getType()).isEqualTo("Long");

        RosettaField statusField = entity.getFields().stream()
                .filter(f -> f.getName().equals("status"))
                .findFirst().orElseThrow();
        assertThat(statusField.getGlyph()).isEqualTo("Status values: ACTIVE, INACTIVE");
        assertThat(statusField.getColumn()).isEqualTo("status_col");
        assertThat(statusField.isNullable()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getEntity_returnsEmptyForUnknownName() {
        when(metamodel.getEntities()).thenReturn(Set.of(entityType));
        when(entityType.getName()).thenReturn("SampleEntity");

        Optional<RosettaEntity> result = service.getEntity("Unknown");

        assertThat(result).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getEntity_returnsPresentForKnownName() {
        when(metamodel.getEntities()).thenReturn(Set.of(entityType));
        when(entityType.getJavaType()).thenReturn((Class) SampleEntity.class);
        when(entityType.getName()).thenReturn("SampleEntity");
        when(entityType.getAttributes()).thenReturn(Set.of());

        Optional<RosettaEntity> result = service.getEntity("SampleEntity");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("SampleEntity");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getSchema_mapsRelationAnnotation() {
        when(metamodel.getEntities()).thenReturn(Set.of(entityType));
        when(entityType.getJavaType()).thenReturn((Class) SampleEntity.class);
        when(entityType.getName()).thenReturn("SampleEntity");
        when(entityType.getAttributes()).thenReturn(Set.of(relatedAttribute));

        when(relatedAttribute.getName()).thenReturn("related");
        when(relatedAttribute.getJavaType()).thenReturn((Class) SampleEntity.class);

        RosettaSchema schema = service.getSchema();
        RosettaField relatedField = schema.getEntities().get(0).getFields().get(0);

        assertThat(relatedField.getRelation()).isEqualTo("ManyToOne");
    }

    // --- Fixture classes for unit tests ---

    @Table(name = "sample_table")
    @Inscription("A sample entity for testing")
    static class SampleEntity {

        @Id
        @Column(name = "id", nullable = false)
        Long id;

        @Column(name = "status_col")
        @Glyph("Status values: ACTIVE, INACTIVE")
        String status;

        @ManyToOne
        SampleEntity related;
    }
}
