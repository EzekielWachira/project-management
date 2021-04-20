plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    val kotlin_version by extra("1.4.31")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation(Dependencies.coroutines)
}

