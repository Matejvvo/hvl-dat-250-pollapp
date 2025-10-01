plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "no.hvl.dat250"
version = "1.2.2"
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

    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

tasks.register<Exec>("rabbitmqStart") {
    group = "rabbitmq"
    description = "Start RabbitMQ detached (no Homebrew/launchd)"

    environment(
        "RABBITMQ_NODENAME" to "rabbit@localhost",
        "RABBITMQ_NODE_PORT" to "5672",
    )

    commandLine(
        "bash", "-lc", """
          set -e
          export PATH="/opt/homebrew/sbin:/opt/homebrew/bin:/usr/local/sbin:/usr/local/bin:${'$'}PATH"
          rabbitmq-server -detached

          for i in {1..40}; do
            if rabbitmq-diagnostics -q ping >/dev/null 2>&1; then
              echo "RabbitMQ is up."
              exit 0
            fi
            sleep 0.5
          done

          echo "RabbitMQ failed to start."
          exit 1
        """.trimIndent()
    )
}

tasks.register<Exec>("rabbitmqStop") {
    group = "rabbitmq"
    description = "Stop detached RabbitMQ node"
    commandLine(
        "bash",
        "-lc",
        """export PATH="/opt/homebrew/bin:/usr/local/bin:${'$'}PATH"; rabbitmqctl -n rabbit@localhost stop || true"""
    )
    isIgnoreExitValue = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn(":frontend:copyWebApp")
}

tasks.named("bootRun") {
    dependsOn(":frontend:copyWebApp")
    dependsOn("rabbitmqStart")
    finalizedBy("rabbitmqStop")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(":frontend:copyWebApp")
}
