plugins {
    val kotlinVersion = "1.7.20"
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "2.7.4"

    id("org.openapi.generator") version "6.2.0"
}

group = "xyz.haff.quoteapi"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    val kotlinxCoroutinesVersion = "1.6.4"
    val kotestVersion = "5.4.2"
    
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.4"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.17.4"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinxCoroutinesVersion")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.11")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // TODO: Shouldn't these be javax, not jakarta?
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")

    testImplementation("org.testcontainers:mongodb")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.4")
    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
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
            "serializationLibrary" to "jackson", // TODO: I want to use moshi but this doesn't seem to detect anything
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
