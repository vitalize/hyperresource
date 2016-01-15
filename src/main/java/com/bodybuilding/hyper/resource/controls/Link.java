package com.bodybuilding.hyper.resource.controls;

import org.springframework.util.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Link {

    private final String href;
    private final String rel;
    private final String name;
    private final String type;

    public Link(String rel, String href) {

        if (StringUtils.isEmpty(rel)) {
            throw new IllegalArgumentException("Rel cannot be empty");
        }
        if (StringUtils.isEmpty(href)) {
            throw new IllegalArgumentException("Href cannot be empty");
        }
        this.rel = rel;
        this.href = href;
        this.name = null;
        this.type = null;
    }

    public Link(String rel, String href, String name, String type) {
        if (StringUtils.isEmpty(rel)) {
            throw new IllegalArgumentException("Rel cannot be empty");
        }
        if (StringUtils.isEmpty(href)) {
            throw new IllegalArgumentException("Href cannot be empty");
        }
        this.rel = rel;
        this.href = href;
        this.name = name;
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
