import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("io.spring.dependency-management") version "1.0.1.RELEASE"
  id("io.freefair.lombok") version "5.3.0"
}

group = "org.rowinson"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.0.2"
val junitJupiterVersion = "5.7.0"
val jacksonVersion = "2.11.3"
val mySqlClient = "4.0.2"

val mainVerticleName = "org.rowinson.healthcheck.adapters.verticles.MainVerticle"
val launcherClassName = "org.rowinson.healthcheck.CustomLauncher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClassName = launcherClassName
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-validation")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-api-contract")
  implementation("io.vertx:vertx-micrometer-metrics:$vertxVersion")
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-mysql-client:$vertxVersion")
  implementation("io.vertx:vertx-sql-client-templates:$vertxVersion")
  implementation("mysql:mysql-connector-java:8.0.15")
  implementation("io.micrometer:micrometer-registry-prometheus:latest.release")
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("org.flywaydb:flyway-core:7.5.3")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

dependencyManagement {
  imports {
    mavenBom("org.apache.logging.log4j:log4j-bom:2.14.0")
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
