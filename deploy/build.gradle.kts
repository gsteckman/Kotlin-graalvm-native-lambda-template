plugins {
    kotlin("multiplatform") version "1.6.10"
    application
}

group = "io.github.gsteckman"
version = "1.0-SNAPSHOT"
val cdkVersion = "1.107.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("software.amazon.awscdk:apigateway:$cdkVersion")
                implementation("software.amazon.awscdk:cloudfront:$cdkVersion")
                implementation("software.amazon.awscdk:cloudfront-origins:$cdkVersion")
                implementation("software.amazon.awscdk:core:$cdkVersion")
                implementation("software.amazon.awscdk:events:$cdkVersion")
                implementation("software.amazon.awscdk:events-targets:$cdkVersion")
                implementation("software.amazon.awscdk:lambda:$cdkVersion")
                implementation("software.amazon.awscdk:s3:$cdkVersion")
                implementation("software.amazon.awscdk:s3-assets:$cdkVersion")
                implementation("software.amazon.awscdk:s3-deployment:$cdkVersion")
                implementation(project(":lambda"))
            }
        }
        val jvmTest by getting
    }
}

application {
    mainClass.set("io.github.gsteckman.deploy.CdkStack")
}

tasks.named<JavaExec>("run") {
    dependsOn(":lambda:packageLambda")
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

