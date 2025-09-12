plugins {
    id("com.github.node-gradle.node")
}

val frontendDir = rootProject.layout.projectDirectory.dir("frontend")
val backendDir = rootProject.layout.projectDirectory.dir("backend")


node {
    npmInstallCommand.set("ci")
}

tasks.named<com.github.gradle.node.npm.task.NpmTask>("npmInstall") {
    workingDir.set(frontendDir.asFile)
}

tasks.named("npmInstall") {
    inputs.file(frontendDir.file("package-lock.json"))
    outputs.dir(frontendDir.dir("node_modules"))
}

val npmBuild by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn("npmInstall")
    workingDir.set(frontendDir.asFile)
    args.set(listOf("run", "build"))
    inputs.dir(frontendDir.dir("src"))
    inputs.file(frontendDir.file("package.json"))
    inputs.file(frontendDir.file("package-lock.json"))
    outputs.dir(frontendDir.dir("dist"))
}

tasks.register<Copy>("copyWebApp") {
    dependsOn(npmBuild)
    from(frontendDir.dir("dist"))
    into(backendDir.dir("src/main/resources/static"))
}
