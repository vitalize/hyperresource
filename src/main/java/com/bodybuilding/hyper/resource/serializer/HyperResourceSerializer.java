package com.bodybuilding.hyper.resource.serializer;

import com.bodybuilding.hyper.resource.HyperResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface HyperResourceSerializer {

    List<String> getContentTypes();

    void write(HyperResource resource, OutputStream output) throws IOException;
}
