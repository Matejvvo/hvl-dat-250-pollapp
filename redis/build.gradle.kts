import org.gradle.kotlin.dsl.implementation

plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "no.hvl.dat250"
version = "0.0.1-SNAPSHOT"
description = "redis"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("redis.clients:jedis:6.2.0")
    implementation(project(":backend"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("bootRun")

tasks.named("build")
