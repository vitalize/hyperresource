package org.hyperfit.hyperresource.serializer;

import org.hyperfit.hyperresource.HyperResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface HyperResourceSerializer {

    /**
     * @return a list of content types that this serializer supports.  EG text/html application/hal+json
     */
    List<String> getContentTypes();

    /**
     * determines if this serializer can serialize the HyperResource searching for a serializer
     * @param resourceClass the class of the HyperResource that is searching for a serializer
     * @return true if this serializer can serialize the given resource, false otherwise
     */
    boolean canWrite(Class<? extends HyperResource> resourceClass);

    void write(HyperResource resource, OutputStream output) throws IOException;

    String writeToString(HyperResource resource) throws IOException;
}
