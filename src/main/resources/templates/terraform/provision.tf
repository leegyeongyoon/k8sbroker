provider "helm" {
  kubernetes {
#    host = "https://localhost:6443"
#    token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkVrWmhDMmRZdnM2TW42ZlF6TkxMdXhla1hKTW1IS1h3YXFObjhTaUdMMGsifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InNhLWFkbWluLXRva2VuLTZyZHF0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InNhLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiM2JkMTUyOWItM2YxZS00OGYyLTkwOTEtNzFjZTJiZjUwMmQ1Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6c2EtYWRtaW4ifQ.Hq0nZri9iKzhfYRNVFdGn16B4TsIJ5WDGTazRl2ACUsN8OXpjP68eN26gYlAnepM0rEbYaRN64ewoDyDKQmob2Xnl4TVC8cK88LLgT2XoWtihm7W1aIiyPUmouDDHFmCdiFFLUwNDa7VZt9A5fOeWl9GMr__S__8bymbRNqMK8y0NIlsoCEdOvajP67BApNyVcNJjHPJQBXljGM4PLMC1slCuPr4cjHFFi6DODwr05HLSGq1LkPqgaQhK3rY9007nPuWE1HXdoyU43EDukFLenUesCiPjg9qy8E82sUciJVwI3_owJiq0JWqVLmpfSUTn8dkoU_eGBA9GrxYTfBcAg"
    host = "${host}"
    token = "${token}"
    insecure = true
  }
}

#<#if selected_service == "redis-paas">
resource "helm_release" "redis-paas" {
  name       = "${appName}"
  namespace = "${namespace}"
  chart = "/Users/igyeong-yun/Desktop/helm/redis-ha"
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
