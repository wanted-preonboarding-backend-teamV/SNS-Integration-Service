
# 소셜 미디어 통합 Feed 서비스
본 서비스는 유저 계정의 해시태그를 기반으로 인스타그램, 스레드, 페이스북, 트위터 등 복수의 SNS에 게시된 게시물 중 유저의 해시태그가 포함된 게시물들을 하나의 서비스에서 확인할 수 있는 통합 Feed 어플리케이션입니다.

<br/>

## Table of Contents
- [개요](#개요)
- [Skils](#skils)
- [Team](#team)
- [프로젝트 진행 및 이슈 관리](#프로젝트-진행-및-이슈-관리)
- [ERD](#erd)
- [API Reference](#api-reference)
- [Running Tests](#running-tests)
- [구현과정(설계 및 의도)](#구현과정(설계-및-의도))
- [TIL 및 회고](#til-및-회고)
- [References](#references)

<br/>


## 개요
고객이 다수의 SNS를 사용하지 않고 하나의 서비스를 통해  유저 또는 브랜드의 SNS 노출 게시물 및 통계를 확인하기 위한 서비스

<br/>


## Skils
언어 및 프레임워크: ![Static Badge](https://img.shields.io/badge/JAVA-17-blue) ![Static Badge](https://img.shields.io/badge/SpringBoot-3.1.5-green)<br/>
데이터베이스: ![Static Badge](https://img.shields.io/badge/MySQL--red)<br/>
테스트 데이터베이스: ![Static Badge](https://img.shields.io/badge/H2--red)

<br/>

## Team

| 팀원      | 담당                         |
|---------|----------------------------|
| [신민석](https://github.com/shinmin9812)  `(팀장)` | 게시물 상세 조회, 게시물 좋아요, 게시물 공유 |
| [김나윤](https://github.com/nayoonk928)      | 게시물 생성, 게시물 목록 조회          |
| [남세원](https://github.com/nswon)      | 사용자 회원가입, 가입승인, 로그인        |
| [원정연](https://github.com/jjungyeun)     | 게시물 통계                     |


<br/>


## 프로젝트 진행 및 이슈 관리

![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
<img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">
<img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white">


### 일정 관리 - Notion

[요구사항 분석 및 일정 관리 - Link](https://wonwonjung.notion.site/7169b5be3652485b82df0c1a2b639788?pvs=4)

![task-management](https://github.com/wanted-preonboarding-backend-teamV/Restaurant-Recommendation/assets/29030538/891af65f-7d4b-4963-951f-ed0f4d22ceb9)

### 이슈 관리 - Github
![issue-management](https://github.com/wanted-preonboarding-backend-teamV/Restaurant-Recommendation/assets/29030538/d8b5b358-87f7-4dfd-a3fe-67b6b4a2b70f)

<br/>

## ERD

![erd](https://github.com/wanted-preonboarding-backend-teamV/Restaurant-Recommendation/assets/29030538/f69e1fa2-c3ff-4e0b-a1da-7e987b0b887e)

<br/>


## API Reference
[API 명세서 - Notion Link](https://wonwonjung.notion.site/API-d4e42a31ea424384b4b6edb4664ea8ef?pvs=4)

<details>
<summary>Member - click</summary>

#### 회원가입

POST /members

| Parameter | Type   | Description |
| :-------- | :----- | :---------- |
| account   | string | 계정        |
| email     | string | 이메일      |
| password  | string | 비밀번호    |

#### Response

    HTTP/1.1 200
    Content-Type: application/json

    {
        "code": "183751"
    }

#### 가입승인

POST /members/approve

| Parameter | Type   | Description |
| :-------- | :----- | :---------- |
| account   | string | 계정        |
| password     | string | 비밀번호      |
| code  | string | 인증코드    |

#### Response

    HTTP/1.1 204
    Content-Type: application/json

#### 로그인

POST /members/login

| Parameter | Type   | Description |
| :-------- | :----- | :---------- |
| account   | string | 계정        |
| password     | string | 비밀번호      |

#### Response

    HTTP/1.1 200
    Content-Type: application/json

    {
        "accessToken": "12412fd12fdksr.142fdadafs.rea2r23r23f"
    }

</details>
<details>
<summary>Post - click</summary>

#### Request
POST /posts

```
{
    "contentId" : "post2",
    "title" : "맛집 투어",
    "content" : "오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.",
    "type" : "facebook",
    "viewCount" : 50,
    "likeCount" : 30,
    "shareCount" : 15,
    "hashtags" : ["맛집", "성수동"]
}
```

#### Response
    HTTP/1.1 200


#### Request
GET /posts?hashtag= &type= &orderBy= &sortBy= &searchBy= &search= &pageCount= &page=

**Query Paramter**

| Paramter  | Type   | Description                                                                                      |
|:----------|:-------|:-------------------------------------------------------------------------------------------------|
| hashtag   | string | default: 본인계정 / 맛집, 성수동 등 1건의 해시태그 이며, 정확히 일치하는 값(Exact)만 검색합니다.                                 |
| type      | string | default: 모든타입 / 게시물의 type 필드 값 별로 조회가 됩니다.                                                       |
| orderBy   | string | default: createdAt / 정렬순서이며, created_at,updated_at,like_count,share_count,view_count 가 사용 가능합니다. |
| sortBy    | string | default: desc / **★추가 구현★**  오름차순, 내림차순을 결정합니다.                                                  |
| searchBy  | string | default:모두 / 검색 기준이며, title , content, title,content 이 사용 가능합니다.                                 |
| search    | string | search_by 에서 검색할 키워드 이며 유저가 입력합니다. 해당 문자가 포함된 게시물을 검색합니다.                                        |
| pageCount | int    | default: 10 / 페이지당 게시물 갯수를 지정합니다.                                                                |
| page      | int    | default: 0 / 조회하려는 페이지를 지정합니다.                                                                   |

<details>
<summary>Parameter 형식</summary>

* type
  * facebook, instagram, x, threads
* searchBy
  * both : 제목과 내용 모두 탐색 (default)
  * title : 제목만 탐색
  * content : 내용만 탐색

</details>



#### Response


```
HTTP/1.1 200
Content-Type: application/json

{
    "content": [
        {
            "postId": 270,
            "contentId": "post1",
            "type": "INSTAGRAM",
            "title": "성수동 맛집 투어",
            "content": "오늘은 성수동 맛집 투어를 다녀왔습니",
            "viewCount": 100,
            "likeCount": 40,
            "shareCount": 10,
            "createdAt": "2023-10-30T15:56:33",
            "updatedAt": "2023-10-30T15:56:33",
            "hashtags": [
                "맛집",
                "성수동",
                "user1"
            ]
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 1,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "unpaged": false,
        "paged": true
    },
    "last": false,
    "totalPages": 3,
    "totalElements": 3,
    "size": 1,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```

#### Request
```javascript
  GET /posts/post/{postId}
```

| 파라미터     | 타입     | 설명     |
|:---------|:-------|:-------|
| `postId` | `Long` | 게시물 ID |

#### Response
```http
    HTTP/1.1 200
    Content-Type: application/json

    [
      {
	  "post_id": 10,
	  "content_id": "1",
	  "type": "X",
	  "title": "test",
	  "content": "test1234",
	  "hashtags": ["맛집", "성수동"],
	  "view_count": 1,
	  "like_count": 5,
	  "share_count": 5,
	  "created_at": "2023-10-25....",
	  "updated_at": "2023-10-25...."
      }
    ]
```

#### Request
```javascript
  POST /posts/likes/{postId}
```

| 파라미터     | 타입     | 설명           |
|:---------|:-------|:-------------|
| `postId` | `Long` | 게시물 ID |

#### Response
```http
    HTTP/1.1 200 OK
```

#### Request
```javascript
  POST /posts/shares/{postId}
```

| 파라미터     | 타입     | 설명           |
|:---------|:-------|:-------------|
| `postId` | `Long` | 게시물 ID |

#### Response
```http
    HTTP/1.1 200 OK
```
</details>
<details>
<summary>Statistics - click</summary>

#### Request

```
GET /statistics
```

##### Query Parameter
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| hashtag | string | 통계값을 볼 대상 해시태그. 기본값은 본인 계정. |
| type | string | `date`, `hour` 날짜 기준 또는 시간 기준으로 조회 |
| start | date | `2023-10-30`과 같이 날짜 형식이며 조회 기준 시작일을 의미. |
| end | date | `2023-10-30`과 같이 날짜 형식이며 조회 기준 종료일을 의미. |
| value | string | `count`, `view_count`, `like_count`, `share_count` |
| sort | string | 날짜를 `asc`(오름차순-오래된순) 또는 `desc` (내림차순-최신순)으로 정렬합니다. 시간은 항상 오름차순으로 정렬됩니다. |

#### Response
    HTTP/1.1 200
    Content-Type: application/json

```
// type=date인 경우
[
  {
    "time": "2023-10-01",
    "value": 12
  },
  {
    "time": "2023-10-02",
    "value": 4
  },
  ...
]

// type=hour인 경우
[
  {
    "time": "2023-10-01T00:00:00",
    "value": 12
  },
  {
    "time": "2023-10-01T01:00:00",
    "value": 4
  },
  ...
]
```
</details>

<br/>

## Running Tests

>  ![Static Badge](https://img.shields.io/badge/Test_Passed-54/54-green) <br/>
![test](https://github.com/wanted-preonboarding-backend-teamV/SNS-Integration-Service/assets/83534757/eee78bcc-0a82-46e9-ae94-906fa217f35f)

<br/>


## 구현과정(설계 및 의도)

<details>
<summary>게시물과 해시태그 연결성 저장 방식 - click</summary>

1. 게시물 테이블에 해시태그 리스트를 string으로 직접 넣기
2. 해시태그 테이블 생성
   - 게시물 id - 해시태그 id
   - 해시태그 정보 저장 테이블을 생성하고, 해시태그 id와 게시물id를 담은 관계 테이블 생성
3. 게시물 id - 해시태그 연결
   - 게시물 id와 해시태그(별도 테이블 연결이 아닌 단순 String 컬럼)을 저장하는 테이블 생성

#### 결론

- 해시태그 테이블을 별도로 만들어도 따로 추가될 정보가 없을 것 같고, 해시태그 값 자체가 unique하기 때문에 **3번** 방법으로 구현

</details>

<details>
<summary>Pageable 도입 - click</summary>

- 처음 코드는 정렬 정보를 enum 으로 처리하여 switch문을 사용했지만, Pageable을 이용한 정렬을 도입하면서 동적으로 정렬을 처리하고 새로운 정렬 기준이 추가되거나 변경되더라도 코드를 변경할 필요가 없게 됩니다.

</details>

<details>
<summary>외부 API 구현방법 - click</summary>

#### RestTemplate VS OpenFeign

- RestTemplate
  - HTTP 요청 후 JSON, XML, String 등의 응답을 받을 수 있다.
  - HTTP 서버와의 통신을 단순화하고 Restful 원칙을 지킨다.
  - header를 적용할 수 있다.
  - 동기적인 HTTP 요청을 한다.

- OpenFeign
  - 어노테이션을 추가하여 외부 API를 호출한다.
  - RestAPI를 사용하는 데 설정이 간편하다

#### 결론

- 사용에는 OpenFeign이 훨씬 간단하고 편리하지만 Spring Cloud 모듈을 추가하고 HttpClient가 Http2를 지원하지 않으므로 추가 설정이 필요하다.
- 또한 테스트 도구를 제공하지 않는다.
- 따라서 동기적 요청을 하지만 실제 API를 다루는 것이 아니므로 좀 더 직관적이고 테스트 코드에 사용할 수 있는 **RestTemplate**을 사용했다.

</details>

<details>

<summary>MySQL의 DATE_FORMAT으로 통계 쿼리 및 파싱 로직 최소화 - click</summary>

- 통계값을 조회하고 파싱하는 방법
  1. 조건 만족하는 기록 전체 다 불러와서 앱에서 파싱
     - 쿼리 1번이지만 조건에 해당하는 PostHistory 데이터를 전부 불러오기 때문에 애플리케이션 레이어에서 부담이 커질 수 있음.
  2. 일자 또는 시간 간격마다 조건 만족하는 기록 조회
     - 쿼리가 최대 7 * 24번 필요하지만 1번처럼 데이터를 다 불러올 필요 없이 COUNT()값만 조회할 수 있음.
  3. 한방 쿼리
     - MySQL의 DATE_FORMAT() 함수를 활용하여 쿼리 한번으로 특정 날짜 또는 시간대에 대한 COUNT()를 바로 구함. 그러나 MySQL에 의존성이 생김.

#### 결론

- 데이터가 많이 쌓이면 1번은 불가능하며, 2번은 최대 쿼리 횟수인 7*24번 정도면 속도에 영향을 줄 수 있는 정도라고 생각됨. 일단은 쿼리 한번으로 원하는 값들을 바로 불러올 수 있어 간편한 **3번** 방식으로 결정함.

</details>

## TIL 및 회고

<details>

<summary>통계 RequestParam을 Enum 타입으로 받기 - click</summary>

1. @RequestParam에 Enum을 사용하면 소문자로 들어오는 파라미터를 변환하지 못하는 문제가 있음
2. 해당 문제를 해결하기 위해 소문자로 들어온 정보를 대문자로 변환한 뒤 Enum 값과 매칭하는 변환기를 구현하고 등록함

```java
// Enum 클래스
public enum StatisticsValueType {
    COUNT,
    VIEW_COUNT,
    LIKE_COUNT,
    SHARE_COUNT;

    public static StatisticsValueType parse(String value) {
        for (StatisticsValueType type : StatisticsValueType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return COUNT;
    }

}

---------------------------------------------
// 변환기
public class StringToStatisticsValueTypeConverter implements Converter<String, StatisticsValueType> {
    @Override
    public StatisticsValueType convert(String value) {
        return StatisticsValueType.parse(value.toUpperCase());
    }
}

---------------------------------------------
// 설정
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToStatisticsValueTypeConverter());
    }
}
```

</details>

[Pageable 사용](https://www.notion.so/Pageable-750088017de0438fa42484dbacaca892?pvs=4)

<br/>

## References

 - [외부 API 구현방법](https://jie0025.tistory.com/531)
 - [RestTemplate 설정하기](https://doohee94.tistory.com/19)
 - [QueryDsl에서 MySQL DATE_FORMAT 사용하기](https://green-joo.tistory.com/51)
 - [Spring Boot 3.x.x에 QueryDsl 설정하기](https://www.inflearn.com/questions/779498/스프링-부트-3-0-querydsl-설정-관련)
 - [Pageable을 이용햔 Pagination](https://tecoble.techcourse.co.kr/post/2021-08-15-pageable/)




