package org.hyperfit.hyperresource.controls;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import static org.hyperfit.hyperresource.Preconditions.notEmpty;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@ToString
public class TemplatedAction {

    private final String name;
    private final List<FieldSet> fieldSets;
    private final String href;

    private TemplatedAction(Builder builder) {
        this.name = notEmpty(builder.name, "name");

        this.href = notEmpty(builder.href, "href");

        //fieldsets cannot be null, but the builder protects us from this currently
        this.fieldSets = builder.fieldSets;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<FieldSet> getFieldSets() {
        return fieldSets;
    }

    @NotNull
    //TODO: really i need is @IsUrl see http://jira/browse/COMAPI-3794
    @Size(min = 5)
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