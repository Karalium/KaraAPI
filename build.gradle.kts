plugins {
    java
    `maven-publish`

    // Makes a fat/shaded jar if you add external libraries.
    id("com.gradleup.shadow") version "9.4.2"

    // Lets you run a local Paper test server with ./gradlew runServer
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project

val minecraftVersion: String by project
val paperApiVersion: String by project
val pluginMain: String by project

group = pluginGroup
version = pluginVersion

base {
    archivesName.set(pluginName)
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    filteringCharset = "UTF-8"

    val props = mapOf(
        "name" to pluginName,
        "version" to project.version.toString(),
        "main" to pluginMain,
        "apiVersion" to minecraftVersion
    )

    inputs.properties(props)

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveClassifier.set("thin")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.runServer {
    minecraftVersion(minecraftVersion)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "org.kerix"
            artifactId = "karaapi"
            version = project.version.toString()
        }
    }
}