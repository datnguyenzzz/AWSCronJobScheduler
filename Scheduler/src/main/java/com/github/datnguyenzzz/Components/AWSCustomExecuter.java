package com.github.datnguyenzzz.Components;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.github.datnguyenzzz.Interfaces.CustomJob;
import com.github.datnguyenzzz.Interfaces.JobExecuter;

@Component("awsCustomExecuter")
public class AWSCustomExecuter implements JobExecuter {

    @Value("${verbal.lambdaActionFile}")
    private String ACTION_FILE;

    @Autowired
    private ResourceLoader resourceLoader;

    private DiagnosticCollector<JavaFileObject> diagnosticListener;

    @PostConstruct
    public void init() {
    }

    /**
     * 
     * @param filePath 
     * @param data java code inside
     * @apiNote prepare file with hierarchy like /<package1>/<package2>/../name.java 
     * @return file path
     */
    private String createNewDirectory(String filePath, String data) {
        //first line
        String customJobPackage = data.split("\n")[0];
        // remove word "package"
        customJobPackage = customJobPackage.split(" ")[1];

        // split by "."
        String[] dirs = customJobPackage.split("\\.");

        StringBuilder newDir = new StringBuilder();

        for (int i=0; i<dirs.length; i++) {
            String dir = dirs[i];
            if (i == dirs.length-1) {
                //remove ';' at last
                dir = dir.replaceAll("[\\W]+", "");
            }
            //logger.info("package = " + dir);
            newDir.append(dir);
            newDir.append("/");
        }

        //get java file name
        String[] filePathSplitted = filePath.split("/");
        String javaFileName = filePathSplitted[filePathSplitted.length - 1];
        newDir.append(javaFileName);

        logger.info("New directory for file is : " + newDir.toString());
        
        return newDir.toString();
    }

    /**
     * 
     * @param filePath
     * @return
     * @apiNote change "a/b/c/d.java" to "a.b.c.d"
     */
    private String getClassName(String filePath) {
        //remove ".java"
        String name = filePath.split("\\.")[0];
        //change "/" to "."
        name = name.replace('/', '.');
        //name = name + ".class";
        return name;
    }

    /**
     * 
     * @param filePath
     * @return "file:<filePath>"
     */
    private String composeTargetFilePath(String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("file:");
        sb.append(filePath);
        return sb.toString();
    }


    /**
     * 
     * @param filePath
     * @return Copy File outside classpath with content into tar classpath
     */
    private File loadOutSideResource(String filePath) {
        Resource resource = resourceLoader.getResource(this.composeTargetFilePath(filePath));

        try {
            InputStream inputStream = resource.getInputStream();

            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(bdata, StandardCharsets.UTF_8);

            //logger.info("File content is: \n");
            //logger.info(data);

            File targetFile = new File(this.createNewDirectory(filePath, data));

            if (!targetFile.getParentFile().exists())
                targetFile.getParentFile().mkdirs();

            // Write to file
            Writer writer = null;
            try {
                writer = new FileWriter(targetFile);
                writer.write(data);
                writer.flush();
            }
            finally {
                try {
                    writer.close();
                }
                catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
            }

            return targetFile;
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
            return null;
        }
    }
    
    /**
     * 
     * @param targetFile
     * @return
     * @apiNote Load external class at run time to get .class byte code 
     */
    private CompilationTask compileCustomClassAtRuntime(File targetFile) {
        // compilation requirements
        this.diagnosticListener = new DiagnosticCollector<>();
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = complier.getStandardFileManager(
            this.diagnosticListener, null, null
        );

        // add classpath if needed. 
        List<String> optionList = new ArrayList<>();
        optionList.add("-classpath");
        //all class reside in /classes
        optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "/classes");
        // compose task
        try {
            List<File> fileList = new ArrayList<>();
            fileList.add(targetFile);

            Iterable<? extends JavaFileObject> unit = 
                fileManager.getJavaFileObjectsFromFiles(fileList);

            CompilationTask task = complier.getTask(
                null, 
                fileManager, 
                this.diagnosticListener, 
                optionList, 
                null, 
                unit);
            
            return task;
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
            return null;
        }
    }

    
    /**
     * @apiNote load and execute Compilation task
     */
    private void loadAndExecuteClassAtRuntime(CompilationTask task, String filePath) {
        if (task.call()) {
            logger.info("TASK IS FINE !!!");

            URLClassLoader classLoader = null;
            try {
                // class loader point to top of structure
                classLoader = new URLClassLoader(
                    new URL[] {new File("./").toURI().toURL()}
                );

                // load class
                String className = this.getClassName(filePath);
                logger.info("Class name = " + className);
                Class<?> loadedClass = classLoader.loadClass(className);
                logger.info("Finish loading !!!!");

                Object obj = loadedClass.getDeclaredConstructor().newInstance();
                if (obj instanceof CustomJob) {
                    // execute job
                    CustomJob customJob = (CustomJob) obj;
                    customJob.execute();
                }
                else {
                    logger.info("OBJ isn't implemented CustomJob.class");
                }
            }

            catch (Exception ex) {
                logger.info(ex.getMessage());
            }

            finally {
                //close loader
                try {
                    classLoader.close();
                }
                catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
            }
        }
        else {
            for (Diagnostic<? extends JavaFileObject> diagnostic: this.diagnosticListener.getDiagnostics()) {
                logger.info("Error at " + diagnostic.getLineNumber() 
                        + " : " + diagnostic.getColumnNumber()
                        + " - Message: " + diagnostic.getMessage(Locale.ENGLISH));
            }            
        }
    }

    private final Logger logger = LoggerFactory.getLogger(AWSCustomExecuter.class);
    @Override
    public void execute(JobDetail jobDetail) {
        // TODO Auto-generated method stub
        logger.info("Execute custom job with name : " + jobDetail.getKey().toString());

        JobDataMap dataMap = jobDetail.getJobDataMap();
        String actionFilePath = dataMap.getString(ACTION_FILE);
        //actionFilePath = this.composeTargetFilePath(actionFilePath);
        
        //TODO: Access file outside classpath
        File targetFile = this.loadOutSideResource(actionFilePath);
        //TODO: Compile java to class byte code
        CompilationTask task = this.compileCustomClassAtRuntime(targetFile);
        //TODO: Load class byte code 
        this.loadAndExecuteClassAtRuntime(task, targetFile.getPath());
    }

}
