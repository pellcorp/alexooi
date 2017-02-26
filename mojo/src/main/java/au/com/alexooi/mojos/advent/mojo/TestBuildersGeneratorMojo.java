/*
 *
 */
package au.com.alexooi.mojos.advent.mojo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import au.com.alexooi.mojos.advent.generator.GeneratedClass;
import au.com.alexooi.mojos.advent.generator.JavaGenerator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

@Mojo(name = "generate", 
   requiresDependencyResolution = ResolutionScope.TEST, 
   defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class TestBuildersGeneratorMojo extends AbstractMojo
{
    @Component
    protected MavenProject project;

    /**
     * Flag file
     */
    @Parameter(defaultValue = "${project.build.directory}/advent-flag", required = true)
    private File flagFile;
    
    /**
     * Will check for existence of these flags and if they have a newer timestamp than the flagFile, will trigger
     * a build.
     */
    @Parameter
    private List<File> generatedFlags;
	
    @Parameter(required = true)
    private List<String> classFqns;

    @Parameter
    private List<String> extraBuilderMethodSupportFqns;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/advent", required = true)
    private File outputDirectory;
    
    private MavenSourceJarFactory mavenSourceJarFactory;

    @Component
    private BuildContext buildContext;
    
    public TestBuildersGeneratorMojo()
    {
        this.classFqns = new ArrayList<String>();
        this.extraBuilderMethodSupportFqns = new ArrayList<String>();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (isOutOfDate()) 
        {
            URLClassLoader classLoader = getClassLoader();
            JavaGenerator javaGenerator = new JavaGenerator(classLoader, extraBuilderMethodSupportFqns);
            
            getLog().info("Advent Generating ...");
            
		    outputDirectory.mkdirs();
		    project.addTestCompileSourceRoot(outputDirectory.getPath());
		    
		    Resource resource = new Resource();
		    resource.setDirectory(outputDirectory.getPath());
		    project.addTestResource(resource);
		        
		    for (String classFqn : classFqns)
		    {
		        List<GeneratedClass> generatedClasses = javaGenerator.generate(classFqn);
		        for (GeneratedClass generatedClass : generatedClasses)
		        {
		            saveToFile(generatedClass);
		        }
		    }
		    mavenSourceJarFactory.cleanUp();
		        
		    try {
		      	FileUtils.write(flagFile, "generated");
		    } catch (IOException e) {
		       	return;
		    }
		    
		    buildContext.refresh(outputDirectory);
        } else {
            getLog().info("Advent Skipping generation as up to date");
        }
    }

    private boolean isOutOfDate() {
        getLog().info("Advent Flag File: " + flagFile);
        if (flagFile.exists()) {
            if (generatedFlags != null) {
                for (File generatedFlag : generatedFlags) {
                    getLog().info("Advent Generated Flag: " + generatedFlag);
                    if (generatedFlag.lastModified() > flagFile.lastModified()) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true; // else need to generate as no flag file yet!
        }
    }
    
    private void saveToFile(GeneratedClass generatedClass)
    {
        String className = generatedClass.getClassName();
        String packageName = generatedClass.getPackageName();
        File classDirectory = new File(outputDirectory, packageName.replace('.', File.separatorChar));
        classDirectory.mkdirs();
        File sourceFile = new File(classDirectory, className + ".java");
        try
        {
            FileUtils.write(sourceFile, generatedClass.getSource());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private URLClassLoader getClassLoader()
    {
        mavenSourceJarFactory = new MavenSourceJarFactory(getLog());
        URL jarFile = mavenSourceJarFactory.createFor(project);
        List<URL> additionalJars = new ArrayList<URL>();
        additionalJars.add(jarFile);
        additionalJars.addAll(getAdditionalJars());
        return new URLClassLoader(additionalJars.toArray(new URL[additionalJars.size()]), this.getClass().getClassLoader());
    }

    private List<URL> getAdditionalJars()
    {
        List<URL> additionalJars = new ArrayList<URL>();
        try
        {
            @SuppressWarnings("unchecked")
            List<Artifact> artifacts = project.getCompileArtifacts();
            for (Artifact artifact : artifacts)
            {
            	File file = new File(artifact.getFile().getAbsolutePath());
            	URL url = file.toURI().toURL();
                additionalJars.add(url);
            }
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
        return additionalJars;
    }

    public void setClassFqns(List<String> classFqns)
    {
        this.classFqns = classFqns;
    }
}
