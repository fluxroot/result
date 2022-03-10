plugins {
	`java-library`
	`java-test-fixtures`
	checkstyle
	pmd
	alias(libs.plugins.spotbugs)
}

group = "dev.nonava.result"
version = "1.0.0-SNAPSHOT"

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
