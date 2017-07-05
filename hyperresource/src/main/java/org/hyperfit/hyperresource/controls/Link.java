package org.hyperfit.hyperresource.controls;

import static org.hyperfit.hyperresource.Preconditions.notEmpty;

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
        this.rel = notEmpty(rel, "rel");

        this.href = notEmpty(href, "href");

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
