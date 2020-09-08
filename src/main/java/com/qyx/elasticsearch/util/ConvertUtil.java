package com.qyx.elasticsearch.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合转换工具类
 *
 * @author : huzhiting
 * @date : 2020-09-04 15:29
 */
@Component
public class ConvertUtil {
    /**
     * list转为List<Map<String,Object>>
     *
     * @param list
     * @return
     */
    public List<Map<String, Object>> convertListMap(List<?> list) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Object obj : list) {
            Class c = obj.getClass();
            Field[] f = c.getDeclaredFields();
            Map<String, Object> map = new HashMap<>();
            for (Field fie : f) {
                try {
                    //取消语言访问检查
                    fie.setAccessible(true);
                    //获取私有变量值
                    map.put(fie.getName(), fie.get(obj));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            //获取父类的私有属性
            for (Field fie : c.getSuperclass().getDeclaredFields()) {
                try {
                    //取消语言访问检查
                    fie.setAccessible(true);
                    //获取私有变量值
                    map.put(fie.getName(), fie.get(obj));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            maps.add(map);
        }
        return maps;
    }
}
