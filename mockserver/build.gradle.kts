plugins {
    `java-library`
    kotlin("jvm")
    application
    java
}

group = "org.sanrod"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.mock-server:mockserver-core:5.15.0")
    implementation("org.mock-server:mockserver-client-java:5.15.0")
    implementation("org.mock-server:mockserver-netty:5.15.0")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")

    implementation("net.datafaker:datafaker:2.1.0")

    implementation("org.postgresql:postgresql:42.7.4")
}

application {
    mainClass = "org.mockserver.Main"
}