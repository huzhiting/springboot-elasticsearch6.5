package com.qyx.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.qyx.elasticsearch.common.PageResult;
import com.qyx.elasticsearch.dto.UserDTO;
import com.qyx.elasticsearch.dto.UserPageDTO;
import com.qyx.elasticsearch.service.UserService;
import com.qyx.elasticsearch.util.ConvertUtil;
import com.qyx.elasticsearch.client.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 用户接口Service实现类
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:25
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String INDEX_NAME = "user";
    private static final String INDEX_TYPE = "_doc";

    @Resource
    EsClient esClient;

    @Resource
    ConvertUtil convertUtil;

    @Override
    public String save(UserDTO dto) {
        return esClient.addData(INDEX_NAME, INDEX_TYPE, String.valueOf(dto.getId()), JSON.toJSONString(dto)).toString();
    }

    @Override
    public String queryOne(UserDTO dto) {
        GetRequest request = new GetRequest();
        request.id(dto.getId().toString()).index(INDEX_NAME);
        return esClient.queryOne(request).toString();
    }

    @Override
    public Map<String, Object> queryAll(UserDTO dto) {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(dto.getName())) {
            //wildcardQuery 模糊匹配
            boolQueryBuilder.filter(QueryBuilders.wildcardQuery("name", String.format("*%s*", dto.getName())));
        }
        if (dto.getId() != null) {
            //termQuery 精确匹配
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", dto.getId()));
        }
        builder.query(boolQueryBuilder);
        request.source(builder);
        return esClient.queryAll(request);
    }

    @Override
    public PageResult pageAll(UserPageDTO dto) {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(dto.getName())) {
            //wildcardQuery 模糊匹配
            boolQueryBuilder.filter(QueryBuilders.wildcardQuery("name", String.format("*%s*", dto.getName())));
        }
        builder.query(boolQueryBuilder).from(dto.getPage()).size(dto.getSize());
        request.source(builder);
        return esClient.pageAll(request);
    }

    @Override
    public void bulkUser(List<UserDTO> dto) {
        esClient.bulkData(INDEX_NAME, INDEX_TYPE, "id", convertUtil.convertListMap(dto));
    }

    @Override
    public String deleteAll(Long id) {
        DeleteRequest request = new DeleteRequest();
        request.type(INDEX_TYPE).index(INDEX_NAME).id(id.toString());
        return esClient.deleteData(request);
    }

    @Override
    public boolean findExist(Long id) {
        GetRequest request = new GetRequest();
        request.type(INDEX_TYPE).index(INDEX_NAME).id(id.toString());
        return esClient.checkIndexExist(request);
    }
}
