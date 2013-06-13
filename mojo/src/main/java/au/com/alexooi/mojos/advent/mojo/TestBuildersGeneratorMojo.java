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
import org.apache.maven.project.MavenProject;

/**
 * @goal generate
 * @requiresDependencyResolution test
 * @phase generate-test-sources
 */
public class TestBuildersGeneratorMojo extends AbstractMojo
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    /**
	 * Flag file
	 * 
	 * @parameter default-value="${project.build.directory}/advent-flag"
	 * @required
	 */
	private File flagFile;
	
    /**
     * @parameter
     * @required
     */
    private List<String> classFqns;

    /**
     * @parameter
     */
    private List<String> extraBuilderMethodSupportFqns;

    /**
     * @parameter expression="${project.build.directory}/generated-sources/advent"
     * @required
     * @readonly
     */
    private File outputDirectory;

    private MavenSourceJarFactory mavenSourceJarFactory;


    public TestBuildersGeneratorMojo()
    {
        this.classFqns = new ArrayList<String>();
        this.extraBuilderMethodSupportFqns = new ArrayList<String>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!flagFile.exists()) {
        	URLClassLoader classLoader = getClassLoader();
            JavaGenerator javaGenerator = new JavaGenerator(classLoader, extraBuilderMethodSupportFqns);
            getLog().info("Generating ...");
            
	        outputDirectory.mkdirs();
	        project.getTestCompileSourceRoots().add(outputDirectory.getAbsolutePath());
	        // Resource resource = new Resource();
	        // resource.setDirectory(outputDirectory.getAbsolutePath());
	        // project.addTestResource(resource);
	        
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
        } else {
        	getLog().info("Skipping generation as flag file exists already: " + flagFile);
        }
    }

    private void saveToFile(GeneratedClass generatedClass)
    {
        String className = generatedClass.getClassName();
        String packageName = generatedClass.getPackageName();
        File classDirectory = new File(outputDirectory, packageName.replaceAll("\\.", File.separator));
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
            List<Artifact> artifacts = project.getCompileArtifacts();
            for (Artifact artifact : artifacts)
            {
                additionalJars.add(new URL("file://" + artifact.getFile().getAbsolutePath()));
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
