package com.github.datnguyenzzz.Components;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

import com.github.datnguyenzzz.Interfaces.JobExecuter;

@Component("awsCustomExecuter")
public class AWSCustomExecuter implements JobExecuter {

    @Value("${verbal.lambdaActionFile}")
    private String ACTION_FILE;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
    }

    /**
     * 
     * @param filePath
     * @return File within classpath with content
     */
    private File loadOutSideResource(String filePath) {
        Resource resource = resourceLoader.getResource(filePath);

        try {
            InputStream inputStream = resource.getInputStream();

            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(bdata, StandardCharsets.UTF_8);

            //logger.info("File content is: \n");
            //logger.info(data);

            File targetFile = new File("Jobs/" + filePath);

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
    

    private void loadAndExecuteAtRuntime(File targetFile) {
        // compilation requirements
        DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<>();
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = complier.getStandardFileManager(diagnosticListener, null, null);

        // add classpath if needed. 
        List<String> optionList = new ArrayList<>();

        // compose task
        try {
            List<File> fileList = new ArrayList<>();
            fileList.add(targetFile);

            Iterable<? extends JavaFileObject> unit = 
                fileManager.getJavaFileObjectsFromFiles(fileList);

            CompilationTask task = complier.getTask(
                null, 
                fileManager, 
                diagnosticListener, 
                optionList, 
                null, 
                unit);
            
            // Load and execute function
            if (task.call()) {
                logger.info("Class is okay !!!");
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticListener.getDiagnostics()) {
                    logger.info("Error on " + 
                        diagnostic.getLineNumber() + " - " + diagnostic.getColumnNumber()
                        + " \n With message : \n"
                        + diagnostic.getMessage(null));
                }
            }
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    private final Logger logger = LoggerFactory.getLogger(AWSCustomExecuter.class);
    @Override
    public void execute(JobDetail jobDetail) {
        // TODO Auto-generated method stub
        logger.info("Execute custom job with name : " + jobDetail.getKey().toString());

        JobDataMap dataMap = jobDetail.getJobDataMap();
        String actionFilePath = dataMap.getString(ACTION_FILE);
        actionFilePath = "file:" + actionFilePath;
        
        //TODO: Access file outside classpath
        File targetFile = this.loadOutSideResource(actionFilePath);
        //TODO: Compile java to class byte code
        this.loadAndExecuteAtRuntime(targetFile);
        //TODO: Load class byte code 
    }

}
