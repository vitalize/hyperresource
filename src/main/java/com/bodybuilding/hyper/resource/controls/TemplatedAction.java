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
        if(!StringUtils.hasText(builder.name)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = builder.name;

        if(!StringUtils.hasText(builder.href)) {
            throw new IllegalArgumentException("href cannot be null or empty");
        }
        this.href = builder.href;

        //fieldsets cannot be null, but the builder protects us from this currently
        this.fieldSets = builder.fieldSets;
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