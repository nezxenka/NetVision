import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    id("com.gradleup.shadow") version "9.0.0-beta6"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("net.ltgt.errorprone") version "4.3.0"
    id("com.diffplug.spotless") version "7.2.1"
}

group = "club.nezxenka.netvision"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.17")
    implementation("com.github.retrooper:packetevents-spigot:2.13.0")
    implementation("org.incendo:cloud-paper:2.0.0-beta.16")
    implementation("org.incendo:cloud-processors-requirements:1.0.0-rc.1")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.slf4j:slf4j-jdk14:2.0.17")
    compileOnly("org.geysermc.floodgate:api:2.0-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("it.unimi.dsi:fastutil:8.5.15")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.flatbuffers:flatbuffers-java:25.2.10")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")
    implementation("io.lettuce:lettuce-core:6.5.0.RELEASE") { exclude(group = "io.netty") }
    compileOnly("io.netty:netty-handler:4.1.113.Final")
    errorprone("com.google.errorprone:error_prone_core:2.41.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.release.set(17)
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    minimize {
        exclude(dependency("org.slf4j:slf4j-api"))
        exclude(dependency("org.slf4j:slf4j-jdk14"))
        exclude(dependency("net.kyori:adventure-text-serializer-gson"))
        exclude(dependency("net.kyori:adventure-text-serializer-json"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy"))
        exclude(dependency("io.lettuce:lettuce-core"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-databind"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-core"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-annotations"))
        exclude(dependency("io.projectreactor:reactor-core"))
        exclude(dependency("org.reactivestreams:reactive-streams"))
    }

    transformers.add(ServiceFileTransformer())

    relocate("com.github.retrooper.packetevents", "club.nezxenka.netvision.libs.packetevents.api")
    relocate("io.github.retrooper.packetevents", "club.nezxenka.netvision.libs.packetevents.impl")
    relocate("net.kyori", "club.nezxenka.netvision.libs.kyori")
    relocate("com.google.gson", "club.nezxenka.netvision.libs.gson")
    relocate("org.incendo", "club.nezxenka.netvision.libs.incendo")
    relocate("io.leangen.geantyref", "club.nezxenka.netvision.libs.geantyref")
    relocate("it.unimi.dsi.fastutil", "club.nezxenka.netvision.libs.fastutil")
    relocate("com.google.flatbuffers", "club.nezxenka.netvision.libs.flatbuffers")
    relocate("com.zaxxer.hikari", "club.nezxenka.netvision.libs.hikari")
    relocate("org.slf4j", "club.nezxenka.netvision.libs.slf4j")
    relocate("org.jetbrains", "club.nezxenka.netvision.libs.jetbrains")
    relocate("org.intellij", "club.nezxenka.netvision.libs.intellij")
    relocate("com.fasterxml.jackson", "club.nezxenka.netvision.libs.jackson")
    relocate("io.lettuce", "club.nezxenka.netvision.libs.lettuce")
    relocate("reactor", "club.nezxenka.netvision.libs.reactor")
    relocate("org.reactivestreams", "club.nezxenka.netvision.libs.reactivestreams")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(tasks.spotlessApply)
}

bukkit {
    name = "NetVision"
    main = "club.nezxenka.netvision.NetVision"
    version = project.version.toString()
    apiVersion = "1.13"
    authors = listOf(
        "nezxenka"
    )
    softDepend = listOf(
        "ProtocolLib",
        "ProtocolSupport",
        "Essentials",
        "ViaVersion",
        "ViaBackwards",
        "ViaRewind",
        "Geyser-Spigot",
        "floodgate",
        "FastLogin",
        "PlaceholderAPI",
        "WorldGuard",
        "DecentHolograms",
    )

    permissions {
        register("netvision.*") {
            description = "Все права"
            default = Permission.Default.OP
            children = listOf(
                "netvision.help",
                "netvision.alerts",
                "netvision.alerts.enable-on-join",
                "netvision.menu",
                "netvision.status",
                "netvision.reload",
                "netvision.prob",
                "netvision.profile",
                "netvision.brand",
                "netvision.brand.enable-on-join",
                "netvision.falsepositive",
                "netvision.ban"
            )
        }
        register("netvision.help") {
            description = "Показывает хелп"
            default = Permission.Default.OP
        }
        register("netvision.alerts") {
            description = "Получение уведомлений о нарушениях"
            default = Permission.Default.OP
        }
        register("netvision.alerts.enable-on-join") {
            description = "Автоматически включает оповещения при присоединении"
            default = Permission.Default.OP
        }
        register("netvision.menu") {
            description = "Открывает курятник"
            default = Permission.Default.OP
        }
        register("netvision.status") {
            description = "Включает голограмму над игроками"
            default = Permission.Default.OP
        }
        register("netvision.reload") {
            description = "Перезагрузка конфига"
            default = Permission.Default.OP
        }
        register("netvision.exempt") {
            description = "Исключение для всех чеков"
            default = Permission.Default.FALSE
        }
        register("netvision.falsepositive") {
            description = "Сохранение тиков для анализа false positive"
            default = Permission.Default.OP
        }
        register("netvision.prob") {
            description = "Разрешает смотреть вероятность (пробу)"
            default = Permission.Default.OP
        }
        register("netvision.profile") {
            description = "Смотреть профиль игрока"
            default = Permission.Default.OP
        }
        register("netvision.brand") {
            description = "Получение уведомлений о версии"
            default = Permission.Default.OP
        }
        register("netvision.brand.enable-on-join") {
            description = "Автоматически включает уведомления о версии при входе"
            default = Permission.Default.OP
        }
        register("netvision.ban") {
            description = "Бан через /nvp ban"
            default = Permission.Default.OP
        }
    }
}

spotless {
    isEnforceCheck = true

    java {
        importOrder()

        removeUnusedImports()

        googleJavaFormat("1.17.0")
    }
}
