package com.bodybuilding.hyper.resource.controls;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.util.StringUtils;


@EqualsAndHashCode
@ToString
public class TemplatedAction {
    
    private final String name;
    private final List<FieldSet> fieldSets;
    private final String href;
    
    private TemplatedAction(Builder builder) {
        if(StringUtils.isEmpty(builder.name)) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        if(StringUtils.isEmpty(builder.href)) {
            throw new IllegalArgumentException("href cannot be empty");
        }
        this.name = builder.name;
        this.fieldSets = builder.fieldSets;
        this.href = builder.href;
    }
    
    public String getName() {
        return name;
    }

    public List<FieldSet> getFieldSets() {
        return fieldSets;
    }

    public String getHref() {
        return href;
    }
    
    public static class Builder {
        
        private String name;
        private List<FieldSet> fieldSets = new ArrayList<FieldSet>();
        private String href;
        
        public TemplatedAction build() {
            return new TemplatedAction(this);
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder addFieldSet(FieldSet fieldSet) {
            this.fieldSets.add(fieldSet);
            return this;
        }
        
        public Builder href(String href) {
            this.href = href;
            return this;
        }
        
    }

}