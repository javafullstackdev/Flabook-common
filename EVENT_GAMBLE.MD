## 룰렛 이벤트 상세조회


### <code>GET</code> /v1/gambles/\{id\}

게임 조회

- Parameter

  | attribute| description | required | default |
      | --- | --- | -------- | --- |
  | `id`  |    |  |  |

- Response (application/json)

    * 필드
      
      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id`  | 게임ID |  |  |
      | `title`  | 버전 |  |  |
      | `maxAttempt`  | 최대시도수(0이면 제한없음) |  |  |
      | `attempt`  | 사용자 시도수 |  |  |
      | `already`  | 참여가능여부(true 이면 retryHour 이후 다시 시도) |  |  |
      | `retryHour`  | 재시도 시간 (0이면 제한 없음) |  |  |
      | `items.id`  | item ID |  |  |
      | `items.name`  | item 이름 |  |  |

```
    {
        "id": 1,
        "items": [
            {
                "id": 1,
                "name": "씨앗",
                "address": true
            },
            {
                "id": 2,
                "name": "10P",
                "address": false
            },
            {
                "id": 3,
                "name": "50P",
                "address": false
            },
            {
                "id": 4,
                "name": "100P",
                "address": false
            },
            {
                "id": 5,
                "name": "꽝",
                "address": false
            }
        ],
        "title": "게임1",
        "attempts": 0, 
        "tryCount": 0,
        "already": false,
        "retryHour": 0
    }
}
    
```


### <code>POST</code> /v1/gambles/\{id\}/share

게임 공유

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id`  |    |  |  |

- Response (application/json)

    * 필드
      
      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id`  | 게임ID |  |  |
      | `title`  | 버전 |  |  |
      | `items.id`  | item ID |  |  |
      | `items.name`  | item 이름 |  |  |

```
    https://go.flabook.co.kr/tmKuH9zS64V1TsVRA ==> https://link.flabook.co.kr/redirect?resourceType=web&resourceId=https://localhost:8080?auth%3Dtrue&&code=c7f377c4-cd11-41e7-a68a-c8b11363fe30
  
    위 주소의 referenceType, referenceId 로 새로 추가된 /v2/page/redirect api 호출하고 결과의 페이지로 접속합니다.
  
    {
        "uuid": "c7f377c4-cd11-41e7-a68a-c8b11363fe30",
        "resource": {
            "resourceId": "https://localhost:8080?auth=true",
            "resourceType": "web",
            "referenceId": "https://localhost:8080?auth=true",
            "referenceType": "web"
        },
        "deeplink": "https://go.flabook.co.kr/tmKuH9zS64V1TsVRA"
    }
```


## 룰렛 이벤트 참여

### <code>POST</code> /v1/gambles/\{id\}/bet

게임 참여

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id`  |    |  |  |

- Response (application/json)

    * 필드
      
      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id`  | 결과ID |  |  |
      | `item.id`  | 당첨 item ID |  |  |
      | `item.name`  | 당첨 item 이름 |  |  |
      | `user`  | 당첨자 정보|  |  |
      | `needAddress`  | 주소입력 여부 (null : 입력예외, false : 이미 입력됨, true : 입력 필요) |  |  |
      

```
    {
        "id": 3567,
        "item": {
            "id": 2,
            "name": "10P",
            "address": false
        },
        "user": {
            "id": 2,
            "nickname": "모야모1"
        },
        "needAddress": null
    }
```

## 룰렛 이벤트 주소입력

### <code>POST</code> /v1/gambles/address

주소입력

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `roadAddress`  |  도로명주소  | true |  |
  | `detailAddress`  |  상세주소  | true |  |
  | `postCode`  | 우편번호   | true |  |
  | `name`  | 수령자이름   |  | true |
  | `phone1`  | 전화번호1   |  | true |
  | `phone2`  | 전화번호2   |  |  |

- Response (application/json)

    * 필드
      
      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id`  | 결과ID |  |  |
      | `item.id`  | 당첨 item ID |  |  |
      | `item.name`  | 당첨 item 이름 |  |  |
      | `user`  | 당첨자 정보|  |  |
      | `needAddress`  | 주소입력 여부 (null : 입력예외, false : 이미 입력됨, true : 입력 필요) |  |  |
      
```
    * 당첨메시지는 상위 'resultMsg' 를 보여주면 됩니다.
  
    {
        "id": 3567,
        "item": {
            "id": 2,
            "name": "10P",
            "address": false
        },
        "user": {
            "id": 2,
            "nickname": "모야모1"
        },
        "needAddress": null
    }
```

## 주소 미입력시 당첨 취소 호출

### <code>DELETE</code> /v1/gambles/results/\{id\}

주소 미입력시 당첨 취소 호출

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id`  |  결과ID  |  |  |

- Response (application/json)

    * 필드

```    
```
