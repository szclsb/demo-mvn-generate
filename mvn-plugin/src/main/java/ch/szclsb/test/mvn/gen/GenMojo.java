package ch.szclsb.test.mvn.gen;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Mojo(name = "gen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("execute");

        var path = target.toPath().resolve("generated-sources/gen-plugin");

        createFile(path, "ch.szclsb.test.mvn.gen", "Gen");

        project.addCompileSourceRoot(path.toString());
    }

    private void createFile(Path path, String packageName, String className) throws MojoExecutionException {
        var dir = path.resolve(packageName.replace(".", "/"));
        var file = dir.resolve(className + ".java");
        getLog().info("creating file: " + file);
        try {
            Files.createDirectories(dir);
            try (var os = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE))) {
                os.write(String.format("""
                        package %s;
                        
                        public class %s {
                            public String get() {
                                return "hello gen";
                            }
                        }
                        """, packageName, className).getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        } catch (IOException e) {
            getLog().error("Failed to generate new file", e);
            throw new MojoExecutionException("Failed to generate new file", e);
        }
    }
}
