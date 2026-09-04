package be.gerard.lifestarter

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields

/**
 * Guards the hexagon: the domain sits in the middle and knows nothing about the world around it.
 *
 * These rules are what stop the architecture from quietly eroding — an accidental `@Autowired` in
 * a domain service or a controller reaching straight into a repository fails the build.
 */
@AnalyzeClasses(
    packages = ["be.gerard.lifestarter"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class HexagonalArchitectureTest {

    @ArchTest
    val domainIsFrameworkFree: ArchRule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta.persistence..",
            "jakarta.servlet..",
            "com.fasterxml.jackson..",
            "tools.jackson..",
            "org.apache.poi..",
        )
        .because("the domain must stay independent of frameworks and infrastructure")

    @ArchTest
    val domainDoesNotDependOnOuterLayers: ArchRule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage("..api..", "..repository..", "..mapper..")
        .because("dependencies point inwards: only adapters may know the domain")

    @ArchTest
    val controllersDoNotUseRepositoryAdapters: ArchRule = noClasses()
        .that().resideInAPackage("..api..")
        .should().dependOnClassesThat().resideInAPackage("..repository..")
        .because("controllers talk to domain services, never to persistence adapters")

    @ArchTest
    val persistenceRecordsStayInsideRepositories: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Record")
        .should().resideInAPackage("..repository.model..")
        .because("persistence records must not leak past the repository boundary")

    @ArchTest
    val controllersAreNamedConsistently: ArchRule = classes()
        .that().resideInAPackage("..api..")
        .and().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
        .should().haveSimpleNameEndingWith("Controller")

    @ArchTest
    val noFieldInjection: ArchRule = noFields()
        .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
        .because("constructor injection only")
}
