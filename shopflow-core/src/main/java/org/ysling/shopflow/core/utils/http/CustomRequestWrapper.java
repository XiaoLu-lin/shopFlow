package org.ysling.shopflow.core.utils.http;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * 解决 getReader()/getInputStream() has already been called for this request
 */
public class CustomRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 请求体参数
     */
    private final byte[] body;

    /**
     * 获取当前请求体参数
     * @param request       当前请求
     */
    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = getBody(request);
    }

    /**
     * 获取当前请求体数据
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * 重写http请求体getReader
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 读取请求体参数
     * @param request   http请求
     */
    public byte[] getBody(HttpServletRequest request) throws IOException{
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];  // 调整缓冲区大小为8KB，可根据实际情况进行调整
            int bytesRead;
            ServletInputStream is = request.getInputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return os.toByteArray();
        }
    }

    /**
     * 重写http请求体getInputStream
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public boolean isFinished() {
                return false;
            }
        };
    }


}
