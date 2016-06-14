package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import lombok.ToString;

@ToString
public class Link {

    private final String href;
    private final String rel;
    private final String name;
    private final String type;

    public Link(String rel, String href) {
        this(rel, href, null, null);
    }

    public Link(String rel, String href, String name, String type) {
        if (!StringUtils.hasText(rel)) {
            throw new IllegalArgumentException("rel cannot be null or empty");
        }
        this.rel = rel;

        if (!StringUtils.hasText(href)) {
            throw new IllegalArgumentException("href cannot be null or empty");
        }
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
