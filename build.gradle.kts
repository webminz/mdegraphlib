plugins {
    `java-library`
    `java-test-fixtures`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.0")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testCompileOnly("junit:junit:4.13")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("org.slf4j:slf4j-api:2.0.16")
    //testImplementation(testFixtures(project(":mdegraphlib")))
    testFixturesCompileOnly("junit:junit:4.13")
    testFixturesCompileOnly("org.junit.jupiter:junit-jupiter:5.8.1")

}

repositories {
    mavenCentral()
}



tasks.named<Test>("test") {
    useJUnitPlatform()
    reports.junitXml.apply {
        enabled  = true
        outputLocation = layout.buildDirectory.dir("reports/tests-xml")
    }
}

val testFixturesImplementation by configurations.existing
val implementation by configurations.existing
testFixturesImplementation.get().extendsFrom(implementation.get())

