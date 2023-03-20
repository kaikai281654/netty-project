package cn.kai.config;

import cn.kai.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {

    static Properties properties;

    static {
        try(InputStream in = Config.class.getResourceAsStream("/application.properties")){

            properties = new Properties();
            properties.load(in);

        } catch (IOException e){

            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getServerPort(){
        final String value = properties.getProperty("server.port");

        if(value == null)
        {
            return 8080;
        }else{
            return Integer.parseInt(value);
        }
    }

    public static Serializer.Algorithm getMySerializerAlgorithm() {

        final String value = properties.getProperty("mySerializer.algorithm");
        if (value == null) {
            return Serializer.Algorithm.java;
        } else {
            // 拼接成  MySerializer.Algorithm.Java 或 MySerializer.Algorithm.Json
            return Serializer.Algorithm.valueOf(value);
        }
    }



}
