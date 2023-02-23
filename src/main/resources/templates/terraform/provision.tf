provider "helm" {
  kubernetes {
    host = "${host}"
    token = "${token}"
    insecure = true
  }
}

#<#if selected_service == "redis-paas">
resource "helm_release" "redis-paas" {
  name       = "${appName}"
  namespace = "${namespace}"
  chart = "/home/redis-ha"
  timeout = 600
  cleanup_on_fail = true

  set {
    name  = "replicas"
    value = "1"
  }
}
#<#else>
#resource "helm_release" "rabbitmq-paas" {
#  name       = "${appName}"
#  namespace = "${namespace}"
#  chart = "/var/folders/charts/rabbitmq"
#  timeout = 600
#  cleanup_on_fail = true
#}

#</#if>
