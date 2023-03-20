package cn.RPC.server.service;

public class helloServiceImpl implements helloService{
    @Override
    public String sayHello(String name) {
        int a=1/0;
        System.out.println("hello"+name);
        return "成功调用";
    }
}
