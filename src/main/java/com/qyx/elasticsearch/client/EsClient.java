package com.qyx.elasticsearch.client;

import com.alibaba.fastjson.JSON;
import com.qyx.elasticsearch.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Es操作工具类
 *
 * @author : huzhiting
 * @date : 2020-09-04 12:00
 */
@Slf4j
@Component
public class EsClient {

    private RestHighLevelClient client;

    /**
     * Java High Level REST Client  初始化
     */
    public EsClient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.17.146", 9200, "http")));
    }

    /**
     * 新增，修改文档
     *
     * @param indexName 索引
     * @param type      mapping type
     * @param id        文档id
     * @param jsonStr   文档数据
     */
    public IndexResponse addData(String indexName, String type, String id, String jsonStr) {
        IndexResponse indexResponse = null;
        try {
            // 1、创建索引请求  //索引  // mapping type  //文档id
            IndexRequest request = new IndexRequest(indexName, type, id);
            // 2、准备文档数据
            // 直接给JSON串
            request.source(jsonStr, XContentType.JSON);
            //4、发送请求
            try {
                // 同步方式
                indexResponse = client.index(request, RequestOptions.DEFAULT);
            } catch (ElasticsearchException e) {
                // 捕获，并处理异常
                //判断是否版本冲突、create但文档已存在冲突
                if (e.status() == RestStatus.CONFLICT) {
                    log.error("[ESUtil.addData] [error] [conflict]", e);
                }
            }
            //5、处理响应
            if (indexResponse != null) {
                String index1 = indexResponse.getIndex();
                String type1 = indexResponse.getType();
                String id1 = indexResponse.getId();
                long version1 = indexResponse.getVersion();
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    log.info("[ESUtil.addData] [end] [create success] [result is {}]", index1 + type1 + id1 + version1);
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    log.info("[ESUtil.addData] [end] [update success]");
                }
                // 分片处理信息
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    log.info("[ESUtil.addData] [start] [sharding info]");
                }
                // 如果有分片副本失败，可以获得失败原因信息
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason();
                        log.error("[ESUtil.addData] [error] [replicate failure, reason is {}]", reason);
                    }
                }
                return indexResponse;
            }

        } catch (Exception e) {
            log.error("[ESUtil.addData] [error] [fail to add data]", e);
        }

        return indexResponse;
    }

    /**
     * 查询指定文档数据
     *
     * @param request
     * @return
     */
    public GetResponse queryOne(GetRequest request) {
        GetResponse documentFields = null;
        try {
            documentFields = client.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[ESUtil.queryOne] [error] [fail to query one, param is {}]", JSON.toJSON(request), e);
        }
        return documentFields;
    }

    /**
     * 条件查询数据
     *
     * @param request
     * @return
     */
    public Map<String, Object> queryAll(SearchRequest request) {
        Map<String, Object> resultMap = new HashMap<>(10);
        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
            //从查询结果中获得想要数据
            SearchHits searchHits = response.getHits();
            //获得查询结果条数
            long total = searchHits.getTotalHits();
            resultMap.put("total", total);
            resultMap.put("tookInMillis", response.getTook().getMillis());
            //获得查询结果并转换为map
            if (total > 0) {
                SearchHit[] searchHitsArr = searchHits.getHits();
                List<Map<String, Object>> resultMapList = convertResultToObject(searchHitsArr);
                resultMap.put("list", resultMapList);
            }
        } catch (IOException e) {
            log.error("[ESUtil.queryAll] [error] [fail to query, param is {}]", JSON.toJSON(request), e);
            return resultMap;
        }
        return resultMap;
    }

    /**
     * ES 检索结果格式转换
     *
     * @param searchHitsArr
     * @return
     */
    private List<Map<String, Object>> convertResultToObject(SearchHit[] searchHitsArr) {
        List<Map<String, Object>> resultMapList = new ArrayList<>(searchHitsArr.length);
        for (SearchHit documentFields : searchHitsArr) {
            Map<String, Object> map = documentFields.getSourceAsMap();
            resultMapList.add(map);
        }
        return resultMapList;
    }

    /**
     * 条件查询数据（分页）
     *
     * @param request
     * @return
     */
    public PageResult pageAll(SearchRequest request) {
        PageResult pageResult = new PageResult();
        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
            //从查询结果中获得想要数据
            SearchHits searchHits = response.getHits();
            //获得查询结果条数
            long total = searchHits.getTotalHits();
            pageResult.setTotal(Integer.parseInt(String.valueOf(total)));
            //获得查询结果并转换为map
            if (total > 0) {
                SearchHit[] searchHitsArr = searchHits.getHits();
                List<Map<String, Object>> resultMapList = convertResultToObject(searchHitsArr);
                pageResult.setRecords(resultMapList);
            }
        } catch (IOException e) {
            log.error("[ESUtil.queryAll] [error] [fail to query, param is {}]", JSON.toJSON(request), e);
            return pageResult;
        }
        return pageResult;
    }

    /**
     * 批量插入ES
     *
     * @param indexName 索引
     * @param type      类型
     * @param idName    id名称
     * @param list      数据集合
     */
    public void bulkData(String indexName, String type, String idName, List<Map<String, Object>> list) {
        try {
            if (null == list || list.size() <= 0) {
                return;
            }
            if (StringUtils.isBlank(indexName) || StringUtils.isBlank(idName) || StringUtils.isBlank(type)) {
                return;
            }
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> map : list) {
                if (map.get(idName) != null) {
                    request.add(new IndexRequest(indexName, type, String.valueOf(map.get(idName)))
                            .source(map, XContentType.JSON));
                }
            }
            // 同步请求
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            //处理响应
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        log.info("[ESUtil.bulkData] [end] [create success, response is {}]", indexResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        log.info("[ESUtil.bulkData] [end] [update success, response is {}]", updateResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        log.info("[ESUtil.bulkData] [end] [delete success, response is {}]", deleteResponse.toString());
                    }
                }
            }
        } catch (IOException e) {
            log.error("[ESUtil.bulkData] [error] [fail to bulk data]", e);
        }
    }


    /**
     * 删除索引数据
     *
     * @param request
     * @return
     */
    public String deleteData(DeleteRequest request) {
        DeleteResponse response = new DeleteResponse();
        try {
            response = client.delete(request, RequestOptions.DEFAULT);
            if (DocWriteResponse.Result.DELETED == response.getResult()) {
                return response.toString();
            }
        } catch (IOException e) {
            log.error("[EsUtil.deleteData] [error] [delete data fail, request is {}]", request, e);
        }
        return response.toString();
    }

    /**
     * 检查某索引是否存在
     *
     * @param request
     * @return
     */
    public boolean checkIndexExist(GetRequest request) {
        try {
            return client.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("[EsUtil.checkIndexExist] [error] [request is {}.", request, e);
        }
        return false;
    }
}
