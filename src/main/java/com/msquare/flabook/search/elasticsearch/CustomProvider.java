package com.msquare.flabook.search.elasticsearch;

import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionProvider;
import org.hibernate.search.elasticsearch.analyzer.definition.ElasticsearchAnalysisDefinitionRegistryBuilder;

/**
json : {
  "settings" : {
    "index":{

      "analysis":{
        "char_filter": {
          "old_nickname_filter": {
            "type": "pattern_replace",
            "pattern": "\\[.*\\]",
            "replacement": ""
          }
        },
        "analyzer":{
          "posting_korean_text_analyzer":{
            "type":"custom",
            "tokenizer":"mecab_ko_standard_tokenizer"
          },
        "goods_korean_text_analyzer":{
            "type":"custom",
            "tokenizer":"mecab_ko_standard_tokenizer"
          },
        "attachment_korean_text_analyzer":{
            "type":"custom",
            "tokenizer":"mecab_ko_standard_tokenizer"
          },
          "comment_korean_text_analyzer":{
            "type":"custom",
            "tokenizer":"mecab_ko_standard_tokenizer",
            "char_filter": [
              "old_nickname_filter"
            ]
          }
        },
        "tokenizer": {
            "mecab_ko_standard_tokenizer": {
              "decompound": "true",
              "type": "seunjeon_tokenizer",
              "index_eojeol": false,
              "user_words": []
            }
        }
      }
    }
  }
}'
 */

public class CustomProvider implements ElasticsearchAnalysisDefinitionProvider {

    private static final String KO_STANDARD_TOKENZER = "mecab_ko_standard_tokenizer";

    @Override
    public void register(ElasticsearchAnalysisDefinitionRegistryBuilder builder) {
        builder.charFilter( "old_nickname_filter" )
                .type( "pattern_replace" )
                .param( "pattern", "\\[.*\\]" )
                .param( "replacement", "" );

        builder.tokenizer(KO_STANDARD_TOKENZER).type("seunjeon_tokenizer")
                .param("index_eojeol", false).param("user_words", "낄낄빠빠");

        builder.analyzer( "posting_korean_text_analyzer" )
                .withTokenizer( KO_STANDARD_TOKENZER );

        builder.analyzer( "goods_korean_text_analyzer" )
                .withTokenizer( KO_STANDARD_TOKENZER );

        builder.analyzer( "attachment_korean_text_analyzer" )
                .withTokenizer( KO_STANDARD_TOKENZER );

        builder.analyzer( "comment_korean_text_analyzer" )
                .withTokenizer( KO_STANDARD_TOKENZER ).withCharFilters("old_nickname_filter");



    }
}
