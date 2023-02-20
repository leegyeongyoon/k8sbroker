package okestro.servicebroker.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import okestro.servicebroker.util.ShellCommandExecutor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class KubernetesApiController {
    @Autowired
    private Configuration configuration;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    ShellCommandExecutor shellCommandExecutor;

    @GetMapping("/provision")
    private void test1() throws Exception {
        Template tfTemplate = configuration.getTemplate("provision.tf");
        Map<String, String> tfMap = new HashMap<>();

        String harborCrt = getAbsolutePath("classpath:/templates/terraform/harbor.crt");


        createDirectories("/Users/kimjaejung/terraforms/test");


        tfMap.put("appName", "provisioning-test");
        tfMap.put("namespace", "jjtest");
        tfMap.put("caFilePath", harborCrt);


        createFile(tfTemplate, tfMap, "/Users/kimjaejung/terraforms/test/provision.tf");


        Template shTemplate = configuration.getTemplate("run.sh");
        Map<String, String> shMap = new HashMap<>();

        shMap.put("path", "/Users/kimjaejung/terraforms/test");


        createFile(shTemplate, shMap, "/Users/kimjaejung/terraforms/test/run.sh");
        createFile(shTemplate, shMap, "/Users/kimjaejung/terraforms/test/destroy.sh");


//        shellCommandExecutor.exec("/Users/kimjaejung/terraforms/test/run.sh");

    }

    private void createFile(Template template, Map<String, String> dataModel, String path) throws IOException, TemplateException {
        File file = new File(path);

        FileWriter fileWriter = new FileWriter(file );

        template.process(dataModel, fileWriter);

        fileWriter.close();
        file.setExecutable(true);
    }


    @GetMapping("/destroy")
    private void test2() throws Exception {
//        shellCommandExecutor.exec("/Users/kimjaejung/terraforms/test/destroy.sh");
        FileUtils.deleteDirectory(new File("/Users/kimjaejung/terraforms/test"));
        log.info("Success to delete");
    }
    private String getAbsolutePath(String classPath) {
        String result = "";
        Resource resource = resourceLoader.getResource(classPath);
        try {
            result = resource.getURL().getPath();
            log.info("\nSuccess get absolute path : {}", classPath);
        } catch (IOException e) {
            log.error("\nERROR : {}", e.getMessage());
        }
        return result;
    }


    private void createDirectories(String path) {
        Path dirPath = Paths.get(path);
        try {
            Files.createDirectories(dirPath);
            log.info("\nCreated directories : {}", path);
        } catch (IOException e) {
            log.error("\nERROR : {}", e.getMessage());
        }
    }


}
