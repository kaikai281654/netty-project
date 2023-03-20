package cn.kai.protocol;

import cn.kai.message.Message;
import com.google.gson.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public interface Serializer {


    //反序列化
    <T> T deserialize(Class<T> clazz,byte[] bytes);

    //序列化
    <T> byte[] serialize(T object) throws IOException;


    enum Algorithm implements Serializer{
        java{
            //基于java实现
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                // 处理内容

                try {
                     ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois;
                        ois = new ObjectInputStream(bis);
                        return  (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败",e);
                }

                // 转成 Message类型

            }


            @Override
            public <T> byte[] serialize(T object){
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos=new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    byte[] bytes = bos.toByteArray();
                    return bytes;
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败",e);
                }

            }
        },

        json{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json=new String(bytes,StandardCharsets.UTF_8);
                return gson.fromJson(json,clazz);
            }

            @Override
            public <T> byte[] serialize(T object) throws IOException {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return  json.getBytes(StandardCharsets.UTF_8);
            }
        }




    }



// 针对之前报出：不支持 Class类转json的异常 做处理
class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
    @Override
    public Class<?> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            String str = json.getAsString();
            return Class.forName(str);

        } catch (ClassNotFoundException e) {

            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Class<?> src, Type type, JsonSerializationContext jsonSerializationContext) {

        // JsonPrimitive 转化基本数据类型
        return new JsonPrimitive(src.getName());
    }

}









}
