plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "5.3.0"
}

dependencies {
    testImplementation(kotlin("test"))
}


// TODO: This project is the API skeleton implementation... not DTO models, so name is off
// TODO: use kotlin time
val apiGeneratedSourcesDir = "$buildDir/generated-sources/api"
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/../../api/api.yaml") // TODO: From properties?
    outputDir.set(apiGeneratedSourcesDir)
    modelPackage.set("xyz.haff.backend.dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "reactive" to "true",
        )
    )
    typeMappings.set(
        mapOf(
            "java.time.OffsetDateTime" to "java.time.LocalDateTime",
            "time" to "java.time.LocalTime",
        )
    )
    importMappings.set(
        mapOf(
            "java.time.OffsetDateTime" to "java.time.LocalDateTime",
            "LocalTime" to "java.time.LocalTime",
        )
    )
}

sourceSets["main"].java.srcDir(file("$apiGeneratedSourcesDir/src/main/kotlin"))

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}
