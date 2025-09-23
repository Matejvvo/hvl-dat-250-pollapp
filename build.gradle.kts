plugins {
    base
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.github.node-gradle.node") version "7.0.2" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }

    plugins.withId("com.github.node-gradle.node") {
        the<com.github.gradle.node.NodeExtension>().apply {
            version.set("22.18.0")
            npmVersion.set("10.9.3")
            download.set(true)

            val base = rootProject.layout.projectDirectory.dir(".gradle/node")
            workDir.set(base.dir("nodejs"))
            npmWorkDir.set(base.dir("npm"))
            yarnWorkDir.set(base.dir("yarn"))
            pnpmWorkDir.set(base.dir("pnpm"))
        }
    }
}

tasks.named("build") {
    description = "Builds the backend (and anything it depends on)"
    dependsOn(":backend:build")
}

tasks.register("test") {
    group = "verification"
    description = "Runs backend tests"
    dependsOn(":backend:test")
}

tasks.register("bootRun") {
    group = "application"
    description = "Runs the Spring Boot app from :backend"
    dependsOn(":backend:bootRun")
}

tasks.register("run") {
    group = "application"
    description = "Alias for bootRun"
    dependsOn("bootRun")
}

tasks.register("redis") {
    group = "application"
    description = "Runs the redis CLI demo"
    dependsOn(":redis:bootRun")
}