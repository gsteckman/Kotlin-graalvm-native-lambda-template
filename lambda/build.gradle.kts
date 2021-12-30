plugins {
    kotlin("multiplatform") version "1.6.10"
    id("com.github.johnrengelman.shadow") version ("7.1.0")
    id("com.palantir.docker") version "0.30.0"
}

group = "io.github.gsteckman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()

        tasks.withType<Jar> {
            manifest {
                attributes["Main-Class"] = "com.amazonaws.services.lambda.runtime.api.client.AWSLambda"
            }
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
                implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
                runtimeOnly("com.amazonaws:aws-lambda-java-runtime-interface-client:2.0.0")
            }
        }
        val jvmTest by getting
    }
}

docker {
    name = "graalvm"
}

val buildNativeTask = task("buildNative") {
    group = "build"
    description = "Builds a native image from the fat Jar using Graalvm"

    dependsOn(tasks.named("build").get())
    dependsOn("shadowJar")
    dependsOn("docker")

    doLast {
        val shadowJar = tasks.named<Jar>("shadowJar").get()
        val jarFile = shadowJar.archiveFile.get().asFile
        val functionFile = File("${shadowJar.destinationDirectory.get()}/function")

        if (!functionFile.exists() || (functionFile.lastModified() < jarFile.lastModified())) {
            val dockerCmd =
                "docker run --rm --mount type=bind,source=${shadowJar.destinationDirectory.get()},target=/var/build/libs graalvm"
            val nativeImageCmd =
                "--verbose --no-fallback -jar /var/build/libs/${shadowJar.archiveFileName.get()} function"
            exec {
                commandLine(dockerCmd.split(" ") + nativeImageCmd.split(" "))
            }
        }
    }
}

val packageLambda = task<Zip>("packageLambda") {
    group = "build"
    description = "Packages the native image with a bootstrap script in a zip file suitable for AWS lambda deployment"

    dependsOn(buildNativeTask)

    val bootstrapFile = File(layout.buildDirectory.dir("libs").get().asFile, "bootstrap")
    val executable = File(layout.buildDirectory.dir("libs").get().asFile, "function")

    doFirst {
        // write the bootstrap script
        val bootstrap = bootstrapFile
        val bsWriter = bootstrap.printWriter()
        bsWriter.print("#!/usr/bin/env bash\n")
        bsWriter.print("./function \$_HANDLER")
        bsWriter.close()
    }

    from(bootstrapFile)
    from(executable)
}