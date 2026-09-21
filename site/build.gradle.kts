import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.script

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "com.corbettcode.host2048game"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("2048, the sliding tile puzzle, built with Kobweb")
            head.add {
                script {
                    async = true
                    src = "https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-9887769187597821"
                    attributes["crossorigin"] = "anonymous"
                }
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("host2048game")

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
