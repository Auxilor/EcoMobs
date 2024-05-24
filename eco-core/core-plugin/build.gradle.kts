group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("io.github.arcaneplugins:levelledmobs-plugin:4.0.2")
    compileOnly("LibsDisguises:LibsDisguises:10.0.38")
    implementation("com.willfp:ModelEngineBridge:1.0.0")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = project.group.toString()
            version = project.version.toString()
            artifactId = rootProject.name

            artifact(rootProject.tasks.shadowJar.get().archiveFile)
        }
    }

    repositories {
        maven {
            name = "auxilor"
            url = uri("https://repo.auxilor.io/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

tasks {
    build {
        dependsOn(publishToMavenLocal)
    }
}
