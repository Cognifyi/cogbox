plugins {
    application
}

group = "io.cognifyi.examples"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.cognifyi:sdk-java")
}

application {
    mainClass.set("io.cognifyi.examples.Lifecycle")
}
