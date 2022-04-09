plugins {
	`java-library`
	`java-test-fixtures`
	checkstyle
	pmd
	alias(libs.plugins.spotbugs)
	`maven-publish`
	signing
}

group = "io.github.fluxroot"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(libs.junit)
	testImplementation(libs.assertj)
	testImplementation(libs.mockito)
	testFixturesImplementation(libs.assertj)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(8))
	}
	withSourcesJar()
	withJavadocJar()
}

tasks.test {
	useJUnitPlatform()
}

checkstyle {
	config = resources.text.fromFile("$rootDir/config/checkstyle/checkstyle.xml")
	configDirectory.set(file("$rootDir/config/checkstyle"))
}

pmd {
	ruleSets = listOf()
	ruleSetConfig = resources.text.fromFile("$rootDir/config/pmd/main-ruleset.xml")
}

tasks.pmdTest {
	ruleSets = listOf()
	ruleSetConfig = resources.text.fromFile("$rootDir/config/pmd/test-ruleset.xml")
}

tasks.pmdTestFixtures {
	ruleSets = listOf()
	ruleSetConfig = resources.text.fromFile("$rootDir/config/pmd/testFixtures-ruleset.xml")
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			pom {
				name.set("Result")
				description.set("A Result object implementation for Java")
				url.set("https://github.com/fluxroot/result")
				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}
				developers {
					developer {
						id.set("fluxroot")
						name.set("Phokham Nonava")
					}
				}
				scm {
					connection.set("scm:git:git@github.com:fluxroot/result.git")
					developerConnection.set("scm:git:git@github.com:fluxroot/result.git")
					url.set("https://github.com/fluxroot/result")
				}
			}
		}
	}
	repositories {
		maven {
			val releasesRepository = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
			val snapshotRepository = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
			url = if (version.toString().endsWith("SNAPSHOT")) snapshotRepository else releasesRepository
			credentials {
				val ossrhUsername: String? by project
				val ossrhPassword: String? by project
				if (ossrhUsername != null && ossrhPassword != null) {
					username = ossrhUsername
					password = ossrhPassword
				}
			}
		}
	}
}

signing {
	val signingKeyId: String? by project
	val signingKey: String? by project
	val signingPassword: String? by project
	if (signingKeyId != null && signingKey != null && signingPassword != null) {
		useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
		sign(publishing.publications["mavenJava"])
	}
}
