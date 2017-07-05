package org.hyperfit.hyperresource.controls;

import org.springframework.util.StringUtils;

import lombok.ToString;

@ToString
public class Link {

    private final String href;
    private final String rel;
    private final String name;
    private final String type;
    private final String title;

    public Link(String rel, String href) {
        this(rel, href, null, null, null);
    }

    public Link(String rel, String href, String name, String type) {
        this(rel, href, name, type, null);
    }

    public Link(String rel, String href, String name, String type, String title) {
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
        this.title = title;
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

    public String getTitle() {
        return title;
    }
}
