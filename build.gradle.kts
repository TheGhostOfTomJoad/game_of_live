plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/com.gitlab.klamonte/jexer
    //implementation("com.gitlab.klamonte:jexer:1.6.0")
    // https://mvnrepository.com/artifact/com.googlecode.lanterna/lanterna
    implementation("com.googlecode.lanterna:lanterna:3.1.2")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}