plugins {
    kotlin("jvm") version "1.7.10"
    id("org.openapi.generator") version "5.3.0"
}

group = "xyz.haff.quoteapi"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

val apiGeneratedSourcesDir = "$buildDir/generated-sources/api"
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/../api/api.yaml") // TODO: From properties?
    outputDir.set(apiGeneratedSourcesDir)
    apiPackage.set("xyz.haff.quoteapi.controller")
    modelPackage.set("xyz.haff.quoteapi.dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "moshi", // TODO: not working?
            "enumPropertyNaming" to "UPPERCASE",
            "reactive" to "true",
            "interfaceOnly" to "true",
        )
    )
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
    dependsOn(tasks.openApiGenerate)
}

sourceSets["main"].java.srcDir(file("$apiGeneratedSourcesDir/src/main/kotlin"))
