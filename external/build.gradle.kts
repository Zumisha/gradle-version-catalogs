import java.io.FileInputStream
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21" apply false
    `maven-publish`
    `version-catalog`
}

val secretsPropertiesFile = rootProject.file("../secrets.properties")
if (secretsPropertiesFile.exists()) {
    val secretProperties = Properties()
    // Try reading secrets from file
    secretProperties.load(FileInputStream(secretsPropertiesFile))
    for (prop in secretProperties) {
        ext[prop.key.toString()] = prop.value.toString()
    }
}

fun getProp(key: String): String? = if (ext.has(key)) ext[key]?.toString() else System.getenv(key)

catalog {
    versionCatalog {
        from(files("./external.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zustematic.versions"
            artifactId = "external"
            version = "1.0.0"
            from(components["versionCatalog"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/zumisha/Data-Collection-Terminal")
            name = "GitHubPackages"
            credentials {
                username = getProp("GITHUB_USERID")
                password = getProp("GITHUB_ACCESS_TOKEN")
            }
        }
    }
}