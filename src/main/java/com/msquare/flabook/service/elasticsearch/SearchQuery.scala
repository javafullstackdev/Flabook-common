package com.msquare.flabook.service.elasticsearch

object SearchQuery {

  def search =
    """{
                     "query": {
                         "function_score": {
                             "query": {
                                 "match": {
                                    "posting_text": "{{query}}"
                                 }
                             },
                             "functions": [
                                 {
                                     "script_score": {
                                        "script": "doc[\"readCount\"].value * 10"
                                     }
                                 }
                             ]
                         }
                     }
                 }"""


  def search2 =
    """{
                     "query": {
                         "function_score": {
                             "query": {
                                 "match": {
                                    "posting_text": "{{query}}"
                                 }
                             },
                             "functions": [
                                 {
                                     "script_score": {
                                        "script": "doc[\"readCount\"].value * 10"
                                     }
                                 },

                             ]
                         }
                     }
                 }"""


  def script(baseScore: Int, baseLevel: Int, levelWeight: Int, commentWeight: Int): String = s"""String script = "($baseScore - doc['receivingCount'].value + (((doc['owner.level'].value >= $baseLevel) ? doc['owner.level'].value : 0) * $levelWeight)) - (doc['commentCount'].value * $commentWeight)""".stripMargin


  /*

   옛날 계산식
    scoreExp = String.format("%d - %d + "
      + "(((%d >= %d) ? %d : 0) * %d)"
      + " + (%d * %d) + %d"
      , FlabookPost.FeedUtils.FeedStatusConfig.DEFAULT_SCORE, receiving
      , creatorLevel, FlabookPost.FeedUtils.FeedStatusConfig.BASE_USER_LEVEL, levelScore, levelWeight
      ,subScore, statusWeight, plantcareScore);



    int commentSubPoint = commentCount * FlabookPost.FeedUtils.FeedStatusConfig.COMMENT_POINT_PER;
    scoreExp += String.format(" - %d", commentSubPoint)
  */


  //PUT /flabook/_settings


  /*
   * GET /flabook/_analyze?analyzer=korean&pretty=true
   * {
   * "tokenizer": "standard",
   * "text" :"하늘장미"
   * }
   *
   */


  def open(index: String) = s"$index/_open"

  def close(index: String) = s"$index/_close"

  def settings =
    """
      {
        "settings" : {
          "index":{
            "analysis":{
              "analyzer":{
                "korean":{
                  "type":"custom",
                  "tokenizer":"mecab_ko_standard_tokenizer"
                }
              },
                "tokenizer": {
                  "mecab_ko_standard_tokenizer": {
                    "decompound": "true",
                    "type": "seunjeon_tokenizer"
                  }
                }
            }
          }
        }
      }
      """.stripMargin

  //yyyy-MM-dd'T'HH:mm:ss.SSSZZ'['ZZZ']

  /*
  {
  "sort" : [
        { "id" : {"order" : "desc"}},
        "_score"
    ],
  "query": {
        "function_score": {
            "score_mode": "sum",
            "boost_mode": "multiply",
              "query": {
                "match_all" : {}
              }
                ,
                  "functions": [
                           {
                             "script_score": {
                               "script": "doc[\"readCount\"].value * 10"
                }}	      ,{

                    "weight": 5,
                    "gauss": {
                        "createdAt": {
                            "origin": "2020-04-23T08:52:47.557+09:001[Asia/Seoul]",
                            "scale": "3600s",
                            "decay": 0.5
                        }
                    }

             }
           ]
       }
  }
}
   */

  //"min_score": 0,


}
