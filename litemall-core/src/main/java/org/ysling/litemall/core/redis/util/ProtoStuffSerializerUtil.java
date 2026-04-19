package org.ysling.litemall.core.redis.util;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/** 
 * @ClassName: ProtoStuffSerializerUtil 
 * @Description: protostuff序列化
 */
public class ProtoStuffSerializerUtil {
	
	private static Schema<SerializerHolder> SCHEMA = RuntimeSchema.getSchema(SerializerHolder.class);
	private static final Logger LOG = LoggerFactory.getLogger(ProtoStuffSerializerUtil.class);

	private ProtoStuffSerializerUtil() {
	}

	public static <T> byte[] serialize(T obj) {
		LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(new SerializerHolder(obj), SCHEMA, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(String.format("serialize(%s)--(%s) error!", obj.getClass(), obj), e);
		} finally {
			buffer.clear();
		}
		return protostuff;
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] paramArrayOfByte) {
		if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
			return null;
		}
		SerializerHolder instance = new SerializerHolder();
		try {
			ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, SCHEMA);
		} catch (Exception e) {
			LOG.error("deserialize faild, result[{}], error: {}.", instance, e);
		}
		return (T) instance.getObj();
	}

	/**
	 * 序列化中间件，解决泛型问题
	 */
	private static class SerializerHolder implements Serializable {
		private static final long serialVersionUID = 1L;
		// 真正要序列化的对象
		private Object obj;

		public SerializerHolder() {
		}

		public SerializerHolder(Object obj) {
			super();
			this.obj = obj;
		}

		public Object getObj() {
			return obj;
		}

		@SuppressWarnings("unused")
		public void setObj(Object obj) {
			this.obj = obj;
		}

		@Override
		public String toString() {
			return "obj: " + (obj == null ? null : obj.toString());
		}
	}

}
