plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "no.hvl.dat250"
version = "1.1.0-SNAPSHOT"
description = "Simple Poll & Voting App"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2:2.3.232")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("build") {
    dependsOn(":frontend:copyWebApp")
}

tasks.named("bootRun") {
    dependsOn(":frontend:copyWebApp")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(":frontend:copyWebApp")
}
