#elasticsearch install approach

## Download seunjeon
```bash
bash curl -s https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/scripts/downloader.sh -e 5.6.16 -p 5.4.1.1
```
#### Move downloaded zip file to the root directory of ES installed

## install seunjeon
```run the below command on the root directory of ES
(example) 
./bin/elasticsearch-plugin install file://`pwd`/elasticsearch-analysis-seunjeon-5.4.1.1.zip
```

## set analyzer 

-aws elasticsearch cannot be used because it cannot execute the _close command. Requires setting through alias

#### Run the below commands on different terminals respectively to create flabook index.
```
.\bin\elasticsearch
```

``` Copy this commands to shell script file and run bash ```

```
curl -XPUT 'http://localhost:9200/flabook'

curl -XPOST 'http://localhost:9200/flabook/_close'

curl -XPUT 'http://localhost:9200/flabook/_settings' -d '
{
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

curl -XPOST 'http://localhost:9200/flabook/_open'

```

