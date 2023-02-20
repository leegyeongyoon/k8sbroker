/*
 * Developed by bhhan@okestro.com on 2020-05-11
 * Last modified 2020-05-11 13:55:47
 */

package okestro.servicebroker.service;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import okestro.servicebroker.constant.PaaSBrokerConstant;
import okestro.servicebroker.model.entity.PaasServiceInstance;
import okestro.servicebroker.repo.PaasServiceInstanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BrokerService implements ServiceInstanceService {
	@Autowired
	private PaasServiceInstanceRepo openshiftServiceInstanceRepo;

	@Autowired
	private PaasService openshiftService;
	@Autowired
	private CatalogService catalog;

	/**
	 * create service instance
	 */
	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		// log
		log.info("create service instance: id={}", request.getServiceInstanceId());
		return Mono.just(request.getServiceInstanceId()).flatMap(instanceId ->
				Mono.just(openshiftServiceInstanceRepo.existsById(request.getServiceInstanceId())).flatMap(exists -> {
					if (exists) {
						log.info("service instance existed: id={}", instanceId);

						return Mono.just(CreateServiceInstanceResponse.builder()
								.async(true)
								.instanceExisted(true)
								.build());
					} else {
							CompletableFuture.runAsync(() -> {
								String tmpdir = null;
								try {
									tmpdir = Files.createTempDirectory("paasService").toFile().getAbsolutePath();

								Gson gson = new Gson();
								PaasServiceInstance serviceInstance = PaasServiceInstance.builder()
										.instanceId(instanceId)
										.serviceId(request.getServiceDefinitionId())
										.parameters(gson.toJson(request.getParameters()))
										.state(PaaSBrokerConstant.IN_PROGRESS)
										.directoryPath(tmpdir+"/")
										.projectId(request.getParameters().get("project_id").toString())
										.build();

								openshiftServiceInstanceRepo.save(serviceInstance);

								openshiftService.createInstance(request);
								} catch (IOException | JSchException e) {
									log.error("Create instance failed : {}", e.getMessage());
								}
							});
						ServiceDefinition serviceDefinition = catalog.getServiceDefinition(request.getServiceDefinitionId()).block();

						return Mono.just(CreateServiceInstanceResponse.builder()
								.async(true)
								.dashboardUrl(serviceDefinition.getDashboardClient() == null ? "" : serviceDefinition.getDashboardClient().getRedirectUri())
								.build());
					}
				}));
	}

	/**
	 * get last operation
	 * 프로비저닝 결과 리턴
	 */
	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return Mono.just(request.getServiceInstanceId()).flatMap(instanceId ->{
			Optional<PaasServiceInstance> serviceInstance = openshiftServiceInstanceRepo.findById(instanceId);

			if (serviceInstance.isPresent()) {
				// log
				log.debug("service instance({}) state is: {}", instanceId, serviceInstance.get().getState());

				return Mono.just(GetLastServiceOperationResponse.builder()
						.operationState(OperationState.valueOf(serviceInstance.get().getState()))
						.build());
			} else {
				return Mono.error(new ServiceInstanceDoesNotExistException(instanceId));
			}
		});
	}

	/**
	 * update service instance
	 */
	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return null;
	}

	/**
	 * delete service instance
	 */
	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		// log
		log.info("delete service instance: id={}", request.getServiceInstanceId());

		// get service instance
		return Mono.just(request.getServiceInstanceId()).flatMap(instanceId -> {
			Optional<PaasServiceInstance> serviceInstance = openshiftServiceInstanceRepo.findById(instanceId);

			if (serviceInstance.isPresent()) {
				// delete instance at openstack
				CompletableFuture.runAsync(() -> {
					try {
						openshiftService.deleteInstance(request);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("\nerror : {}", e.getMessage());
					}

				});
				return Mono.just(DeleteServiceInstanceResponse.builder()
						.async(true)
						.build());
			} else {
				return Mono.error(new ServiceInstanceDoesNotExistException(instanceId));
			}
		});
	}

	/**
	 * get service instance
	 */
	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		// log
		log.info("get service instance: id={}", request.getServiceInstanceId());

		// get service instance
		return Mono.just(request.getServiceInstanceId()).flatMap(instanceId -> {
			Optional<PaasServiceInstance> serviceInstance = openshiftServiceInstanceRepo.findById(instanceId);

			if (serviceInstance.isPresent()) {
				// json to map
				Gson gson = new Gson();

				// get service definition
				ServiceDefinition serviceDefinition = catalog.getServiceDefinition(serviceInstance.get().getServiceId()).block();

				return Mono.just(GetServiceInstanceResponse.builder()
						.serviceDefinitionId(serviceInstance.get().getServiceId())
						.dashboardUrl(serviceDefinition.getDashboardClient() == null ? "" : serviceDefinition.getDashboardClient().getRedirectUri())
						.parameters(gson.fromJson(serviceInstance.get().getParameters(), new HashMap<String, Object>().getClass()))
						.build());
			} else {
				return Mono.error(new ServiceInstanceDoesNotExistException(instanceId));
			}
		});
	}
}
