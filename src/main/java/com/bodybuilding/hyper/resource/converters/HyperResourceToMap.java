package com.bodybuilding.hyper.resource.converters;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.controls.Link;

public class HyperResourceToMap {
    
    private static final Logger LOG = LoggerFactory.getLogger(HyperResourceToMap.class);

    
    public static Map<String, Object> convert(HyperResource hr) {
        Map<String, Object> map = new TreeMap<String, Object>();
        Map<String, List<Link>> linksMap = new TreeMap<String, List<Link>>();
        
        try {
            BeanInfo info = Introspector.getBeanInfo(hr.getClass(), Object.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method m = pd.getReadMethod();
                if (m != null)
                    if(m.getReturnType().equals(Link.class)) {
                        Object o = m.invoke(hr);
                        if(o != null) {
                            Link link = (Link) o;
                            addLinkToMap(linksMap, link);
                        }                                           
                    } else if(m.getReturnType().equals(Link[].class)) {
                        Object o = m.invoke(hr);                    
                        if(o != null) {
                            Link [] links = (Link[]) o;                     
                            for(Link link: links) {
                                addLinkToMap(linksMap, link);
                            }
                        }                   
                    } else {                    
                        map.put(pd.getName(),m.invoke(hr));
                    }
            }
        } catch (Exception e) {
            LOG.error("Error while converting hyper resource to map {}", e);
        }
        
        if(!linksMap.isEmpty()) {
            map.put("_links", linksMap);
        }
        
        return map;
    }    
    
    private static void addLinkToMap(Map<String, List<Link>> linksMap, Link link) {
        if(link != null) {
            if(!linksMap.containsKey(link.getRel())) {
                linksMap.put(link.getRel(), new LinkedList<Link>());
            }
            linksMap.get(link.getRel()).add(link);
        }
    }
    
    
}
