package dev.csl.foliamines

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

class FoliaMinesLoader: PluginLoader {
    override fun classloader(pluginClasspathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()
        resolver.addRepository(RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build())
        resolver.addRepository(RemoteRepository.Builder("mavenCentral", "default", "https://oss.sonatype.org/content/groups/public/").build())
        resolver.addDependency(Dependency(DefaultArtifact("xyz.xenondevs.invui:invui:pom:1.44"), null))
        resolver.addDependency(Dependency(DefaultArtifact("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0"), null))
        pluginClasspathBuilder.addLibrary(resolver)
    }
}