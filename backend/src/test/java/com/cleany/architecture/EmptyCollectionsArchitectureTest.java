package com.cleany.architecture;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class EmptyCollectionsArchitectureTest {

    @Test
    void productionCodeUsesExplicitEmptyCollectionFactories() {
        var productionClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.cleany");

        noClasses()
                .should().callMethod(List.class, "of")
                .orShould().callMethod(Set.class, "of")
                .orShould().callMethod(Map.class, "of")
                .because("empty collections must use Collections.emptyList/emptySet/emptyMap")
                .check(productionClasses);
    }
}
