#Flabook Server API DOC 



- The result code is excluded because the basic format of the REST API server is not defined.
- Error message not defined


## History

| document version | Created on| description  | Author|
| -------- | ---------- | ---------------- | --------|
| 0.1 | 2020-05-07 | First written | Park Young-soo |
| 0.1.1 | 2020-05-11 | Added API related to user profile lookup. | Park Young-soo |
| 0.1.2 | 2020-05-12 | Add list of answers api field (mentioned.nickname)<br>Add Notification Read Processing API.<br>Add Get Notification, Cancel API.<br>Add Report (Post, Comment) API | Park Young-soo |
| 0.1.3 | 2020-05-13 | Token Change | Park Young-soo |
| 0.1.4 | 2020-05-15 | Add book search, auto-completion, and search suggestions | Park Young-soo |
| 0.1.5 | 2020-05-21 | Add post edit api<br> Post search (/v2/search) View only my posts (`mine`) Add filter | Park Young-soo |
| 0.1.6 | 2020-05-22 | Edit reply Add api | Park Young-soo |
| 0.1.7 | 2020-05-26 | Add Share api<br> Post / Post / Edit Post Add title field (bragging only)<br> Sort Search Add mostComments | Park Young-soo |
| 0.1.8 | 2020-06-01 | Add shop product search api [Search Product](#<code>GET</code>--/v2/shops/goods) <br> Search my profile Add `provider` field [Search My Profile](#<code>GET </code>--/v2/users/me) | Park Young-soo |
| 0.1.9 | 2020-06-04 | ~~~Add shop access address inquiry API [Home moving address inquiry](#<code>GET</code> /v2/shops/home) <br> [Cart moving address inquiry](#<code>GET</code > /v2/shops/cart) <br> [Search order list moving address](#<code>GET</code> /v2/shops/orderDto)~~~ <br> [FCM notification broadcast test api](# <code>GET</code>--/v2/shops/goods/test)| Park Young-soo |
| 0.1.10 | 2020-06-08 | Add book sharing api [Share](#<code>GET</code>--/v2/dictionaries/{name}/share)| Park Young-soo |
| 0.1.11 | 2020-06-11 | Change of shop access address | Park Young-soo |
| 0.1.12 | 2020-06-15 | Added search result count API. <br>[Search the number of search results](#<code>GET</code>--/v2/search/count) <br> [Search the number of product search results](#<code>GET</code>-- /v2/shops/goods/count) <br> [Search the number of search results for recommended products](#<code>GET</code>--/v2/shops/goods/recommended/count)| Park Young-soo |
| 0.1.13 | 2020-06-17 | Added product search number of reviews field. (`reviewCnt`) | Park Young-soo |
| 0.1.14 | 2020-06-23 | Added guidebook poster entity.(`posters`) | Park Young-soo |
| 0.1.15 | 2020-07-01 | App release version lookup | Park Young-soo |
| 0.1.16 | 2020-07-06 | Add shopping mall mileage, mileage/deposit integrated inquiry, shopping mall address (mileage, refund, shipping) | Park Young-soo |
| 0.1.17 | 2020-07-08 | Modification of notification reception settings/addition of inquiry API. [View my notification settings](#<code>GET</code>-/v2/notifications/setting) [Edit my notification settings](#<code>POST</code>-/v2/notifications/setting) | Park Young-soo |
| 0.1.18 | 2020-07-13 | Add Sort by Product Lookup (`orderby`) , Add Photo Resolution field (dimension) | Park Young-soo |
| 0.1.19 | 2020-07-30 | Added post sort option `sortby`. [Policy](#Policy) Policy (Privacy Policy, Terms of Use, Location Information Terms of Use) | Park Young-soo |
| 0.1.20 | 2020-08-03 | Edited to allow multiple comment users to mention, and added a comment user (`@`) query API. [Register reply](#<code>POST</code>--/v2/postings/{id}/comments), [Register large comments](#<code>POST</code>--/v2/comments/ {id}/reply), [Post Participant List](#<code>GET</code>--/v2/postings/{id}/users)<br> - The mentioned user searches the participant list and is selected from the list Only the user (`@[{user-id}])`) is valid, and the manually entered @nickname is saved as simple text and the color is the same as plain text.<br><br> Add product exposure field on post detail page ( `goodses`) | Park Young-soo |
| 0.1.21 | 2020-08-04 | Add notification setting field [View my notification settings](#<code>GET</code>-/v2/notifications/setting) [Modify my notification settings](#<code>POST</code>-/v2/notifications/ setting) | Park Young-soo |
| 0.1.22 | 2020-08-05 | When the attribute `resourceType` of `resource` of notification list and PUSH notification is `shop`, search the page address to go to `resourceId` <br> [Search the address to go to resource](#<code>GET</code> /v2 /shops/page/redirect) | Park Young-soo |
| 0.1.23 | 2020-08-06 | Add notification setting field `replyNotiEnable`[lookup my notification settings](#<code>GET</code>-/v2/notifications/setting) | Park Young-soo |
| 0.1.24 | 2020-08-12 | Added notification settings and login fields. `adNotiConfirmedAt`, `adNotiAgreement` <br> - When logging in, if `adNotiAgreement` is null, a pop-up appears<br> - Upon agreeing to receive, only the `adNotiAgreement` field is updated <br><br> [Inquire my notification settings](#< code>GET</code>-/v2/notifications/setting) | Park Young-soo |
| 0.1.25 | 2020-08-13 | Added banner lookup api. [Banner inquiry](#<code>GET</code>--/v2/banners)<br> Added notice inquiry api. [Search notices](#<code>GET</code>--/v2/notices) | Park Young-soo |
| 0.1.26 | 2020-08-18 | [Policy](#Policy) Notice added. | Park Young-soo |
| 0.1.27 | 2020-09-18 | [Suggested search terms]() Edit search suggestions | Park Young-soo |
| 0.1.28 | 2020-09-21 | Modify [App release version lookup](#<code>GET</code>-/v2/notices/apps/release), [Upload client log](#<code>GET</code>-/v2/logs) , [View my notification settings, edit my notification settings](#<code>GET</code>-/v2/notifications/setting) Add field (joinCommentNotiEnable) , Add ResourceType (web - go to id address) | Park Young-soo |
| 0.1.29 | 2020-09-23 | [Search Users](#<code>GET</code>-/v2/search/users) | Park Young-soo |
| 0.1.30 | 2021-01-07 | [Search web resource address](#<code>GET</code>-/v2/webs/page/redirect) | Park Young-soo |
| 0.1.31 | 2021-01-11 | [Event popup inquiry](#<code>GET</code>-/v2/events (same structure as notice)) | Park Young-soo |
| 0.1.32 | 2021-02-04 | Clinic post registration, update, and inquiry field added. (condition) | Park Young-soo |
| 0.1.33 | 2021-02-18 | [Search similar posts](#<code>GET</code>--/v2/postings/{id}/similarContent) Add field (`offset`, `count`, `orderby`, `sortby`) <br> [Register large comments](#<code>POST</code>--/v2/comments/{id}/:commentType) Thank you, add category of acceptance <br> <br> [Search for response](#<code >GET</code>--/v2/postings/{id}/comments/adopt) Add | Park Young-soo |
| 0.1.34 | 2021-02-18 | [View my notification settings](#<code>GET</code>-/v2/notifications/setting) [Edit my notification settings](#<code>POST</code>-/v2/notifications/setting) All notifications Added setting (top condition notification setting) field (notiEnable) | Park Young-soo |
| 0.1.35 | 2021-02-22 | Adds the number of badges acquired and the representative badge search when viewing the profile.<br> Adds the profile search field (badgeCount, representBadge) <br> <br> Adds the badge search api. [Get Badge Inquiry](#<code>GET</code>--/v2/users/me/badges) <br> Add a representative badge setting api. [Modify My Representative Badge](#<code>POST</code>--/v2/users/me/badges/{id}/representBadge) <br> Add another user's badge inquiry api. [View other users' badges](#<code>GET</code>--/v2/users/{id}/badges)| Heo Jeong-in
| 0.1.36 | 2021-02-23 | Add [Inquire My Notification Settings](#<code>GET</code>-/v2/users/me/adopts) | Park Young-soo |
| 0.1.37 | 2021-03-29 | [Ranking Search](#<code>GET</code>-/v2/rankings), [Ranking Sort Search](#<code>GET</code>-/v2/rankings/order) added | Park Young-soo |
| 0.1.38 | 2021-05-06 | Add photo, photo album function api | Heo Jeong-in |
| 0.1.39 | 2021-05-14 | Add detailed search in photo album, View Collection-Add photo, Add [Popular Writer](#<code>GET</code>--/v2/photoAlbums/popularWriters), Add user's popular photo | Heo Jeong-in |
| 0.1.40 | 2021-05-26 | [Search the number of photos in a photo album](#<code>GET</code>--/v2/photoAlbums/{id}/photoCount) Add api, add profile lookup field (photoEnable) | Heo Jeong-in |

## index

index
[Server Information] (#Server Information)
[API](#API)
1. [Post Search](#Post Search)

- [View all](#<code>GET</code>--/v2/home)
- [More](#<code>GET</code>--/v2/search)
- [Search result count](#<code>GET</code>--/v2/search/count)
- [Post Details](#<code>GET</code>--/v2/postings/{id})
- [Post details activity information-(Like, Get notifications, scrap)](#<code>GET</code>--/v2/postings/{id}/activity)
- [Register post](#<code>POST</code>--/v2/postings)
- [Edit post](#<code>PUT</code>--/v2/postings/{id})
- [Delete post](#<code>DELETE</code>--/v2/postings/{id})
- [Like](#<code>POST</code>--/v2/postings/{id}/like)
- [Unlike Like](#<code>DELETE</code>--/v2/postings/{id}/like)
- [Scrap](#<code>POST</code>--/v2/postings/{id}/scrap)
- [Cancel scrap](#<code>DELETE</code>--/v2/postings/{id}/scrap)
- [Report](#<code>POST</code>--/v2/postings/{id}/report)
- [Receive notifications](#<code>POST</code>--/v2/postings/{id}/watch)
- [Cancel notification](#<code>DELETE</code>--/v2/postings/{id}/watch)
- [Share](#<code>POST</code>--/v2/postings/{id}/share)
- [Post Participant List](#<code>GET</code>--/v2/postings/{id}/users)
- [Reply list search](#<code>GET</code>--/v2/postings/{id}/comments)
- [Register reply](#<code>POST</code>--/v2/postings/{id}/comments)
- [Register for comments](#<code>POST</code>--/v2/comments/{id}/:commentType)
- [Edit reply](#<code>PUT</code>--/v2/comments/{id})
- [Delete reply](#<code>DELETE</code>--/v2/comments/{id})
- [Report reply](#<code>POST</code>--/v2/comments/{id}/report)
- [Search similar posts](#<code>GET</code>--/v2/postings/{id}/similarContent)

2. [Profile Lookup](#Profile Lookup)

- [Search My Profile](#<code>GET</code>--/v2/users/me)
- [My Likes](#<code>GET</code>--/v2/users/me/likes)
- [My scraps list](#<code>GET</code>--/v2/users/me/scraps)
- [My Posts List](#<code>GET</code>--/v2/users/me/postings)
- [List of my replies](#<code>GET</code>--/v2/users/me/comments)
- [My Adopted Posts List](#<code>GET</code>--/v2/users/me/adopts)
- [My Notification List](#<code>GET</code>--/v2/notifications)
- [View my notification settings](#<code>GET</code>-/v2/notifications/setting)
- [Modify my notification settings](#<code>POST</code>-/v2/notifications/setting)
- [Search User](#<code>GET</code>-/v2/search/users)

- [Process all my notifications read](#<code>POST</code>--/v2/notifications/markRead)
- [Process my notifications individually read](#<code>POST</code>--/v2/notifications/{id}/markRead)

- [Get Badge Inquiry](#<code>GET</code>--/v2/users/me/badges)
- [Modify My Representative Badge](#<code>POST</code>--/v2/users/me/badges/{id}/representBadge)
- [View other users' badges](#<code>GET</code>--/v2/users/{id}/badges)

3. [Search term](#Search term)
- [recommended search term](#<code>GET</code>--/v2/tags/recommended)
- [autocomplete](#<code>GET</code>--/v2/tags/autocomplete)

4. [Encyclopedia](#Encyclopedia)
- [Lookbook search](#<code>GET</code>--/v2/dictionaries/{name})
- [Share](#<code>GET</code>--/v2/dictionaries/{name}/share)

5. [Shopping Mall](#Shopping Mall)
- [Product Search](#<code>GET</code>--/v2/shops/goods)
- [Search the number of product search results](#<code>GET</code>--/v2/shops/goods/count)
- [Search for recommended products](#<code>GET</code>--/v2/shops/goods/recommended)
- [Search the number of search results for recommended products](#<code>GET</code>--/v2/shops/goods/recommended/count)

- [Search shopping mall address](#<code>GET</code>--/v2/shops/page/{name})
- [Inquire product detail moving address](#<code>GET</code>--/v2/shops/page/goods/{id})
- [Deposit inquiry](#<code>GET</code>--/v2/shops/deposit)
- [Mileage inquiry](#<code>GET</code>--/v2/shops/mileage)
- [deposit/mileage combined inquiry](#<code>GET</code>--/v2/shops/wallet)

- [FCM notification broadcast test api](#<code>GET</code>--/v2/shops/goods/test)

6. [Notice](#Notice)
- [Search app release version](#<code>GET</code>-/v2/notices/apps/release)

7. [Banner](#banner)
- [Banner Query](#<code>GET</code>--/v2/banners)

8. [Policy](#Policy)
- [Policy Query](#<code>GET</code>--/v2/policies/page/:name)

9. [Log](#Log)
- [Client log upload](#<code>GET</code>-/v2/logs)

10. [resource](#resource)
- [Search for moving address to shop resource](#<code>GET</code>--/v2/shops/page/redirect)
- [Search for moving address to integrated resource](#<code>GET</code>--/v2/page/redirect)

11. [Ranking](#Ranking)
- [Ranking inquiry](#<code>GET</code>--/v2/rankings)
- [Ranking sort inquiry](#<code>GET</code>--/v2/rankings/order)

12. [FCM](#FCM)

13. [Photo] (#Photo)

- [Search photo album](#<code>GET</code>--/v2/photoAlbums?userId={userId})
- [View detailed photo album](#<code>GET</code>--/v2/photoAlbums/{photoAlbumId})
- [My Page Representative Photos, Popular Photos](#<code>GET</code>--/v2/photoAlbums/representPhotos?userId={userId})


- [Create photo album](#<code>POST</code>--/v2/photoAlbums)
- [Edit photo album](#<code>PUT</code>--/v2/photoAlbums/{id})
- [Delete photo album](#<code>DELETE</code>--/v2/photoAlbums/{id})
- [Register photo in photo album](#<code>POST</code>--/v2/photoAlbums/me/{id})
- [Search number of photos in photo album](#<code>GET</code>--/v2/photoAlbums/{id}/photoCount)


- [Photo Registration](#<code>POST</code>--/v2/photos)
- [Edit photo](#<code>PUT</code>--/v2/photos/{id})
- [Delete photo](#<code>PUT</code>--/v2/photos/delete)
- [Move photo](#<code>PUT</code>--/v2/photos/move)
- [Photocopy](#<code>PUT</code>--/v2/photos/copy)
  
- [Photo Search](#<code>GET</code>--/v2/photos/{id})
- [Like photo](#<code>POST</code>--/v2/photos/{id}/like)
- [Cancel like photo](#<code>DELETE</code>--/v2/photos/{id}/like)
- [Report photo](#<code>POST</code>--/v2/photos/{id}/report)

- [by photo](#<code>GET</code>--/v2/search)
- [by author](#<code>GET</code>--/v2/search/writerUsers)
- [Popular Writers](#<code>GET</code>--/v2/photos/popularWriters)


- [Share photo album](#<code>POST</code>--/v2/photoAlbums/{id}/share)


## Server information

### URL

| Category | |
| ------------------ | --------------------- |
| Dev | http://flabook-develop-rest-1302610380.ap-northeast-2.elb.amazonaws.com/ |
| Staging | - |
| Production | - |

### token

| token | user | Description | |
| -------- | ---------- | ---------------- | --------|
| Bearer a | User1 | | |
| Bearer b | User2 | | |
| Bearer c | User3 | | |
| | | | |




# API

### common

- Header

| attribute | description | required | default |
| --- | --- | -------- | --- |
| `Authorization` | auth key `Bearer {token}` | `true` | --- |

- Response

| attribute | description | default |
| -------------- | ------------------ | -------------------------- |
| resultCode | result code | `true` | 1000 : Normal<br>9000 : Error |
| resultData | {object} || of each result
| resultMsg | result message ||

```
{
resultCode: 1000
resultData : {Object}
resultMsg: "Message"
    }
  ```



### Enumeration

- PostingType

```
     - question("Ask a question", Question.class),
     - boast("bragging", Boost.class),
     - clinic("Plant Clinic", Clinic.class),
     - guidebook("guidebook", Guide.class),
     - free("free bulletin board", Free.class),
     - magazine("magazine", Magazine.class),
     - photo("Photo", Photo.class)

```


- ResourceType

```
  # each post
  boast
  clinic
  free
  guide
  magazine
  question
  photo
  
  comment : comment
  reply : reply
  likeposting : Like a post,
  likecomment (disabled)
  advertisement: advertisement,
  shop : related to shopping,
  web : access webview
```

- EventType

```
    NEW_QUESTION
    NEW_BOAST,
    NEW_CLINIC,
    NEW_GUIDE,
    NEW_MAGAZINE,
    NEW_COMMENT,
    NEW_LIKE_POSTING,
    NEW_LIKE_COMMENT,
    NEW_AD
```

## Post inquiry

### <code>GET</code> /v2/home

'Collect'

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `postingType` | Post Type | |`question`|
    | `q` | search terms | ||

- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `posters` | Guidebook poster | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
        ```
         [
                {
                    "id": 28,
                    "text": "boast",
                    "readCount": 0,
                    "commentCount": 19,
                    "likeCount": 2,
                    "attachments": [
                        {
                            "id": 32,
                            "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                            "description" : "", #optional,
                            "dimension" : "1080,920"
                        }
                    ],
                    "posters": [
                           {
                               "id": 32,
                               "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                               "description" : "", #optional
                                "dimension" : "1080,920"
                    }],
                    "owner": {
                        "id": 1,
                        "nickname": "user1",
                        "levelInfo": {
                            "badge": "/assets/img/badges/badge0.png",
                            "level": 1,
                            "name": "general"
                        }
                    },
                    "postingType": "boast",
                    "createdAt": "2020-04-23T15:09:53+09:00",
                    "modifiedAt": "2020-04-24T15:50:11+09:00"
                }
         ]
     ```


### <code>GET</code> /v2/search

Search / search for a list of 'More' posts

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `postingType` | Post Type | |`question`|
    | `q` | search terms | ||
    | `offset` | if orderby is 'popular' | | |
    | `sinceId` | When orderby is 'id', search timeline | | |
    | `maxId` | When orderby is 'id', search timeline | | |
    | `count` | number of listings | | `10`|
    | `orderby` | sort (`id` or `popular` or `mostComments`) | | `id` |
    | `sortby` | sort (`desc` or `asc`) | | `asc` if postingType is guide and `desc` |
    | `mine` | View only my posts | | false |
    | `mostComments` | Filter by number of comments | | false |
    | `day` | Search period (days) | | false |

    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `posters` | Guidebook poster | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `isScrap` | postingType Whether or not to be exposed depends on the screen<br> Magazine, only to brag | | |

   * success response
   
        ```
         [
                {
                    "id": 28,
                    "text": "boast",
                    "readCount": 0,
                    "commentCount": 19,
                    "likeCount": 2,
                    "attachments": [
                        {
                            "id": 32,
                            "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                            "description" : "", #optional
                            "dimension" : "1080,920"
                        }
                    ],
                     "posters": [
                        {
                            "id": 32,
                            "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                            "description" : "", #optional
                            "dimension" : "1080,920"
                        }
                     ],
                    "owner": {
                        "id": 1,
                        "nickname": "user1",
                        "levelInfo": {
                            "badge": "/assets/img/badges/badge0.png",
                            "level": 1,
                            "name": "general"
                        }
                    },
                    "postingType": "boast",
                    "createdAt": "2020-04-23T15:09:53+09:00",
                    "modifiedAt": "2020-04-24T15:50:11+09:00",
                    "isScrap" : false
                }
         ]
        ```


### <code>GET</code> /v2/search/count

Search the number of search results for posts

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `postingType` | Post Type | |`question`|
    | `q` | search terms | ||
    | `sinceId` | When orderby is 'id', search timeline | | |
    | `maxId` | When orderby is 'id', search timeline | | |
    | `orderby` | sort (`id` or `popular` or `mostComments`) | | `id` |
    | `mine` | View only my posts | | false |
    
- Response (application/json)

   * success response
   
```
{number}
```



### <code>GET</code> /v2/postings/{id}

Post details

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `posters` | Guidebook poster | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `isLike` | Like or not | | |
       | `goods` | Product exposure field (list but only the first one) | | |

   * success response
   
```
         
        {
            "id": 28,
            "text": "boast",
            "readCount": 0,
            "commentCount": 19,
            "likeCount": 2,
            "attachments": [
                {
                    "id": 32,
                    "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                    "description" : "", #optional
                    "dimension" : "1080,920"
                }
            ],
            "posters": [
            {
            "id": 32,
            "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
            "description" : "", #optional
                    "dimension" : "1080,920"
            }
            ],
            "owner": {
                "id": 1,
                "nickname": "user1",
                "levelInfo": {
                    "badge": "/assets/img/badges/badge0.png",
                    "level": 1,
                    "name": "general"
                }
            },
            "goods": [
                {
                    "goodsNo": "1000001772",
                    "photoUrl": "http://flabook.godomall.com/data/goods//70/01/01/1000001772/1000001772_main_074.jpg",
                    "reviewCnt": 0,
                    "resource": {
                        "resourceId": "http://flabook-develop-rest-1302610380.ap-northeast-2.elb.amazonaws.com/v2/shops/goods/1000001772",
                        "resourceType": "shop",
                        "referenceId": "http://flabook-develop-rest-1302610380.ap-northeast-2.elb.amazonaws.com/v2/shops/goods/1000001772",
                        "referenceType": "shop"
                    },
                    "name": "[Flower Seed] Blue Daisy (Byeolchunghwaguk, Felicia) 20",
                    "price": 1500,
                    "discountedPrice": 1500
                }
            ],
            "postingType": "boast",
            "createdAt": "2020-04-23T15:09:53+09:00",
            "modifiedAt": "2020-04-24T15:50:11+09:00",
            "isLike" : true
        }
 
```
   
### <code>GET</code> /v2/postings/{id}/activity

Post activity view

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `isLike` | Like or not | | |
       | `isWatch` | Whether to set notifications | | |
       | `isScrap` | Whether scrap | | |

   * success response
   
```

       {
               "isLike": false,
               "isScrap": false,
               "isWatch": true
       }
       
```

### <code>POST</code> /v2/postings

Post registration

- Parameter (multipart/form)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `text` | Written content | true ||
    | `files` | File (multiple) | true ||
    | `descriptions` | File Description (multiple) | ||
    | `postingType` | type |true | |
    | `place` | clinic additional information <br> INDOOR("indoor"), OUTDOOR("outdoor"), VERANDA("veranda")|true | |
    | `water` | Clinic Additional Information <br> PERIOD("Once periodically"), LITTLE("Slightly often"), THINK("Once every time I remember"), HARD_DRY("After the soil is dry enough"), DRY ("After the topsoil is dry"), ETC("Other") |true | |
    | `light` | Clinic Additional Information <br> SHADE("Shadow"), SUN("Sunshine"), HALF("Half-sun/Half-shade") |true | |
    | `reporting` | Clinic Additional Information <br> UNDER_TWO_WEEKS("within 2 weeks"), UNDER_TWO_YEARS("within 2 years"), OVER_TWO_YEARS("after 2 years"), NONE("none") |true | |
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `condition.place` | Clinic Additional Information<br> INDOOR("Indoor"), OUTDOOR("Outdoor"), VERANDA("Veranda") | | |
       | `condition.light` | Clinic Additional Information <br> SHADE("Shadow"), SUN("Sunshine"), HALF("Halflighted/Half-shaded") | | |
       | `condition.water` | Clinic Additional information<br> PERIOD("Once periodically"), LITTLE("Little and often"), THINK("Once every time I remember"), HARD_DRY("After the soil is dry enough"), DRY ("After the top soil dries, plenty of")| | |
       | `condition.repotting` | Clinic Additional Information<br> UNDER_TWO_WEEKS("within 2 weeks"), UNDER_TWO_YEARS("within 2 years"), OVER_TWO_YEARS("after 2 years"), NONE("none")| | |

   * success response
   
```
         
        {
            "id": 28,
            "text": "boast",
            "readCount": 0,
            "commentCount": 19,
            "likeCount": 2,
            "attachments": [
                {
                    "id": 32,
                    "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                    "description" : "", #optional
                    "dimension" : "1080,920"
                }
            ],
            "owner": {
                "id": 1,
                "nickname": "user1",
                "levelInfo": {
                    "badge": "/assets/img/badges/badge0.png",
                    "level": 1,
                    "name": "general"
                }
            },
            "postingType": "boast",
            "createdAt": "2020-04-23T15:09:53+09:00",
            "modifiedAt": "2020-04-24T15:50:11+09:00",
            "condition": { # Clinic only
                "place": "INDOOR",
                "light": "SHADE",
                "water": "DRY",
                "repotting": "NONE"
            }
        }
 
```

### <code>PUT</code> /v2/postings/{id}

edit post

- Parameter (multipart/form)

```
        1. The attachment modification has a files, descriptions, ids pair. (must include all keys)
        2. ids is used to keep the existing upload data, and if there is no existing attachment.id, it is deleted.
        3. Sort the upload files by key pair.
```

*
    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `text` | Written content | true ||
    | `files` | File (multiple) | true ||
    | `descriptions` | File Description (multiple) |||
    | `ids` | attachment.id (multiple) |||
    

- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `posters` | Guidebook poster | | |

   * success response
   
```
         
        {
            "id": 28,
            "text": "boast",
            "readCount": 0,
            "commentCount": 19,
            "likeCount": 2,
            "attachments": [
                {
                    "id": 32,
                    "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                    "description" : "", #optional
                    "dimension" : "1080,920"
                }
            ],
             "posters": [
                     {
                         "id": 32,
                         "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                         "description" : "", #optional
                         "dimension" : "1080,920"
                     }
                 ]
            "owner": {
                "id": 1,
                "nickname": "user1",
                "levelInfo": {
                    "badge": "/assets/img/badges/badge0.png",
                    "level": 1,
                    "name": "general"
                }
            },
            "postingType": "boast",
            "createdAt": "2020-04-23T15:09:53+09:00",
            "modifiedAt": "2020-04-24T15:50:11+09:00"
        }
 
```

### <code>DELETE</code> /v2/postings/{id}

delete post

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post id |||

- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `posters` | Guidebook poster | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
```
         
        {
            "id": 28,
            "text": "boast",
            "readCount": 0,
            "commentCount": 19,
            "likeCount": 2,
            "attachments": [
                {
                    "id": 32,
                    "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                    "description" : "", #optional
                    "dimension" : "1080,920"
                },
            ],
            "posters": [
                          {
                              "id": 32,
                              "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                              "description" : "", #optional
                            "dimension" : "1080,920"
            }],
            "owner": {
                "id": 1,
                "nickname": "user1",
                "levelInfo": {
                    "badge": "/assets/img/badges/badge0.png",
                    "level": 1,
                    "name": "general"
                }
            },
            "postingType": "boast",
            "createdAt": "2020-04-23T15:09:53+09:00",
            "modifiedAt": "2020-04-24T15:50:11+09:00"
        }
 
```




### <code>POST</code> /v2/postings/{id}/like

like

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `id` | Post ID | ||
    
- Response (application/json)

    * success response
   
```

     true, false
    
```

### <code>DELETE</code>  /v2/postings/{id}/like
Like Cancel

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `id` | Post ID | ||
    
- Response (application/json)

    * success response
   
```

true, false

```
   

### <code>POST</code> /v2/postings/{id}/scrap

scrap

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `id` | Post ID | ||
    
- Response (application/json)

    * success response
   
```

true, false

```

### <code>DELETE</code> /v2/postings/{id}/scrap

Cancel scrap

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `id` | Post ID | ||
    
- Response (application/json)

    * success response
   
```

true, false

```
   
### <code>POST</code> /v2/postings/{id}/report

Report it

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    | `title` | Reported items | ||
    | `text` | content | ||
    
- Response (application/json)

   * success response
   
```
    true, false
```
   

### <code>POST</code> /v2/postings/{id}/watch

Get notified

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||

- Response (application/json)

   * success response
   
```

true, false

```


### <code>DELETE</code> /v2/postings/{id}/watch

Cancel notifications

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    
- Response (application/json)

   * success response
   
```

true, false

```


### <code>POST</code> /v2/postings/{id}/share
`TODO`
Share (specifications are not organized)

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||

- Response (application/json)

   * success response
    
```

        {
                "id": "e997176e-c924-40c3-b992-e54e2e5fccca",
                "resource": {
                    "resourceId": "10004",
                    "resourceType": "question",
                    "referenceId": "10004",
                    "referenceType": "question"
                },
                "deeplink": "https://devflabook.page.link/kPLjjM6siR6B6qt6A"
            }
            
```

   * Link definition
     
```
            https://devflabook.page.link/kPLjjM6siR6B6qt6A
            
            -> https://link.flabookplus.co.kr/postings/10004?postingType=question&code=7041a745-45d3-44a8-8ebd-cf179ad24f1e
```


### <code>GET</code> /v2/postings/{id}/users
List of post participants (for searching nicknames mentioned in comments)

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    | `q` | search terms | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | User ID | | |
       | `nickname` | Nickname | | |

   * success response
   
        ```
        [
            {
                "id": 111,
                "nickname": "111"
            }
        ]
        ```


### <code>GET</code> /v2/postings/{id}/comments

View reply list

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `isLike` | Like or not | | |

   * success response
   
        ```
        [
            {
                "id": 111,
                "text": "111",
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "createdAt": "2020-05-07T08:42:46.554+09:00",
                "modifiedAt": "2020-05-07T08:42:46.554+09:00",
                "isLike": false
            }
           
        ]
        ```
    
### <code>POST</code> /v2/postings/{id}/comments

Register a reply

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | true ||

- Parameter (multipart/form)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `text` | content, user mention `@[{user-id}]` | true | |
    | `files` | Upload File | | |
    

- Response (application/json)

   * field
   
 | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content, replace `@[{user-id}]` with your username | | |
       | `plainText` | content original | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `mentions` | List of mentioning users (id, nickname), you need to replace `@[{user-id}]` in plainText with your nickname. | | |

   * success response
   
        ```
        [
            {
                "id": 111,
                "text": "111",
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "createdAt": "2020-05-07T08:42:46.554+09:00",
                "modifiedAt": "2020-05-07T08:42:46.554+09:00"
            }
           
        ]
        ```


### <code>POST</code> /v2/comments/{id}/:commentType

Register for comments

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | comment you want to mention id | true ||
    | `commentType` | reply, adopt, thanks | true ||
    
- Parameter (multipart/form)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `text` | content, user mention `@[{user-id}]` | true ||
    | `files` | Upload File | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content, replace `@[{user-id}]` with your username | | |
       | `plainText` | content original | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |
       | `mentioned` | Mentioned User Information | | |
       
* success response
  
        ```
        [
            {
                "id": 111,
                "text": "111",
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "createdAt": "2020-05-07T08:42:46.554+09:00",
                "modifiedAt": "2020-05-07T08:42:46.554+09:00",
                "mentioned": {
                    "nickname": "Mart 1 (Ansan)"
                }
            }
           
        ]
        ```
   
     
    
    
### <code>PUT</code> /v2/comments/{id}

Edit comment

- Parameter (multipart/form)

```
    1. The attachment modification has a files, descriptions, ids pair. (must include all keys)
    2. ids is used to keep the existing upload data, and if there is no existing attachment.id, it is deleted.
    3. Sort the upload files by key pair.
```


- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | comment you want to mention id | true ||

- Parameter (multipart/form)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `text` | content | true ||
    | `ids` | attachment.id, if not, delete existing image | ||
    | `files` | Upload File | ||


 * success response
   
        ```
        [
            {
                "id": 111,
                "text": "111",
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "createdAt": "2020-05-07T08:42:46.554+09:00",
                "modifiedAt": "2020-05-07T08:42:46.554+09:00"
            }
           
        ]
        ```
     

### <code>DELETE</code> /v2/comments/{id}

Delete comment

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | comment id | true ||


 * success response
   
```
    {
        "id": 111,
        "text": "111",
        "owner": {
            "id": 1,
            "nickname": "user1",
            "levelInfo": {
                "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                "level": 1,
                "name": "general"
            }
        },
        "createdAt": "2020-05-07T08:42:46.554+09:00",
        "modifiedAt": "2020-05-07T08:42:46.554+09:00"
    }

```

### <code>GET</code> /v2/postings/{id}/comments/first

First reply inquiry

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | ID | | |
      | `text` | content | | |
      | `attachments` | Attachment List | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
      | `isLike` | Like or not | | |

    * success response

         ```
         {
             "id": 111,
             "text": "111",
             "owner": {
                 "id": 1,
                 "nickname": "user1",
                 "levelInfo": {
                     "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                     "level": 1,
 "name": "general"
                 }
             },
             "createdAt": "2020-05-07T08:42:46.554+09:00",
             "modifiedAt": "2020-05-07T08:42:46.554+09:00",
             "isLike": false
         }
         ```



### <code>GET</code> /v2/postings/{id}/comments/adopt

Accepted answer inquiry

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | ID | | |
      | `text` | content | | |
      | `attachments` | Attachment List | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
      | `isLike` | Like or not | | |

    * success response

         ```
         {
             "id": 111,
             "text": "111",
             "owner": {
                 "id": 1,
                 "nickname": "user1",
                 "levelInfo": {
                     "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                     "level": 1,
                     "name": "general"
                 }
             },
             "createdAt": "2020-05-07T08:42:46.554+09:00",
             "modifiedAt": "2020-05-07T08:42:46.554+09:00",
             "isLike": false
         }
         ```



### <code>POST</code> /v2/comments/{id}/report

Report a comment
     
- Parameter
  
     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `id` | Comment ID | ||
     | `title` | Reported items | ||
     | `text` | content | ||

- Response (application/json)
    * success response
        
```
true, false
```

 
### <code>GET</code> /v2/postings/{id}/similarContent

Similar posts

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | Post ID | ||
    | `offset` | | | `0` |
    | `count` | | | `5`|
    | `orderby` | sort (`id` or `popular` or `mostComments`) | | `id` |
    | `sortby` | sort (`desc` or `asc`) | | `asc` if postingType is guide and `desc` |
    
- Response (application/json)

   * field
   
 | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `posters` | Guidebook poster | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
        ```
        [
            {
                "id": 28,
                "text": "boast",
                "readCount": 0,
                "commentCount": 19,
                "likeCount": 2,
                "attachments": [
                    {
                        "id": 32,
                        "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                        "description" : "", #optional
                        "dimension" : "1080,920"
                    }
                ],
                "posters": [
                {
                "id": 32,
                "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                "description" : "", #optional
                        "dimension" : "1080,920"
                }
                ],
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "/assets/img/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "postingType": "boast",
                "createdAt": "2020-04-23T15:09:53+09:00",
                "modifiedAt": "2020-04-24T15:50:11+09:00"
            }
        ]
        ```


## Profile lookup

### <code>GET</code> /v2/users/me
View my profile


- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | User ID | | |
       | `nickname` | content | | |
       | `photoUrl` | avatar image | | |
       | `levelInfo` | level information | | |
       | `activity.postingCount` | Number of Posts | | |
       | `activity.commentCount` | Number of reply posts | | |
       | `activity.adoptedCount` | number of adoptions | | |
       | `security.email` | Email | | |
       | `security.phoneNumber` | phone number | | |
       | `adoptedCount` | Number of Adopted Articles | | |
       | `badgeCount` | Badge | | |
       | `representBadge` | Representative Badge | | |
       | `representBadge.id` | id of representative badge | | |
       | `representBadge.title` | Title of Representative Badge | |
       | `representBadge.description1` | Description of the representative badge 1 | |
       | `representBadge.description2` | Description of the representative badge 2 | |
       | `representBadge.imageUrl` | Representative badge image url | |

   * success response
   
```
    {
        "id": 1,
        "nickname": "user4",
        "levelInfo": {
            "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
            "level": 1,
            "name": "general"
        },
        "activity": {
          "postingCount": 12,
          "commentCount": 3,
          "adoptedCount" : 1
        },
        "photoUrl": null,
        "security": {
            "email": null,
            "phoneNumber": null
        },
        "provider": "email",
        "adopedCount" : 1,
        "badgeCount" : 6,
        "representBadge" : {
            "id": 2,
            "title" : "No comments",
            "description1" : "Your name left ten thousand comments on Moya!",
            "description2" : "You are starting to make a name for yourself as a plant expert!",
            "imageUrl" : image url
        }
    }
```

### <code>POST</code> ~~/v2/users/me~~
~~Update my profile~~ API to be added for each update item

- Parameter (multipart/form)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `nickname` | Nickname | ||
    | `avatar` | image file | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | User ID | | |
       | `nickname` | content | | |
       | `photoUrl` | avatar image | | |
       | `levelInfo` | Level Information | | |

   * success response
   
```
    {
            "id": 1,
            "nickname": "user4",
            "levelInfo": {
                "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                "level": 1,
 "name": "general"
            },
            "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/users/2020/05/11/3378eb45-69df-4f61-aea3-c64299bd78c5.png"
        }
```
  
     
### <code>GET</code> /v2/users/me/likes

Look up likes list

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `postingType` | type | | |
    | `sinceId` | | | |
    | `maxId` | | | |
    | `count` | | | |
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `timelineId` | Use this field when viewing the timeline (sinceId, maxId) | | |
       | `id` | ID for resource lookup | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
```
        [
            {
                "id": 28,
                "text": "boast",
                "readCount": 0,
                "commentCount": 19,
                "likeCount": 2,
                "attachments": [
                    {
                        "id": 32,
                        "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                        "description" : "", #optional
                        "dimension" : "1080,920"
                    }
                ],
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "/assets/img/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "postingType": "boast",
                "createdAt": "2020-04-23T15:09:53+09:00",
                "modifiedAt": "2020-04-24T15:50:11+09:00"
            }
        ]
```


### <code>GET</code> /v2/users/me/scraps

Scrap List Lookup

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `postingType` | type | | |
    | `sinceId` | | | |
    | `maxId` | | | |
    | `count` | | | |
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `timelineId` | Use this field when viewing the timeline (sinceId, maxId) | | |
       | `id` | ID for resource lookup | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
        ```
        [
            {
                "id": 28,
                "text": "boast",
                "readCount": 0,
                "commentCount": 19,
                "likeCount": 2,
                "attachments": [
                    {
                        "id": 32,
                        "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                        "description" : "" #optional,
                        "dimension" : "1080,920"
                    }
                ],
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "/assets/img/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "postingType": "boast",
                "createdAt": "2020-04-23T15:09:53+09:00",
                "modifiedAt": "2020-04-24T15:50:11+09:00"
            }
        ]
        ```



### <code>GET</code> /v2/users/me/postings

View the list of written posts

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `sinceId` | | | |
    | `maxId` | | | |
    | `count` | | | |
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID for resource lookup | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
        ```
        [
            {
                "id": 28,
                "text": "boast",
                "readCount": 0,
                "commentCount": 19,
                "likeCount": 2,
                "attachments": [
                    {
                        "id": 32,
                        "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                        "description" : "", #optional
                        "dimension" : "1080,920"     
                    }
                ],
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "/assets/img/badges/badge0.png",
                        "level": 1,
 "name": "general"
                    }
                },
                "postingType": "boast",
                "createdAt": "2020-04-23T15:09:53+09:00",
                "modifiedAt": "2020-04-24T15:50:11+09:00"
            }
        ]
        ```



### <code>GET</code> /v2/users/me/comments

View list of written replies

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `sinceId` | | | |
    | `maxId` | | | |
    | `count` | | | |
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID for resource lookup | | |
       | `text` | content | | |
       | `readCount` | Reads | | |
       | `commentCount` | Answers | | |
       | `likeCount` | Likes | | |
       | `attachments` | Attachment List | | |
       | `owner` | Author | | |
       | `postingType` | Post Type | | |
       | `createdAt` | Creation Date | | |
       | `modifiedAt` | Date of modification | | |

   * success response
   
        ```
        [
            {
                "id": 28,
                "text": "boast",
                "readCount": 0,
                "commentCount": 19,
                "likeCount": 2,
                "attachments": [
                    {
                        "id": 32,
                        "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                        "description" : "", #optional
                        "dimension" : "1080,920"
                    }
                ],
                "owner": {
                    "id": 1,
                    "nickname": "user1",
                    "levelInfo": {
                        "badge": "/assets/img/badges/badge0.png",
                        "level": 1,
                        "name": "general"
                    }
                },
                "postingType": "boast",
                "createdAt": "2020-04-23T15:09:53+09:00",
                "modifiedAt": "2020-04-24T15:50:11+09:00"
            }
        ]
        ```

### <code>GET</code> /v2/users/me/adopts

List of Adopted Articles

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `sinceId` | | | |
  | `maxId` | | | |
  | `count` | | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `timelineId` | Use this field when viewing the timeline (sinceId, maxId) | | |
      | `id` | ID for resource lookup | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `commentCount` | Answers | | |
      | `likeCount` | Likes | | |
      | `attachments` | Attachment List | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |

    * success response
         ```
         [
             {
                 "id": 28,
                 "text": "boast",
                 "readCount": 0,
                 "commentCount": 19,
                 "likeCount": 2,
                 "attachments": [
                     {
                         "id": 32,
                         "photoUrl": "posting/2020/04/23/b36606a5-9cba-4820-940f-c662b3cecdb8.png"
                         "description" : "" #optional,
                         "dimension" : "1080,920"     
                     }
                 ],
                 "owner": {
                     "id": 1,
                     "nickname": "user1",
                     "levelInfo": {
                         "badge": "/assets/img/badges/badge0.png",
                         "level": 1,
  "name": "general"
                     }
                 },
                 "postingType": "boast",
                 "createdAt": "2020-04-23T15:09:53+09:00",
                 "modifiedAt": "2020-04-24T15:50:11+09:00"
             }
         ]
         ```



### <code>GET</code> /v2/search/users

Search users (limit 20 without paging processing)

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `q` | search terms | ||
    
- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `id` | ID | | |
       | `nickname` | Nickname | | |
       | `photoUrl` | Thumbnails | | |
       | `level` | level | | |
       | `role` | | | |
       | `status` | | | |
       | `levelInfo` | Level Information | | |
       

   * success response
   
        ```
         {
             "resultCode": 1000,
             "resultData": [
                 {
                     "id": 1,
                     "nickname": "Moyamo",
                     "photoUrl": null,
                     "level": 6,
                     "role": "expert",
                     "status": "normal",
                     "levelInfo": {
                         "badge": "http://d1ujq2dpv14gay.cloudfront.net/commons/badges/badge5.png",
                         "level": 6,
                         "name": "God"
                     }
                 },
                 {
                     "id": 54242,
                     "nickname": "Moyamoyamo",
                     "photoUrl": null,
                     "level": 1,
                     "role": null,
                     "status": "normal",
                     "levelInfo": {
                         "badge": "http://d1ujq2dpv14gay.cloudfront.net/commons/badges/badge0.png",
                         "level": 1,
                         "name": "general"
                     }
                 }
         ]
        ```




## notice

### <code>GET</code> /v2/notifications

Notification list inquiry

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `sinceId` | | | |
     | `maxId` | | | |
     | `count` | | | |
     
- Response (application/json)

    * EventType
    
        ```
            NEW_QUESTION
            NEW_BOAST,
            NEW_CLINIC,
            NEW_GUIDE,
            NEW_MAGAZINE,
            NEW_COMMENT,
            NEW_LIKE_POSTING,
            NEW_LIKE_COMMENT,
            NEW_AD
        ```


  
* success response

    * field
   
    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `id` | timeline ID | | |
    | `title` | Title | | |
    | `description` | Description | | |
    | `eventType` | type | | |
    | `photoUrl` | image | | |
    | `owner` | Relative User | | |
    | `resource.resourceId` | resource ID | | |
    | `resource.resourceType` | resource type | | |
    | `resource.referenceId` | Detail ID to move when notification is clicked | | |
    | `resource.referenceType` | Page type to be moved when notification is clicked| | |
    | `unread` | unread | | |
```
         [
                {
 "id": 112,
                    "title": "Moyamo1 left a new comment.",
                    "description": "111",
                    "eventType": "NEW_COMMENT",
                    "photoUrl": "posting/2020/05/11/2da7555a-4e16-457a-a451-3eb0cc7b9f44.png?d=50x50",
                    "resource": {
                        "resourceId": "135",
                        "resourceType": "comment",
                        "referenceId": "93",
                        "referenceType": "question"
                    },
                    "owner": {
                         "id": 2,
                         "nickname": "Moyamo1",
                         "role": "expert",
                         "levelInfo": {
                             "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                             "level": 1,
                             "name": "general"
                         },
                         "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/users/2020/05/11/aeba4282-7f6b-486b-80ee-6f3cc7ffa135.png"
                    },
                    "unread": true
                },
                {
                    "id": 109,
                    "title": "Mart 1 (Ansan) left a new comment.",
                    "description": "111",
                    "eventType": "NEW_COMMENT",
                    "photoUrl": "posting/2020/04/22/0d80d769-b218-4c8c-80d2-df7bd555e2c1.png?d=50x50",
                    "resource": {
                        "resourceId": "133",
                        "resourceType": "comment",
                        "referenceId": "2",
                        "referenceType": "question"
                    },
                     "owner": {
                         "id": 2,
                         "nickname": "Moyamo1",
                         "role": "expert",
                         "levelInfo": {
                             "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                             "level": 1,
                             "name": "general"
                         },
                         "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/users/2020/05/11/aeba4282-7f6b-486b-80ee-6f3cc7ffa135.png"
                     },
                     "unread": true
                },
                {
                    "id": 108,
                    "title": "Moyamo1 left a new comment.",
                    "description": "111",
                    "eventType": "NEW_COMMENT",
                    "photoUrl": "posting/2020/04/22/0d80d769-b218-4c8c-80d2-df7bd555e2c1.png?d=50x50",
                    "resource": {
                        "resourceId": "132",
                        "resourceType": "comment",
                        "referenceId": "2",
                        "referenceType": "question"
                    },
                    "owner": {
                         "id": 2,
                         "nickname": "Moyamo1",
                         "role": "expert",
                         "levelInfo": {
                             "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge0.png",
                             "level": 1,
                             "name": "general"
                         },
                         "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/users/2020/05/11/aeba4282-7f6b-486b-80ee-6f3cc7ffa135.png"
                    },
                    "unread": true
                }
        ]
```


### <code>POST</code> /v2/notifications/markRead

Read all notifications

- Parameter

- Response (application/json)



### <code>POST</code> /v2/notifications/{id}/markRead

Notification read processing

- Response (application/json)



### <code>GET</code> /v2/notifications/setting

View my notification settings

- Response (application/json)

   * field
   
  | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `notiEnable` | Whether to notify all boolean | | |
       | `adNotiEnable` | Whether to be notified of advertisements boolean | | |
       | `postingNotiEnable` | Whether to be notified of featured posts boolean | | |
       | `commentNotiEnable` | Whether to be notified of replies boolean | | |
       | `replyNotiEnable` | Whether to be notified of comments boolean | | |
       | `likeNotiEnable` | like notification or not boolean | | |
       | `shopNotiEnable` | Whether to notify payment and shipping information boolean | | |
       | `mentionNotiEnable` | Whether to notify comment mentions boolean | | |
       | `adNotiAgreement` | Advertisement, marketing consent boolean(true, false, null), if null, pop-up notification | | |
       | `adNotiConfirmedAt` | Advertisement, Marketing Notification Set Date | | |
       | `joinCommentNotiEnable` | Get All Comments on Participating Posts | | |
       | `badgeNotiEnable` | Badge notifications boolean | | |
       | `adoptNotiEnable` | Adoption notification or not boolean | | |
       
    

### <code>POST</code> /v2/notifications/setting

Edit my notification settings

- Parameter(application/json)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `notiEnable` | Whether to notify all boolean | | |
    | `adNotiEnable` | Whether to be notified of advertisements boolean | | |
    | `postingNotiEnable` | Whether to be notified of featured posts boolean | | |
    | `commentNotiEnable` | Whether to be notified of replies boolean | | |
    | `replyNotiEnable` | Whether to be notified of comments boolean | | |
    | `likeNotiEnable` | like notification or not boolean | | |
    | `shopNotiEnable` | Whether to notify payment and shipping information boolean | | |
    | `mentionNotiEnable` | Whether to notify comment mentions boolean | | |
    | `adNotiAgreement` | Advertisement, marketing consent or not boolean | | |
    | `joinCommentNotiEnable` | Get All Comments on Participating Posts | | |
   

- Response (application/json)

   * field
   
       | attribute| description | required | default |
       | --- | --- | -------- | --- |
       | `notiEnable` | Whether to notify all boolean | | |
       | `adNotiEnable` | Whether to be notified of advertisements boolean | | |
       | `postingNotiEnable` | Whether to be notified of featured posts boolean | | |
       | `commentNotiEnable` | Whether to be notified of replies boolean | | |
       | `replyNotiEnable` | Whether to be notified of comments boolean | | |
       | `likeNotiEnable` | like notification or not boolean | | |
       | `shopNotiEnable` | Whether to notify payment and shipping information boolean | | |
       | `mentionNotiEnable` | Whether to notify comment mentions boolean | | |
       | `adNotiAgreement` | Advertisement, marketing consent boolean(true, false, null), if null, pop-up notification | | |
       | `adNotiConfirmedAt` | Advertisement, Marketing Notification Set Date | | |


## badge

### <code>GET</code> /v2/users/me/badges

Acquired Badge Lookup

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `allBadges` | All Badges | |
      | `allBadges.id` | Badge ID | |
      | `allBadges.title` | Title of Badge | |
      | `allBadges.description1` | Description of the representative badge 1 | |
      | `allBadges.description2` | Description of the representative badge 2 | |
      | `allBadges.representable` | Possibility to set as representative badge boolean | |
      | `allBadges.currentRepresent` | Whether to check the current representative badge boolean | |
      | `allBadges.imageUrl` | Badge image url | |

* success response
```
    [
        {
            "allBadges" : [
                {
                    "id": 2,
                    "title" : "No comments",
                    "description1" : "Your name left ten thousand comments on Moya!",
                    "description2" : "You are starting to make a name for yourself as a plant expert!",
                    "representable" : true,
                    "currentRepresent": true,
                    "imageUrl" : image url
                },
                {
                    "id": 3,
                    "title" : "One hundred thousand comments",
                    "description1" : "Your name has left a hundred thousand comments on Moya!",
                    "description2" : "You are one of the best plant experts!",
                    "representable" : false,
                    "currentRepresent": false,
                    "imageUrl" : image url
                }
            ]
        }
    ]
```

### <code>POST</code>  /v2/users/me/badges/{id}/representBadge

Edit My Representative Badge

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Badge ID | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `allBadges` | All Badges | |
      | `allBadges.id` | Badge ID | |
      | `allBadges.title` | Title of Badge | |
      | `allBadges.description1` | Description of the representative badge 1 | |
      | `allBadges.description2` | Description of the representative badge 2 | |
      | `allBadges.representable` | Possibility to set as representative badge boolean | |
      | `allBadges.currentRepresent` | Whether to check the current representative badge boolean | |
      | `allBadges.imageUrl` | Badge image url | |
    
* success response
```
    [
        {
            "allBadges" : [
                {
                    "id": 2,
                    "title" : "No comments",
                    "description1" : "Your name left ten thousand comments on Moya!",
                    "description2" : "You are starting to make a name for yourself as a plant expert!",
                    "representable" : true,
                    "currentRepresent": true,
                    "imageUrl" : image url
                },
                {
                    "id": 3,
                    "title" : "One hundred thousand comments",
                    "description1" : "Your name has left a hundred thousand comments on Moya!",
                    "description2" : "You are one of the best plant experts!",
                    "representable" : false,
                    "currentRepresent": false,
                    "imageUrl" : image url
                }
            ]
        }
    ]
```

### <code>GET</code> /v2/users/{id}/badges

View other users' badges

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | User ID | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `allBadges` | All Badges | |
      | `allBadges.id` | Badge ID | |
      | `allBadges.title` | Title of Badge | |
      | `allBadges.description1` | Description of the representative badge 1 | |
      | `allBadges.description2` | Description of the representative badge 2 | |
      | `allBadges.representable` | Possibility to set as representative badge boolean | |
      | `allBadges.currentRepresent` | Whether to check the current representative badge boolean | |
      | `allBadges.imageUrl` | Badge image url | |

* success response
```
    [
        {
            "allBadges" : [
                {
                    "id": 2,
                    "title" : "No comments",
                    "description1" : "Your name left ten thousand comments on Moya!",
                    "description2" : "You are starting to make a name for yourself as a plant expert!",
                    "representable" : true,
                    "currentRepresent": true,
                    "imageUrl" : image url
                },
                {
                    "id": 3,
                    "title" : "One hundred thousand comments",
                    "description1" : "Your name has left a hundred thousand comments on Moya!",
                    "description2" : "You are one of the best plant experts!",
                    "representable" : false,
                    "currentRepresent": false,
                    "imageUrl" : image url
                }
            ]
        }
    ]
```


## search word

### <code>GET</code> /v2/tags/recommended

Suggested search terms

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | ~~`postingType`~~ | | | |
     | `type` | question,boast,magazine,clinic,free,guidebook,shop,dictionary | | |
     
- Response (application/json)
  
    ```
    
        [
            "Search term 1",
            "Search term 2"
        ]
        
    ```


### <code>GET</code> /v2/tags/autocomplete

auto-complete

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `postingType` | | | |
     | `q` | search terms | | |
     
- Response (application/json)
  
    ```
    
        [
            "Search term 1",
            "Search term 2"
        ]
        
    ```
    
## Encyclopedia

### <code>GET</code> /v2/dictionaries/{name}

Encyclopedia

- Parameter
  
- Response (application/json)
  
    ```
        {Encyclopedia URL}
    ```


### <code>GET</code> /v2/dictionaries/{name}/share

Encyclopedia

- Parameter
  
- Response (application/json)
 * success response
        ```
        {
                "id": "e997176e-c924-40c3-b992-e54e2e5fccca",
                "resource": {
                    "resourceId": "rose",
                    "resourceType": "dictionary",
                    "referenceId": "rose",
                    "referenceType": "dictionary"
                },
                "deeplink": "https://devflabook.page.link/AfYUEt4cgHGuLUE26"
            }
        ```


## shopping mall

### <code>GET</code> /v2/shops/goods

Search product list

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `offset` | | | 0 |
     | `count` | | | 10 |
     | `q` | search terms | | |
     | `orderby` | sort `id` or `popular` | | `id` |
  
- Response (application/json)
  
    * field
    
    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `goodsNo` | Product ID | | |
    | `name` | Product Name | | |
    | `photoUrl` | product images | | |
    | `price` | list price | | |
    | `discountedPrice` | discount price | | |
    | `reviewCnt` | number of reviews | | |
  
    ```
        [
               {
                   "goodsNo": "1000000069",
                   "name": "[Vegetable Seeds] Amaranth 100",
                   "price": 1000.00,
                   "photoUrl": "http://flabook.godomall.com/dataDto/goods//17/01/20/1000000069/1000000069Main.jpg",
                   "discountedPrice": 1000.00,
                   "reviewCnt" : 10
               },
               {
                   "goodsNo": "1000004454",
                   "name": "[Vegetable seeds] French radish 100 grains",
                   "price": 1500.00,
                   "photoUrl": "http://flabook.godomall.com/dataDto/goods/18/02/07/1000004454/1000004454_main_042.jpg",
                   "discountedPrice": 1500.00,
                    "reviewCnt" : 10
               }
        ]
    
    ```

### <code>GET</code> /v2/shops/goods/count

Search the number of search results

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `offset` | | | 0 |
     | `count` | | | 10 |
     | `q` | search terms | | |


- Response (application/json)
  
   * success response
   
        ```
        {number}
        ```

  

### <code>GET</code> /v2/shops/goods/recommended

Search for a list of recommended products

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `offset` | | | 0 |
     | `count` | | | 10 |
     | `q` | search terms | | |
     | `orderby` | sort `id` or `popular` | | `id` |
  
- Response (application/json)
  
    * field
    
    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `goodsNo` | Product ID | | |
    | `name` | Product Name | | |
    | `photoUrl` | product images | | |
    | `price` | list price | | |
    | `discountedPrice` | discount price | | |
  
    ```
        [
               {
                   "goodsNo": "1000000069",
                   "name": "[Vegetable Seeds] Amaranth 100",
                   "price": 1000.00,
                   "photoUrl": "http://flabook.godomall.com/dataDto/goods//17/01/20/1000000069/1000000069Main.jpg",
                   "discountedPrice": 1000.00
               },
               {
                   "goodsNo": "1000004454",
                   "name": "[Vegetable seeds] French radish 100 grains",
                   "price": 1500.00,
                   "photoUrl": "http://flabook.godomall.com/dataDto/goods/18/02/07/1000004454/1000004454_main_042.jpg",
                   "discountedPrice": 1500.00
               }
        ]
    
    ```

### <code>GET</code>  /v2/shops/goods/recommended/count

Search the number of search results

- Parameter

     | attribute| description | required | default |
     | --- | --- | -------- | --- |
     | `offset` | | | 0 |
     | `count` | | | 10 |
     | `q` | search terms | | |


- Response (application/json)
  
   * success response
   
        ```
        {number}
        ```
   

### <code>GET</code> /v2/shops/deposit

 Deposit inquiry

 - Parameter

 - Response (application/json)

    * field
    
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `deposit` | Balance (bigdecimal) | | |

     ```
        {
            "deposit": 1000
        }
     ```

### <code>GET</code> /v2/shops/mileage

 Mileage inquiry

 - Parameter

 - Response (application/json)

    * field
    
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `mileage` | Balance (bigdecimal) | | |

     ```
        {
            "mileage" : 1000
        }
     ```



### <code>GET</code> /v2/shops/wallet

 Mileage, deposit inquiry

 - Parameter

 - Response (application/json)

    * field
    
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `mileage` | Balance (bigdecimal) | | |
        | `deposit` | Balance (bigdecimal) | | |

     ```
        {
            "mileage": 1000,
            "deposit": 1000
        }
     ```



### <code>GET</code> /v2/shops/page/{name}

Shop page address lookup

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `name` | | | `home` |


- Name

    | attribute| description |
    | --- | --- |
    | `home` | Home |
    | `cart` | shopping cart |
    | `coupon` | coupon |
    | `wish` | Favorite |
    | `review` | Reviews I wrote |
    | `cancel` | Cancellation/Return/Exchange |
    | `order` | Order List |
    | `deposit` | Deposit |
    | `qa` | My Inquiry History |
    | `recommended` | Recommended Products |
    | `mileage` | Mileage |
    | `refund` | Refund/Deposit Processing |
    | `shipping` | Shipping address management |
    

  

- Response (application/json)

    ```
        {connection address}
    ```

### <code>GET</code>  /v2/shops/page/goods/{id} 

 Move product details

 - Parameter

 - Response (application/json)

     ```
         {connection address}
     ```



### <code>GET</code> /v2/shops/goods/test

FCM notification broadcast test api (payment completed, shipping, delivery completed)

 - Parameter

 - Response (application/json)

 

## Notice


### <code>GET</code> /v2/notices

Notice list lookup

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    

- Response (application/json)

    * field
    
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `title` | Title | | |
        | `description` | Description | | |
        | `interval` | Number of repeat exposure days (0: exposure only once, 1: once per day, 7: once per week) | | |
        | `popup` | pop-up or not | | |
        | `url` | Link Address | | |

    ```
         [
            {
              "id": 1,
              "title": "Notice",
              "description": "Notice",
              "interval": 0,
              "status": "open",
              "popup": true,
              "url": ""
            }
        ]
    
    ```



### <code>GET</code> /v2/notices/apps/release

App release version lookup

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `osType` | | | `android`, `ios` |
    | `version` | | | Client Current Version |


- Response (application/json)

    * field
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `version` | version | | |
        | `forceUpdate` | Required Updates | | |
        | `needUpdate` | Check the server for updates | | |

    ```
         # null if not
    
         {
            "version": "5.1.10",
            "forceUpdate": true,
            "needUpdate": true
         }
    
    ```


##banner lookup


### <code>GET</code> /v2/banners

View banner list

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    


- Response (application/json)

    * field
    
        | attribute| description | required | default |
        | --- | --- | -------- | --- |
        | `title` | | | |
        | `resource` | | | |
        | 'banner image' | | | |

    ```
         # null if not
    
         [
            {
                "title": "title",
                "resource": {
                  "resourceId": "139",
                  "resourceType": "boast",
                  "referenceId": "139",
                  "referenceType": "boast"
                },
                "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/banners/banner2.jpg`
            }
        ]
  
    
    ```


## Policy

### <code>GET</code> /v2/policies/page/:name

App release version lookup

- Parameter

    | attribute| description | required | default |
    | --- | --- | -------- | --- |
    | `name` | `location` : location <br> `terms` : terms of use<br> `privacy` : privacy policy <br> `notice` : notice | `notice` | |


- Response (application/json)

     ```
         {connection address}
     ```






## Log

### <code>GET</code> /v2/logs

Upload client log (json)

- Parameter(application/json)

    | attribute| description | required | default |
    | --- | --- | -------- | --- |


- Response (application/json)

     ```
         null
     ```

## resource

### <code>GET</code> /v2/shops/page/redirect

Go to the shop resource and look up the address
(If the attribute `resourceType` of `resource` of notification list, PUSH notification is `shop`, search the page address to move to `resourceId`)

[FCM notification broadcast test api](#<code>GET</code> /v2/shops/goods/test)

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `resourceId` | Notification list, resource.resourceId of PUSH notification | | |

- Response (application/json)

    ```
        {connection address}
    ```


### <code>GET</code> /v2/page/redirect

Search for moving address to common resources

** Note that it works with referenceId and referenceType

(Search the page address to move to the properties `referenceType`, `referenceId` of `resource` of notification popup, notification list, and PUSH notification)

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `resourceId` | resource.resourceId of notification list, PUSH notification (* It may be a web address, so url encoding is required) | | |
  | `resourceType` | referenceType | | |

- Response (application/json)

    ```
        {connection address}
    ```

## event

### <code>GET</code> /v2/events

Event popup inquiry
- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |


- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `title` | Title | | |
      | `description` | Description | | |
      | `interval` | Number of repeat exposure days (0: exposure only once, 1: once per day, 7: once per week) | | |
      | `popup` | pop-up or not | | |
      | `url` | Link Address | | |

    ```
        
  
         [
            {
              "id": 1,
              "title": "Notice",
              "description": "Notice",
              "interval": 0,
              "status": "open",
              "popup": true,
              "url" : "" # move if not found
            }
        ]
    
    ```


## ranking

### <code>GET</code> /v2/rankings

Ranking inquiry

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |


- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `date` | analysis date | | |
      | `keyword.polularity` | Popular Search Terms | | |
      | `keyword.polularity.rank` | ranking | | |
      | `keyword.polularity.keyword` | search terms | | |
      | `keyword.polularity.fluctuation` | + (up rank), - (down rank), 0 (same rank), null (new) | | |
      | `user.question` | Question User Ranking | | |
      | `user.question.rank` | ranking | | |
      | `user.question.id` | userid | | |
      | `user.question.keyword` | Nickname | | |
      | `user.question.thunbnail` | image | | |
      | `user.question.fluctuation` | + (up rank), - (down rank), 0 (same rank), null (new) | | |
      | `user.clinic` | Clinic User Rankings | | |
      | `user.clinic.rank` | ranking | | |
      | `user.clinic.id` | userid | | |
      | `user.clinic.keyword` | Nickname | | |
      | `user.clinic.thunbnail` | image | | |
      | `user.clinic.fluctuation` | + (up rank), - (down rank), 0 (same rank), null (new) | | |
      | `posting` | Popular posts (free talk, brag) User Rankings | | |
      | `posting.rank` | ranking | | |
      | `posting.id` | post id | | |
      | `posting.postingType` | Post Type | | |
      | `posting.readCount` | Reads | | |
      | `posting.commentCount` | Comments | | |
      | `posting.thunbnail` | image | | |
      | `posting.likeCount` | Likes | | |
      | `posting.fluctuation` | + (up rank), - (down rank), 0 (same rank), null (new) | | |
      | `goods` | Popular Product Ranking | | |
      | `goods.rank` | ranking | | |
      | `goods.id` | userid | | |
      | `goods.keyword` | Nickname | | |
      | `goods.fluctuation` | + (up rank), - (down rank), 0 (same rank), null (new) | | |
      | `goods.price` | Price (KRW) | | |
      | `popup` | pop-up or not | | |
      | `url` | Link Address | | |

    ```
    {
        "date": "03.18~03.24",
        "keyword": {
            "popularity": [
                {
                    "thumbnail": null,
                    "rank": 1,
                    "keyword": "#comment",
                    "fluctuation": 1
                },
                {
                    "thumbnail": null,
                    "rank": 2,
                    "keyword": "#spoon",
                    "fluctuation": 9
                },
                {
                    "thumbnail": null,
                    "rank": 3,
                    "keyword": "#don't know",
                    "fluctuation": 13
                },
                {
                    "thumbnail": null,
                    "rank": 4,
                    "keyword": "#check",
                    "fluctuation": 8
                },
                {
                    "thumbnail": null,
                    "rank": 5,
                    "keyword": "#thank you",
                    "fluctuation": 12
                },
                {
                    "thumbnail": null,
                    "rank": 6,
                    "keyword": "#abc",
                    "fluctuation": 3
                }
            ]
        },
  "user": {
            "question": [
                {
                    "id": "1235910",
                    "thumbnail": null,
                    "rank": 1,
                    "keyword": "paaak2",
                    "fluctuation": 6
                },
                {
                    "id": "1235614",
                    "thumbnail": null,
                    "rank": 2,
                    "keyword": "blackbird",
                    "fluctuation": -1
                },
                {
                    "id": "1235591",
                    "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/users/2020/09/18/b7f62f04-d39d-43d3-8814-5af0fa379f1c.jpg?d=100x100",
                    "rank": 3,
                    "keyword": "choking kakaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo",
                    "fluctuation": 0
                },
                {
                    "id": "284751",
                    "thumbnail": null,
                    "rank": 4,
                    "keyword": "paaah",
                    "fluctuation": 0
                }
            ],
            "clinic": [
                {
                    "id": "1235906",
                    "thumbnail": null,
                    "rank": 1,
                    "keyword": "hjnaver",
                    "fluctuation": 4
                },
                {
                    "id": "1235915",
                    "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/users/2021/03/23/ff595dc1-7f3a-423d-94f0-9748ef958911.jpg?d=100x100",
                    "rank": 2,
                    "keyword": "Hyeongjin face",
                    "fluctuation": 5
                },
                {
                    "id": "1235869",
                    "thumbnail": null,
                    "rank": 3,
                    "keyword": "ddcv",
                    "fluctuation": null
                },
                {
                    "id": "1235614",
                    "thumbnail": null,
                    "rank": 4,
                    "keyword": "blackbird",
                    "fluctuation": 0
                },
                {
                    "id": "1235613",
                    "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/users/2021/03/15/b4ca7c4f-2a91-4735-ae6f-c5536876f6ac.jpg?d=100x100",
                    "rank": 5,
                    "keyword": "Evanryu",
                    "fluctuation": -4
                },
                {
                    "id": "284751",
                    "thumbnail": null,
                    "rank": 6,
                    "keyword": "paaah",
                    "fluctuation": 2
                }
            ]
        },
        "posting": [
            {
                "id": "2002082",
                "text": "12346😚🎊🐥😛🐗",
                "postingType": "free",
                "readCount": 38,
                "commentCount": 17,
                "likeCount": 0,
                "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/03/15/d07668ea-a109-4c7f-9921-959cce28bc98.jpg?d=100x100",
                "rank": 1,
                "fluctuation": null
            },
            {
                "id": "2002082",
                "text": "12346😚🎊🐥😛🐗",
                "postingType": "free",
                "readCount": 38,
                "commentCount": 17,
                "likeCount": 0,
                "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/03/15/d07668ea-a109-4c7f-9921-959cce28bc98.jpg?d=100x100",
                "rank": 2,
                "fluctuation": null
            }
        ],
        "goods": [
            {
                "id": "1000010023",
                "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/goods/21/03/11/1000010023/1000000235_main_030.png?d=100x100",
                "rank": 1,
                "keyword": "[Flower seedling] Dai Asia_Pink Queen 10cm pot",
                "fluctuation": null,
                "price": 3500.00
            },
            {
                "id": "1000010021",
                "thumbnail": "http://d1ujq2dpv14gay.cloudfront.net/dev/goods/21/03/11/1000010021/1000010021_main_030.jpg?d=100x100",
                "rank": 2,
                "keyword": "[Flower Seed] White Lily_Elegance Salmon Rose 20",
                "fluctuation": null,
                "price": 1500.00
            }
        ]
    }
    ```

### <code>GET</code>  /v2/rankings/order

Ranking sort inquiry

Change the ranking order according to the array order

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |


- Response (application/json)
  
    ```
    [
        "user",
        "keyword",
        "posting",
        "goods"
    ]
    ```



## FCM
```
In the same way as in the notification list, the contents are identified by referenceId and referenceType and moved to the details.

referenceType
    - Posts
        * question
        * boast
        * clinic
        * free
        * magazine
        * guidebook
        
    - Moyamo Shop
        * shop
            Call the api with the address of https://.../v2/shops/orderDto referenceId and connect to the address of the result

```


### comment

```
{
  "to": "c4HcqwioXet5YRmQO8Db3m:APA91bE0w1QqmnttkSJqt3HAiZF6lMJ96kIMm6E59W46BCVlzeFtrXkNhZP2JOWb-vYR9gehswBv2AQR9gehswBv2aQR9gehswBv2aQR9gehswBv2AQR9gehswBv2aQR9gehswBv2AQR9gehswBv2AQFuDbluhDgGcWaHb3HV49XLV6GycWaHb-DBluhDgGcWaHb1QqmnttkSJqt3HAiZF6lMJ"to": "to":
  "notification": {
    "title": "Moyamo left a new comment.",
    "body": "111124",
    "badge": 1,
    "click_action": ".MainActivity"
  },
  "dataDto": {
    "value": {
      "id": 252,
      "title": "Moyamo left a new comment.",
      "description": "111124",
      "eventType": "NEW_COMMENT",
      "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/attachments/2014/10/31/2a2a4170-5aa5-495b-b72e-cd37c3bdcaa6.jpg?d=50x50",
      "owner": {
        "id": 1,
        "nickname": "Moyamo",
        "role": "expert",
        "levelInfo": {
          "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge5.png",
          "level": 6,
          "name": "God"
        },
        "photoUrl": null
      },
      "unread": true,
      "resource": {
        "resourceId": "7886505",
        "resourceType": "comment",
        "referenceId": "117",
        "referenceType": "question"
      }
    },
    "imageUrlPath": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/attachments/2014/10/31/2a2a4170-5aa5-495b-b72e-cd37c3bdcaa6.jpg?d=50x50",
    "targetBadge": 1
  }
}
```

###Shipping Information
```
{
  "to": "1",
  "notification": {
    "title": "Moyamo Shop",
    "body": null,
    "badge": 3,
    "click_action": ".MainActivity"
  },
  "dataDto": {
    "value": {
      "id": 251,
      "title": "Moyamo Shop",
      "description": null,
      "eventType": "NEW_SHOP",
      "photoUrl": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/http://m.flabook.godomall.com/dataDto/goods/20/05/18/1000007880/1000007880_main_092 .jpg?d=50x50",
      "owner": {
        "id": 1,
        "nickname": "Moyamo",
        "role": "expert",
        "levelInfo": {
          "badge": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/badges/badge5.png",
          "level": 6,
          "name": "God"
        },
        "photoUrl": null
      },
      "unread": true,
      "resource": {
        "resourceId": "/v2/shops/orders",
        "resourceType": "shop",
        "referenceId": "/v2/shops/orders",
        "referenceType": "shop"
      }
    },
    "imageUrlPath": "https://flabook-test.s3.ap-northeast-2.amazonaws.com/http://m.flabook.godomall.com/dataDto/goods/20/05/18/1000007880/1000007880_main_092 .jpg?d=50x50",
    "targetBadge": 3
  }
}

```

[Edit my notification settings]:






## Photo


### <code>GET</code> /v2/photoAlbums?userId={userId}

Photo album inquiry

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `userId` | User ID | false | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | photo album id | | |
      | `name` | photo album name | | |
      | `representPhoto` | Representative photos of the corresponding photo album | | |
      | `representPhoto.id` | id of representative photo | | |
      | `representPhoto.attachments.photoUrl` | Image link address of representative photo | | |
      | `photoCnt` | Total number of photos in photo album | | |

    ```
        
  
         [
            {
                "id": 31,
                "name": "all",
                "isDelete": false,
                "representPhoto": {
                    "id": 2002805,
                    "title": null,
                    "text": "Is it fast? 1",
                    "readCount": 0,
                    "commentCount": 0,
                    "likeCount": 0,
                    "scrapCount": 0,
                    "reportCount": 0,
                    "goodses": [],
                    "resource": null,
                    "attachments": [
                        {
                            "id": 2648459,
                            "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/60cfcab7-0887-4d36-892f-fabd39faca57.png"
                        }
                    ],
                    "posters": null,
                    "owner": {
                        "id": 39,
                        "nickname": "test",
                        "photoUrl": null,
                        "level": 2,
                        "role": "user",
                        "status": "normal",
                        "levelInfo": {
                            "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                            "level": 2,
                            "name": "seconds"
                        },
                        "activity": {
                            "postingCount": 223,
                            "commentCount": 11,
                            "adoptedCount": 1
                        },
                        "provider": "phone",
                        "createdAt": "2014-11-01T02:57:10+09:00",
                        "badgeCount": 0,
                        "representBadge": null,
                        "totalPhotosCnt": 1,
                        "totalPhotosLikeCnt": 0,
                        "newbie": false
                    },
                    "isDelete": false,
                    "isBlind": false,
                    "postingType": "photo",
                    "createdAt": "2021-05-07T15:02:05+09:00",
                    "modifiedAt": "2021-05-07T15:02:05+09:00",
                    "youtubeId": null,
                    "orgAttachments": [
                        {
                            "id": 2648459,
                            "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/60cfcab7-0887-4d36-892f-fabd39faca57.png"
                        }
                    ],
                    "adopt": false,
                    "orgText": "Is it fast? 1",
                    "orgPosters": null
                },
                "photoCnt": 1
            },
            {
                "id": 32,
                "name": "New album",
                "isDelete": false,
                "representPhoto": null,
                "photoCnt": 0
            }
        ]
    
    ```

### <code>GET</code>  /v2/photoAlbums/{photoAlbumId}

View photo album details

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `photoAlbumId` | Photo Album ID | | |
  | `sinceId` | | |
  | `maxId` | | | |
  | `orderby` | Sort order ( id : newest, popular : popular ) | | id |
  | `offset` | | | 0 |
  | `count` | | | 10 |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
      | `timelineId` | Use this field when viewing the timeline (sinceId, maxId) | | |
      | `isLike` | Like or not | | |

    ```
        
  
         [
            {
                "id": 2002845,
                "title": null,
                "text": "Golden 4",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648500,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/12/75e1fc63-9226-4170-8407-00ca4065aabf.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 1,
                    "nickname": "Moyamo",
                    "photoUrl": null,
                    "level": 6,
                    "role": "admin",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge5.png",
                        "level": 6,
                        "name": "God"
                    },
                    "activity": {
                        "postingCount": 13,
                        "commentCount": 2,
                        "adoptedCount": 0
                    },
                    "provider": "phone",
                    "createdAt": "2014-10-20T22:25:36+09:00",
                    "badgeCount": 2,
                    "representBadge": null,
                    "totalPhotosCnt": 5,
                    "totalPhotosLikeCnt": 1,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-12T15:44:21+09:00",
                "modifiedAt": "2021-05-12T15:44:21+09:00",
                "timelineId": 39,
                "youtubeId": null,
                "orgPosters": null,
                "orgText": "Golden Hand4",
                "orgAttachments": [
                    {
                        "id": 2648500,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/12/75e1fc63-9226-4170-8407-00ca4065aabf.png"
                    }
                ],
                "adopt": false,
                "isLike": false
            },
            {
                "id": 2002843,
                "title": null,
                "text": "Golden hand 3",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648498,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/12/7e98b9f0-8c88-490d-a685-90a79af2031a.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 1,
 "nickname": "Moyamo",
                    "photoUrl": null,
                    "level": 6,
                    "role": "admin",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge5.png",
                        "level": 6,
                        "name": "God"
                    },
                    "activity": {
                        "postingCount": 13,
                        "commentCount": 2,
                        "adoptedCount": 0
                    },
                    "provider": "phone",
                    "createdAt": "2014-10-20T22:25:36+09:00",
                    "badgeCount": 2,
                    "representBadge": null,
                    "totalPhotosCnt": 5,
                    "totalPhotosLikeCnt": 1,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-12T14:43:41+09:00",
                "modifiedAt": "2021-05-12T14:43:41+09:00",
                "timelineId": 37,
                "youtubeId": null,
                "orgPosters": null,
                "orgText": "Golden Hand 3",
                "orgAttachments": [
                    {
                        "id": 2648498,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/12/7e98b9f0-8c88-490d-a685-90a79af2031a.png"
                    }
                ],
                "adopt": false,
                "isLike": false
            }
        ]
    
    ```


### <code>GET</code> /v2/photoAlbums/representPhotos?userId={userId}

My Page representative photos, popular photos

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `userId` | User ID | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
      | `isLike` | Like or not | | |

    ```
        
  
         [
            {
                "id": 2002807,
                "title": null,
                "text": "Is it fast? 3",
                "readCount": 2,
                "commentCount": 0,
                "likeCount": 2,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648461,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "activity": {
                        "postingCount": 226,
                        "commentCount": 11,
                        "adoptedCount": 1
                    },
                    "provider": "phone",
                    "createdAt": "2014-11-01T02:57:10+09:00",
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 4,
                    "totalPhotosLikeCnt": 3,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-07T15:42:03+09:00",
                "modifiedAt": "2021-05-07T15:42:03+09:00",
                "youtubeId": null,
                "orgAttachments": [
                    {
                        "id": 2648461,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                    }
                ],
     "adopt": false,
                "orgText": "Is it fast? 3",
                "orgPosters": null,
                "isLike": true
            },
            {
                "id": 2002805,
                "title": null,
                "text": "Is it fast? 1",
                "readCount": 1,
                "commentCount": 0,
                "likeCount": 1,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648459,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/60cfcab7-0887-4d36-892f-fabd39faca57.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "activity": {
                        "postingCount": 226,
                        "commentCount": 11,
                        "adoptedCount": 1
                    },
                    "provider": "phone",
                    "createdAt": "2014-11-01T02:57:10+09:00",
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 4,
                    "totalPhotosLikeCnt": 3,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-07T15:02:05+09:00",
                "modifiedAt": "2021-05-07T15:02:05+09:00",
                "youtubeId": null,
                "orgAttachments": [
                    {
                        "id": 2648459,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/60cfcab7-0887-4d36-892f-fabd39faca57.png"
                    }
                ],
                "adopt": false,
                "orgText": "Is it fast? 1",
                "orgPosters": null,
                "isLike": true
            },
            {
                "id": 2002810,
                "title": null,
                "text": "Is it fast? 2",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648465,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/61933772-54e3-4944-9781-84a32a950a49.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "activity": {
                        "postingCount": 226,
                        "commentCount": 11,
                        "adoptedCount": 1
                    },
                    "provider": "phone",
                    "createdAt": "2014-11-01T02:57:10+09:00",
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 4,
                    "totalPhotosLikeCnt": 3,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-10T08:44:49+09:00",
                "modifiedAt": "2021-05-10T08:44:49+09:00",
                "youtubeId": null,
                "orgAttachments": [
                    {
                        "id": 2648465,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/61933772-54e3-4944-9781-84a32a950a49.png"
                    }
                ],
                "adopt": false,
               "orgText": "Isn't it fast? 2",
                "orgPosters": null,
                "isLike": false
            },
            {
                "id": 2002811,
                "title": null,
                "text": "Is it fast? 4",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648466,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/48ac10d4-b521-402f-9eb5-5efe554f7551.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "activity": {
                        "postingCount": 226,
                        "commentCount": 11,
                        "adoptedCount": 1
                    },
                    "provider": "phone",
                    "createdAt": "2014-11-01T02:57:10+09:00",
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 4,
                    "totalPhotosLikeCnt": 3,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-10T09:08:07+09:00",
                "modifiedAt": "2021-05-10T09:08:07+09:00",
                "youtubeId": null,
                "orgAttachments": [
                    {
                        "id": 2648466,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/48ac10d4-b521-402f-9eb5-5efe554f7551.png"
                    }
                ],
                "adopt": false,
                "orgText": "Is it fast? 4",
                "orgPosters": null,
                "isLike": false
            }
        ]
    
    ```



### <code>POST</code> /v2/photoAlbums

Create photo album

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `name` | The name of the photo album to be created | true | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Photo Album ID | | |
      | `name` | Name of photo album | | |
      | `representPhoto` | Representative photos of photo albums | | |
      | `photoCnt` | Number of photos in photo album | | |

    ```
        
  
         [
            {
                "id": 41,
                "name": "New album 3",
                "isDelete": false,
                "representPhoto": null,
                "photoCnt": 0
            }
        ]
    
    ```


### <code>PUT</code> /v2/photoAlbums/{id}

Edit photo album

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo album to be edited | | |
  | `name` | Name of photo album after editing | true | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
    ```
        
  
         [
            true
        ]
    
    ```


### <code>DELETE</code>  /v2/photoAlbums/{id}

Delete photo album

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo album to be deleted | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |


    ```
        
  
         [
            true
        ]
    
    ```



### <code>POST</code> /v2/photoAlbums/me/{id}

Photo registration in photo album: multiple at once

- Parameter (multipart/form)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Current photo album ID | | |
  | `postingType` | type |true | |
  | `texts` | Written content | ||
  | `files` | File (multiple) | true ||
  | `albumNames` | Album Name | | |
  
- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |

    ```
        
  
    [
        {
            "id": 2002819,
            "title": null,
            "text": "Smile 2",
            "readCount": 0,
            "commentCount": 0,
            "likeCount": 0,
            "scrapCount": 0,
            "resource": null,
            "attachments": [
                {
                    "id": 2648474,
                    "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/f492efc8-4017-4d82-9a91-3edff10fad48.png"
                }
            ],
            "posters": null,
            "owner": {
                "id": 1,
                "nickname": "Moyamo",
                "photoUrl": null,
                "level": 6,
                "role": "admin",
                "status": "normal",
                "levelInfo": {
                    "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge5.png",
                    "level": 6,
                    "name": "God"
                },
                "badgeCount": 2,
                "representBadge": null,
                "totalPhotosCnt": 1,
                "totalPhotosLikeCnt": 0,
                "newbie": false
            },
            "postingType": "photo",
            "createdAt": "2021-05-10T13:01:37.339+09:00",
            "modifiedAt": "2021-05-10T13:01:37.339+09:00",
            "youtubeId": null,
            "adopt": false
        },
        {
            "id": 2002820,
            "title": null,
            "text": "Smile 3",
            "readCount": 0,
            "commentCount": 0,
            "likeCount": 0,
            "scrapCount": 0,
            "resource": null,
            "attachments": [
                {
                    "id": 2648475,
                    "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/d362f877-70f9-4b08-97c8-8ae9593e755f.png"
                }
            ],
            "posters": null,
            "owner": {
                "id": 1,
                "nickname": "Moyamo",
                "photoUrl": null,
                "level": 6,
                "role": "admin",
                "status": "normal",
                "levelInfo": {
                    "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge5.png",
                    "level": 6,
                    "name": "God"
                },
                "badgeCount": 2,
                "representBadge": null,
                "totalPhotosCnt": 1,
                "totalPhotosLikeCnt": 0,
                "newbie": false
            },
            "postingType": "photo",
            "createdAt": "2021-05-10T13:01:37.52+09:00",
            "modifiedAt": "2021-05-10T13:01:37.52+09:00",
            "youtubeId": null,
            "adopt": false
        }
    ]
    
    ```

### <code>GET</code>  /v2/photoAlbums/{id}/photoCount

View number of photos in photo album

- Parameter (multipart/form)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Current photo album ID | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |


    ```
        
  
    {
        2
    }
    
    ```




### <code>POST</code> /v2/photos

Photo registration: multiple at once

- Parameter (multipart/form)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `postingType` | type |true | |
  | `texts` | Written content | ||
  | `files` | File (multiple) | true ||
  | `albumNames` | Album Name | | |
  
- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
    
    ```
        
  
    [
        {
            "id": 2002806,
            "title": null,
            "text": "Is it fast? 2",
            "readCount": 0,
            "commentCount": 0,
            "likeCount": 0,
            "scrapCount": 0,
            "resource": null,
            "attachments": [
                {
                    "id": 2648460,
                    "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/f7f4fb30-90cd-4877-8b18-70ac36cbe6e5.png"
                }
            ],
            "posters": null,
            "owner": {
                "id": 39,
                "nickname": "test",
                "photoUrl": null,
                "level": 2,
                "role": "user",
                "status": "normal",
                "levelInfo": {
                    "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                    "level": 2,
                    "name": "seconds"
                },
                "badgeCount": 0,
                "representBadge": null,
                "totalPhotosCnt": 1,
                "totalPhotosLikeCnt": 0,
                "newbie": false
            },
            "postingType": "photo",
            "createdAt": "2021-05-07T15:42:02.496+09:00",
            "modifiedAt": "2021-05-07T15:42:02.496+09:00",
            "youtubeId": null,
            "adopt": false
        },
        {
            "id": 2002807,
            "title": null,
            "text": "Is it fast? 3",
            "readCount": 0,
            "commentCount": 0,
            "likeCount": 0,
            "scrapCount": 0,
            "resource": null,
            "attachments": [
                {
                    "id": 2648461,
                    "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                }
            ],
            "posters": null,
            "owner": {
                "id": 39,
                "nickname": "test",
                "photoUrl": null,
                "level": 2,
                "role": "user",
                "status": "normal",
                "levelInfo": {
                    "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                    "level": 2,
                    "name": "seconds"
                },
                "badgeCount": 0,
                "representBadge": null,
                "totalPhotosCnt": 1,
                "totalPhotosLikeCnt": 0,
                "newbie": false
            },
            "postingType": "photo",
            "createdAt": "2021-05-07T15:42:02.637+09:00",
            "modifiedAt": "2021-05-07T15:42:02.637+09:00",
            "youtubeId": null,
            "adopt": false
        }
    ]
    
    ```


### <code>PUT</code>  /v2/photos/{id}

Edit photo (application/json)

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo post to be edited | | |
  | `text` | After editing, photo post body content | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |


    ```
        
  
         [
            {
                "id": 2002806,
                "title": null,
                "text": "Is it fast? 2",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "resource": null,
                "attachments": [
                    {
                        "id": 2648460,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/f7f4fb30-90cd-4877-8b18-70ac36cbe6e5.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 3,
                    "totalPhotosLikeCnt": 0,
                    "newbie": false
                },
                "postingType": "photo",
                "createdAt": "2021-05-07T15:42:02+09:00",
                "modifiedAt": "2021-05-07T15:47:14+09:00",
                "youtubeId": null,
                "adopt": false
            }
        ]
    
    ```


### <code>PUT</code> /v2/photos/delete

Delete photos: multiple photos at once

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo post to be deleted | false | |
  | `albumName` | Album name where the photo post to be deleted is located | | 'All' |
  | `all` | Whether to select all boolean | | `false` |
  
- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |

    ```
        
  
         [
            {
  "id": 2002806,
                "title": null,
                "text": "Is it fast? 2",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "resource": null,
                "attachments": [
                    {
                        "id": 2648460,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/f7f4fb30-90cd-4877-8b18-70ac36cbe6e5.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 3,
                    "totalPhotosLikeCnt": 0,
                    "newbie": false
                },
                "postingType": "photo",
                "createdAt": "2021-05-07T15:42:02+09:00",
                "modifiedAt": "2021-05-07T15:48:30+09:00",
                "youtubeId": null,
                "adopt": false
            }
        ]
    
    ```



### <code>PUT</code> /v2/photos/move

Move photos: multiple photos at once

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo post to move | false | |
  | `beforeAlbumName` | Album name where the photo post to be moved is located | true | |
  | `afterAlbumName` | Album name to which photo posts will be moved | true | |
  | `all` | Whether to select all boolean | | `false` |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |


    ```
        
  
         [
            {
                "id": 2002807,
                "title": null,
                "text": "Is it fast? 3",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "resource": null,
                "attachments": [
                    {
                        "id": 2648461,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 2,
                    "totalPhotosLikeCnt": 0,
                    "newbie": false
                },
                "postingType": "photo",
                "createdAt": "2021-05-07T15:42:03+09:00",
                "modifiedAt": "2021-05-07T15:42:03+09:00",
                "youtubeId": null,
                "adopt": false
            }
        ]
    
    ```

### <code>PUT</code>  /v2/photos/copy

Photo Copy: Multiple copies available at once

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | ID of photo post to be copied | false | |
  | `afterAlbumNames` | Album name to which photo posts will be copied | true | |
  | `all` | Whether to select all boolean | | `false` |
  | `currentAlbumName` | Current album name (current album name you need to know when selecting all) | false | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | Post ID | | |
      | `text` | content | | |
      | `readCount` | Reads | | |
      | `likeCount` | Likes | | |
      | `attachments.photoUrl` | Attached file list : Image path | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |


    ```
        
  
         [
            {
                "id": 2002807,
                "title": null,
                "text": "Is it fast? 3",
                "readCount": 0,
                "commentCount": 0,
                "likeCount": 0,
                "scrapCount": 0,
                "reportCount": 0,
                "goodses": [],
                "resource": null,
                "attachments": [
                    {
                        "id": 2648461,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                    }
                ],
                "posters": null,
                "owner": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "activity": {
                        "postingCount": 224,
                        "commentCount": 11,
                        "adoptedCount": 1
                    },
                    "provider": "phone",
                    "createdAt": "2014-11-01T02:57:10+09:00",
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 2,
                    "totalPhotosLikeCnt": 0,
                    "newbie": false
                },
                "isDelete": false,
                "isBlind": false,
                "postingType": "photo",
                "createdAt": "2021-05-07T15:42:03+09:00",
                "modifiedAt": "2021-05-07T15:42:03+09:00",
                "youtubeId": null,
                "orgAttachments": [
                    {
                        "id": 2648461,
                        "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                    }
                ],
                "orgText": "Is it fast? 3",
                "orgPosters": null,
                "adopt": false
            }
        ]
    
    ```

### <code>GET</code> /v2/photos/{id}

Photo lookup

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | ID | | |
      | `text` | content | | |
      | `likeCount` | Likes | | |
      | `attachments` | Attachment List | | |
      | `owner` | Author | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |
      | `isLike` | Like or not | | |
    
```
    {
        "id": 2002845,
        "title": null,
        "text": "Golden 4",
        "readCount": 1,
        "commentCount": 0,
        "likeCount": 0,
        "scrapCount": 0,
        "goodses": [],
        "resource": null,
        "attachments": [
            {
                "id": 2648500,
                "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/12/75e1fc63-9226-4170-8407-00ca4065aabf.png"
            }
        ],
        "posters": null,
        "owner": {
            "id": 1,
            "nickname": "Moyamo",
            "photoUrl": null,
            "level": 6,
            "role": "admin",
            "status": "normal",
            "levelInfo": {
                "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge5.png",
                "level": 6,
                "name": "God"
            },
            "badgeCount": 2,
            "representBadge": null,
            "totalPhotosCnt": 5,
            "totalPhotosLikeCnt": 1,
            "newbie": false
        },
        "postingType": "photo",
        "createdAt": "2021-05-12T15:44:21+09:00",
        "modifiedAt": "2021-05-17T10:12:07+09:00",
        "youtubeId": null,
        "adopt": false,
        "isLike": false
    }
```


### <code>POST</code> /v2/photos/{id}/like

I like the photo

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||

- Response (application/json)

    * success response

```
    true
```


### <code>DELETE</code> /v2/photos/{id}/like

Cancel like photo

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||

- Response (application/json)

    * success response

```
    true
```

### <code>POST</code> /v2/photos/{id}/report

Report a photo

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | Post ID | ||
  | `title` | Reported items | ||
  | `text` | content | ||

- Response (application/json)

    * success response

```
    true, false
```

### <code>GET</code>  /v2/search/writerUsers

by author

- Parameter (application/json)

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `orderby` | Sort order ( id : Newest, photoCount : Most photos, photoLikeCount : Popular ) | | id |
  | `sinceId` | | | |
  | `maxId` | | | |
  | `offset` | | | 0 |
  | `count` | | | 10 |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `timelineId` | Use this field when viewing the timeline (sinceId, maxId) | | |
      | `user` | user | | |
      | `user.id` | User ID | | |
      | `user.nickname` | User Nickname | | |
      | `user.photoUrl` | user photo url | | |
      | `user.totalPhotosCnt` | Total number of photos | | |
      | `user.totalPhotosLikeCnt` | Total number of hearts (photo only) | | |
      | `photos` | Representative Photos | | |
      | `photos.id` | Post ID | | |
      | `photos.attachments.photoUrl` | Attached file list : Image path | | |
      | `postingType` | Post Type | | |
      | `createdAt` | Creation Date | | |
      | `modifiedAt` | Date of modification | | |


    ```
        
  
         [
            {
                "timelineId": 24,
                "user": {
                    "id": 39,
                    "nickname": "test",
                    "photoUrl": null,
                    "level": 2,
                    "role": "user",
                    "status": "normal",
                    "levelInfo": {
                        "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                        "level": 2,
                        "name": "seconds"
                    },
                    "badgeCount": 0,
                    "representBadge": null,
                    "totalPhotosCnt": 4,
                    "totalPhotosLikeCnt": 3,
                    "newbie": false
                },
                "photos": [
                    {
                        "id": 2002811,
                        "title": null,
                        "text": "Is it fast? 4",
                        "readCount": 0,
                        "commentCount": 0,
                        "likeCount": 0,
                        "scrapCount": 0,
                        "resource": null,
                        "attachments": [
                            {
                                "id": 2648466,
                                "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/48ac10d4-b521-402f-9eb5-5efe554f7551.png"
                            }
                        ],
                        "posters": null,
                        "owner": {
                            "id": 39,
                            "nickname": "test",
                            "photoUrl": null,
                            "level": 2,
                            "role": "user",
                            "status": "normal",
                            "levelInfo": {
                                "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                                "level": 2,
                                "name": "seconds"
                            },
                            "badgeCount": 0,
                            "representBadge": null,
                            "totalPhotosCnt": 4,
                            "totalPhotosLikeCnt": 3,
                            "newbie": false
                        },
                        "postingType": "photo",
                        "createdAt": "2021-05-10T09:08:07+09:00",
                        "modifiedAt": "2021-05-10T09:08:07+09:00",
                        "youtubeId": null,
                        "adopt": false
                    },
                    {
                        "id": 2002810,
                        "title": null,
                        "text": "Is it fast? 2",
                        "readCount": 0,
                        "commentCount": 0,
                        "likeCount": 0,
                        "scrapCount": 0,
                        "resource": null,
                        "attachments": [
                            {
                                "id": 2648465,
                                "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/10/61933772-54e3-4944-9781-84a32a950a49.png"
                            }
                        ],
                   "posters": null,
                        "owner": {
                            "id": 39,
                            "nickname": "test",
                            "photoUrl": null,
                            "level": 2,
                            "role": "user",
                            "status": "normal",
                            "levelInfo": {
                                "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                                "level": 2,
                                "name": "seconds"
                            },
                            "badgeCount": 0,
                            "representBadge": null,
                            "totalPhotosCnt": 4,
                            "totalPhotosLikeCnt": 3,
                            "newbie": false
                        },
                        "postingType": "photo",
                        "createdAt": "2021-05-10T08:44:49+09:00",
                        "modifiedAt": "2021-05-10T08:44:49+09:00",
                        "youtubeId": null,
                        "adopt": false
                    },
                    {
                        "id": 2002807,
                        "title": null,
                        "text": "Is it fast? 3",
                        "readCount": 2,
                        "commentCount": 0,
                        "likeCount": 2,
                        "scrapCount": 0,
                        "resource": null,
                        "attachments": [
                            {
                                "id": 2648461,
                                "photoUrl": "http://d1ujq2dpv14gay.cloudfront.net/dev/postings/2021/05/07/13b137ee-0e4e-4760-bdc3-e214bd16896c.png"
                            }
                        ],
                        "posters": null,
                        "owner": {
                            "id": 39,
                            "nickname": "test",
                            "photoUrl": null,
                            "level": 2,
                            "role": "user",
                            "status": "normal",
                            "levelInfo": {
                                "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                                "level": 2,
                                "name": "seconds"
                            },
                            "badgeCount": 0,
                            "representBadge": null,
                            "totalPhotosCnt": 4,
                            "totalPhotosLikeCnt": 3,
                            "newbie": false
                        },
                        "postingType": "photo",
                        "createdAt": "2021-05-07T15:42:03+09:00",
                        "modifiedAt": "2021-05-07T15:42:03+09:00",
                        "youtubeId": null,
                        "adopt": false
                    }
                ]
            },
            ...
        ]
    
    ```



### <code>GET</code> /v2/photos/popularWriters

popular writer

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  
- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |
      | `id` | User ID | | |
      | `nickname` | User Nickname | | |
      | `photoUrl` | user photo url | | |
      | `totalPhotosCnt` | Total number of photos | | |
      | `totalPhotosLikeCnt` | Total number of hearts (photo only) | | |



    ```
        
  
         [
            {
           "id": 39,
                "nickname": "test",
                "photoUrl": null,
                "level": 2,
                "role": "user",
                "status": "normal",
                "levelInfo": {
                    "badge": "http://d1ujq2dpv14gay.cloudfront.net/dev/commons/badges/badge1.png",
                    "level": 2,
                    "name": "seconds"
                },
                "activity": {
                    "postingCount": 226,
                    "commentCount": 11,
                    "adoptedCount": 1
                },
                "provider": "phone",
                "createdAt": "2014-11-01T02:57:10+09:00",
                "badgeCount": 0,
                "representBadge": null,
                "totalPhotosCnt": 4,
                "totalPhotosLikeCnt": 3,
                "newbie": false
            },
            ...
        ]
    
    ```


### <code>POST</code> /v2/photoAlbums/{id}/share

Sharing photo albums

- Parameter

  | attribute| description | required | default |
  | --- | --- | -------- | --- |
  | `id` | User ID | | |

- Response (application/json)

    * field

      | attribute| description | required | default |
      | --- | --- | -------- | --- |


    ```
        
  
         [
            {
               "uuid": "45e32f18-a228-4392-afd1-2d47c5527475",
                "resource": {
                    "resourceId": "39",
                    "resourceType": "photoalbum",
                    "referenceId": "39",
                    "referenceType": "photoalbum"
                },
                "deeplink": "https://go.flabook.co.kr/UTjp9982H1MZ5H516"
            }
        ]
    
    ```

* Link definition

```
            https://go.flabook.co.kr/skGMeE9DQNdvVHxm8
            
            -> https://link.flabook.co.kr/photoAlbums/39?postingType=photoalbum&code=45e32f18-a228-4392-afd1-2d47c5527475
```