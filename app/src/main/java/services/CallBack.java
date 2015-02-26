package services;

import java.lang.reflect.Method;

/**
 * Created by hasanali on 2/10/14.
 */
public class CallBack {
    Object caller;
    String method;

    public CallBack(Object callie, String name){
        this.caller = callie;
        this.method = name;
    }

    public void invoke(Object obj){

        Method method;
        Class partypes[] = new Class[2];
        partypes[0] = Object.class;
        partypes[1] = Object.class;

        Object[] args = new Object[2];
        args[0] = caller;
        args[1] = obj;

        try {
            method = caller.getClass().getMethod(this.method, partypes);
            method.invoke(caller, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
