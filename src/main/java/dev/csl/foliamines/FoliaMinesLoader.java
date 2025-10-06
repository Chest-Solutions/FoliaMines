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
		resolver.addRepository(new RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build());
		resolver.addRepository(new RemoteRepository.Builder("mavenCentral", "default", "https://oss.sonatype.org/content/groups/public/").build());

		resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.20"), null));

		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:invui-core:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r19:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r20:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r21:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r22:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r23:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r24:1.47"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r25:1.47"), null));

		pluginClasspathBuilder.addLibrary(resolver);
	}
}
