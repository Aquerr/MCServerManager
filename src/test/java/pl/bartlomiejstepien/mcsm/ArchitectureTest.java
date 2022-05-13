package pl.bartlomiejstepien.mcsm;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

@AnalyzeClasses(packages = "pl.bartlomiejstepien.mcsm", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest
{
    @ArchTest
    static final ArchRule SERVICES_SHOULD_EXIST_IN_SERVICE_PACKAGE =
            classes().that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("ServiceImpl");

    @ArchTest
    static final ArchRule SERVICES_SHOULD_NOT_DEPEND_ON_CONTROLLERS =
            noClasses().that().areAnnotatedWith(Service.class)
                    .should().dependOnClassesThat().areAnnotatedWith(Controller.class)
                    .orShould().dependOnClassesThat().areAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_EXISTS_IN_CONTROLLERS_PACKAGE =
            classes().that().areAnnotatedWith(Controller.class)
                    .should().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule CONTROLLERS_SHOULD_NOT_DEPEND_ON_REPOSITORY_CLASSES =
            freeze(noClasses().that().areAnnotatedWith(Controller.class)
                    .should().dependOnClassesThat().resideInAPackage("..repository.."));

    @ArchTest
    static final ArchRule WEB_PACKAGE_IS_NOT_ACCESSED_BY_OTHER_PACKAGES =
            freeze(noClasses().that().resideInAnyPackage("..service..", "..repository..")
                    .should().dependOnClassesThat().resideInAPackage("..web.."));

    @ArchTest
    static final ArchRule CONFIG_CLASSES_SHOULD_RESIDE_IN_CONFIG_PACKAGE =
            classes().that().areAnnotatedWith(Configuration.class)
                    .should().resideInAPackage("..config..");
}
