package kanga.kcae.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import static java.lang.reflect.Proxy.newProxyInstance;

public class ObjectCallHistory implements InvocationHandler {
    public static class Call {
        public Call(String methodName, Object... args) {
            this.methodName = methodName;
            this.args = args;
        }
        
        public final String methodName;
        public final Object[] args;
    }
    
    public ObjectCallHistory() {
        this.calls = new ArrayList<ObjectCallHistory.Call>();
    }
    
    public <T> T getProxy(Class<T> clazz) {
        return clazz.cast(newProxyInstance(this.getClass().getClassLoader(),
                                           new Class[]{ clazz }, this));
    }
    
    public List<Call> getCallHistory() {
        return this.calls;
    }
   
    private final List<Call> calls;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        this.calls.add(new Call(method.getName(), args));
        return null;
    }
}