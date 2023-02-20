#resource "helm_release" "redis-release" {
#  name       = "${appName}"
#  repository = "https://harbor.okestro.cld/chartrepo/test"
#  chart = "redis-ha"
#  version    = "4.14.6"
#  namespace = "${namespace}"
#  repository_username = "admin"
#  repository_password = "okestro2018"
#  repository_ca_file = "${classpath}"
##  repository_ca_file = "./harbor.crt"
#}
#
