###### 0. 서비스 인스턴스 생성테스트
``` shell
$ curl 'http://okestro:okestro2018!@127.0.0.1:9090/v2/service_instances/afe3fvn0-e343-4123-9vbbc-ddfsd4fasd1223?accepts_incomplete=true' -d '{
  "service_id": "de40e97e-0bb5-466c-84ef-7ec85ec7315d",
  "plan_id": "afe580b0-e344-4917-9b7c-db2b3849b782",
  "parameters": {
    "project_id": "9a1cc1c3-b41c-475c-a6eb-7c2b0c151075",
    "service_instance_nm": "g2ytest2334"
  }
}' -X PUT -H "X-Broker-API-Version: 2.14" -H "Content-Type: application/json"
```


###### 1. 서비스 카탈로그 조회
``` shell
$ curl http://id:password@localhost:8200/v2/catalog -X GET -H "X-Broker-API-Version: 2.14"
```

###### 2. 서비스 인스턴스 생성
``` shell
$ curl http://username:password@localhost:8200/v2/service_instances/:instance_id?accepts_incomplete=true -d '{
  "service_id": "service-id-here",
  "plan_id": "service-plan-id-here",
  "parameters": {
    "param1": "param1-here",
    "param2": "param2-here"
  }
}' -X PUT -H "X-Broker-API-Version: 2.14" -H "Content-Type: application/json"
```

###### 3. 서비스 프로비저닝 상태 확인
``` shell
$ curl http://username:password@localhost:8200/v2/service_instances/:instance_id/last_operation
-X GET -H "X-Broker-API-Version: 2.14"
```

###### 4. 프로비저닝 된 서비스 인스턴스 정보 확인
``` shell
$ curl http://username:password@localhost:8200/v2/service_instances/:instance_id -X GET -H "X-Broker-API-Version: 2.14"
```

###### 5. 서비스 인스턴스 삭제(Deprovisioning)
``` shell
$ curl http://username:password@localhost:8200/v2/service_instances/:instance_id?accepts_incomplete=true&service_id=service-offering-id-here&plan_id=service-plan-id-here
-X DELETE -H "X-Broker-API-Version: 2.14"
```

참고: https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md
