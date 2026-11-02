package dev.csl.foliamines;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class FoliaMinesLoader implements PluginLoader {
	@Override
	public void classloader(PluginClasspathBuilder pluginClasspathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
		resolver.addRepository(new RemoteRepository.Builder("mavenCentral", "default", "https://maven-central.storage-download.googleapis.com/maven2/").build());

		resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.21"), null));

		resolver.addDependency(new Dependency(new DefaultArtifact("dev.jorel:commandapi-paper-shade:12.1.0"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("com.github.stefvanschie.inventoryframework:IF:0.11.6"), null));

		pluginClasspathBuilder.addLibrary(resolver);
	}
}
