package okestro.servicebroker.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class PaaSBrokerConstant {

	private PaaSBrokerConstant() {

	}

	// service operation status
	public static final String IN_PROGRESS = "IN_PROGRESS";
	public static final String SUCCEEDED = "SUCCEEDED";
	public static final String FAILED = "FAILED";
	public static final String DELETED = "DELETED";


	// terraform constant
	public static final String TF_PROVISION_FILE = "provision.tf";
	public static final String FTL_PROVISION_FILE = "provision.ftl";
	public static final String TF_RUN_SH = "run.sh";
	public static final String TF_DESTROY_SH = "destroy.sh";
	public static final String PROVISION_NAMESPACE = "namespace";
	public static final String PROVISION_APP_NAME = "appName";
	public static final String PROVISION_HOST = "host";
	public static final String PROVISION_TOKEN = "token";
//	public static final String PROVISION_CA = "caFilePath";
//	public static final String PROVISION_CA_CLASSPATH = "classpath:/templates/terraform/harbor.crt";
	public static final String PROVISION_PATH = "path";
	public static final String PROVISION_SELECTED_SVC = "selected_service";
}
