package okestro.servicebroker.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import okestro.servicebroker.constant.PaaSBrokerConstant;
import okestro.servicebroker.model.dto.ClusterInfoDto;
import okestro.servicebroker.model.entity.PaasServiceInstance;
import okestro.servicebroker.repo.PaasServiceInstanceRepo;
import okestro.servicebroker.service.PaasService;
import okestro.servicebroker.util.PaaSUtils;
import okestro.servicebroker.util.ShellCommandExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaasServiceImpl implements PaasService {

    private final WebClient webClient;
    private final Configuration configuration;

    private final PaasServiceInstanceRepo openshiftServiceInstanceRepo;

    private final PaaSUtils paaSUtils;
    private final ShellCommandExecutor shellCommandExecutor;
    @Value("${api.url.k8s}")
    private String k8sUrl;

    @Value("${k8s.url}")
    private String k8sClusterId;

    @Value("${k8s.token}")
    private String k8sToken;




    public PaasServiceImpl(WebClient webClient, Configuration configuration, PaasServiceInstanceRepo openshiftServiceInstanceRepo, PaaSUtils paaSUtils, ShellCommandExecutor shellCommandExecutor) {
        this.webClient = webClient;
        this.configuration = configuration;
        this.openshiftServiceInstanceRepo = openshiftServiceInstanceRepo;
        this.paaSUtils = paaSUtils;
        this.shellCommandExecutor = shellCommandExecutor;
    }

    @Override
    public void createInstance(CreateServiceInstanceRequest request) {
        PaasServiceInstance instance = openshiftServiceInstanceRepo.findByInstanceId(request.getServiceInstanceId());

        String projectId = instance.getProjectId();
        String createdDir = instance.getDirectoryPath();

        ClusterInfoDto clusterInfoDto =  getClusterInfo(projectId);
//
//        if (clusterInfoDto == null) {
//            throw new RuntimeException("clusterInfo is null");
//        }

        try {
            Template tfTemplate = configuration.getTemplate(PaaSBrokerConstant.TF_PROVISION_FILE);

            Map<String, String> provisionParams = new HashMap<>();

            provisionParams.put(PaaSBrokerConstant.PROVISION_HOST, k8sClusterId);
            provisionParams.put(PaaSBrokerConstant.PROVISION_TOKEN, k8sToken);
            provisionParams.put(PaaSBrokerConstant.PROVISION_NAMESPACE, "terraformtest");
            provisionParams.put(PaaSBrokerConstant.PROVISION_APP_NAME, request.getParameters().get("service_instance_nm").toString());
//            provisionParams.put(PaaSBrokerConstant.PROVISION_CA, paaSUtils.getAbsolutePath(PaaSBrokerConstant.PROVISION_CA_CLASSPATH));
            provisionParams.put(PaaSBrokerConstant.PROVISION_SELECTED_SVC, request.getServiceDefinition().getName());

            paaSUtils.createFile(tfTemplate, provisionParams, createdDir+PaaSBrokerConstant.TF_PROVISION_FILE);


            Template runSh = configuration.getTemplate(PaaSBrokerConstant.TF_RUN_SH);
            Map<String, String> shDataModel = new HashMap<>();


            shDataModel.put(PaaSBrokerConstant.PROVISION_PATH, createdDir);
            paaSUtils.createFile(runSh, shDataModel, createdDir+PaaSBrokerConstant.TF_RUN_SH);


            Template destroySh = configuration.getTemplate(PaaSBrokerConstant.TF_DESTROY_SH);
            paaSUtils.createFile(destroySh, shDataModel, createdDir+PaaSBrokerConstant.TF_DESTROY_SH);

            shellCommandExecutor.exec(createdDir+PaaSBrokerConstant.TF_RUN_SH);
            instance.setState(PaaSBrokerConstant.SUCCEEDED);
            openshiftServiceInstanceRepo.save(instance);

        } catch (Exception e) {
            log.error("\n===== Failed to create Instance ======\n{}", e.getMessage());
            instance.setState(PaaSBrokerConstant.FAILED);
            openshiftServiceInstanceRepo.save(instance);
        }
    }
    private ClusterInfoDto getClusterInfo(String projectId) {
        return webClient.get().uri(k8sUrl + "paas-broker/clusters/" + projectId)
                .headers(h -> {
                }).retrieve().bodyToMono(ClusterInfoDto.class).onErrorReturn(null).block();
    }

    @Override
    public void deleteInstance(DeleteServiceInstanceRequest request) throws Exception {
        PaasServiceInstance instance = openshiftServiceInstanceRepo.findByInstanceId(request.getServiceInstanceId());
        String createdDir = instance.getDirectoryPath();
        CompletableFuture.runAsync(() -> {
            try {
                shellCommandExecutor.exec(createdDir+PaaSBrokerConstant.TF_DESTROY_SH);
            } catch (Exception e) {
                log.error("\n===== Failed to delete Instance ======\n{}", e.getMessage());
                e.printStackTrace();
            }
            instance.setState(PaaSBrokerConstant.DELETED);
            openshiftServiceInstanceRepo.save(instance);
        });

    }
}
