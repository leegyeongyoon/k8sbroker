package okestro.servicebroker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Component
public class PaaSUtils {

    private final ResourceLoader resourceLoader;

    public PaaSUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String convertYamlToJson(String yaml) throws JsonProcessingException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

    public String createDirectories(String path) {
        Path dirPath = Paths.get(path);
        try {
            Files.createDirectories(dirPath);
            log.info("\nCreated directories : {}", path);
        } catch (IOException e) {
            log.error("\nERROR : {}", e.getMessage());
        }
        return path;
    }

    public String getAbsolutePath(String classPath) {
        String result = "";
        Resource resource = this.resourceLoader.getResource(classPath);
        try {
            result = resource.getURL().getPath();
            log.info("\nSuccess get absolute path : {}", classPath);
        } catch (IOException e) {
            log.error("\nERROR : {}", e.getMessage());
        }
        return result;
    }
    public void createFile(Template template, Map<String, String> dataModel, String path) throws IOException, TemplateException {
        File file = new File(path);

        FileWriter fileWriter = new FileWriter(file );

        template.process(dataModel, fileWriter);

        fileWriter.close();
        file.setExecutable(true);
    }

}
