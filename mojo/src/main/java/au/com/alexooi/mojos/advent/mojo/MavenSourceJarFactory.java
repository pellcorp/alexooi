/*
 *
 */
package au.com.alexooi.mojos.advent.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import static org.apache.commons.io.FileUtils.listFiles;

public class MavenSourceJarFactory
{
    private final Log log;
    private File jarFile;

    public MavenSourceJarFactory(Log log)
    {
        this.log = log;
    }

    public URL createFor(MavenProject project)
    {
        try
        {
            String outputClassesDirectory = project.getBuild().getOutputDirectory().replace(File.separatorChar, '/');
            jarFile = File.createTempFile(this.getClass().getCanonicalName() + ".file", ".jar");
            log.info("ZIP file at: " + jarFile.getAbsolutePath());
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(jarFile));

            List<File> allClassFiles = getProjectCompiledClassFiles(outputClassesDirectory);
            for (File classFile : allClassFiles)
            {
            	String classFilePath = classFile.getAbsolutePath().replace(File.separatorChar, '/');
            	classFilePath = classFilePath.replaceFirst(outputClassesDirectory + "/", "");
                zipOutputStream.putNextEntry(new ZipEntry(classFilePath));
                IOUtils.copy(new FileInputStream(classFile), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            File file = new File(jarFile.getAbsolutePath());
            return file.toURI().toURL();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private List<File> getProjectCompiledClassFiles(String outputClassesDirectory) throws IOException
    {
        List<File> allClassFiles = new ArrayList<File>();
        // a module may not actually have any source, meaning that the outputClassesDirectory will not exist.
        // thus the need to check if it exists
        //final File placeholder = File.createTempFile("placeholder", "txt");
        //allClassFiles.add(placeholder);
        //final File placeholder = File.createTempFile("placeholder", "txt");
        //allClassFiles.add(placeholder);
        if (new File(outputClassesDirectory).exists())
        {
        	for (File file : listFiles(new File(outputClassesDirectory), new String[]{"class"}, true)) {
        		allClassFiles.add(file);
        	}
        }
        return allClassFiles;
    }

    public void cleanUp()
    {
        if (jarFile != null && jarFile.exists())
        {
            jarFile.delete();
        }
    }
}
