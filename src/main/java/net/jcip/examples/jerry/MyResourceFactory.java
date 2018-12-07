package net.jcip.examples.jerry;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class MyResourceFactory {

    private static class ResourceHolder{
        public static Resource resource = new Resource();
    }

    public static Resource getResource(){
        return MyResourceFactory.ResourceHolder.resource;
    }

    static class Resource{

    }
}
