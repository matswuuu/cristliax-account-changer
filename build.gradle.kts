plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

group = "org.matswuuu"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.json:json:20231013")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val javafxVersion = "21"
    val platform = "win" // meh...
    implementation("org.openjfx:javafx-controls:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-base:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:$platform")
}

application {
    mainClass.set("org.matswuuu.cristalixaccountchanger.MainKt")
}

tasks {
    jar {
        from(configurations.getByName("runtimeClasspath").map {
            if (it.isDirectory) it else zipTree(it)
        })
        manifest {
            attributes["Manifest-Version"] = "1.0"
            attributes["Main-Class"] = "org.matswuuu.cristalixaccountchanger.MainKt"
        }
    }
}


