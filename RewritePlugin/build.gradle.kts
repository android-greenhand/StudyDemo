//repositories {
//    google()
//    jcenter()
//    mavenCentral()
//}
//
//plugins {
//    `kotlin-dsl`// 使用kotlin脚本
//    `java-gradle-plugin`// 用于定义插件
//    `maven-publish`
//    maven
//    java
//    groovy
//    `java-library`
//}
//
//dependencies {
//    implementation(localGroovy())
//    // 提供plugin
//    implementation(gradleApi())
//    // 提供transform
//    implementation("com.android.tools.build:gradle:4.0.2")
//    compileOnly("com.android.tools.build:gradle-api:4.0.2")
//    // 提供asm
//    implementation("org.ow2.asm:asm:7.1")
//    implementation("org.ow2.asm:asm-util:7.1")
//    implementation("org.ow2.asm:asm-commons:7.1")
//}
//
//group = "rewrite.plugin.RewritePlugin"
//version = "1.0.12"
//
//gradlePlugin {
//    plugins {
//        create("rewrite.plugin.RewritePlugin") {
//            id = project.group as String?
//            implementationClass = "rewrite.plugin.RewritePlugin"
//        }
//    }
//}
//
