package com.yinrj.emos.wx.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 请求包装类
 * @date 2021/3/1
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return filterString(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        InputStream inputStream = super.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        inputStream.close();

        // json字符串转成json
        Map<String, Object> map = JSONUtil.parseObj(sb.toString());
        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (!map.isEmpty()) {
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value instanceof String) {
                    resultMap.put(key, filterString(value.toString()));
                } else {
                    resultMap.put(key, value);
                }
            }
        }

        String str = JSONUtil.toJsonStr(resultMap);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public String getParameter(String name) {
        return filterString(super.getParameter(name));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> valueMap = super.getParameterMap();
        return filterMap(valueMap);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        return filterStringArray(values);
    }

    private String filterString(String value) {
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    private String[] filterStringArray(String[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                values[i] = filterString(values[i]);
            }
        }
        return values;
    }

    private Map<String, String[]> filterMap(Map<String, String[]> valueMap) {
        Map<String, String[]> map = new LinkedHashMap<>();
        if (!valueMap.isEmpty()) {
            for (String key : valueMap.keySet()) {
                map.put(key, filterStringArray(valueMap.get(key)));
            }
        }
        return map;
    }
}
