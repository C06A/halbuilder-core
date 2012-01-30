package com.theoryinpractise.halbuilder.impl.resources;

import com.google.common.base.Optional;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.RenderableResource;
import com.theoryinpractise.halbuilder.api.Renderer;
import com.theoryinpractise.halbuilder.api.Resource;
import com.theoryinpractise.halbuilder.factory.ResourceFactory;
import com.theoryinpractise.halbuilder.impl.bytecode.InterfaceContract;
import com.theoryinpractise.halbuilder.impl.bytecode.InterfaceRenderer;
import com.theoryinpractise.halbuilder.impl.json.JsonRenderer;
import com.theoryinpractise.halbuilder.impl.xml.XmlRenderer;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class ImmutableResource extends BaseResource implements RenderableResource {

    private final Link selfLink;

    public ImmutableResource(ResourceFactory resourceFactory,
                             Map<String, String> namespaces, List<Link> links, Map<String, Object> properties, List<Resource> resources) {
        super(resourceFactory);
        this.namespaces = namespaces;
        this.links = links;
        this.properties = properties;
        this.resources = resources;

        this.selfLink = super.getSelfLink();
    }

    public Link getSelfLink() {
        return selfLink;
    }

    /**
     * Renders the current Resource as a proxy to the provider interface
     *
     * @param anInterface The interface we wish to proxy the resource as
     * @return A Guava Optional of the rendered class, this will be absent if the interface doesn't satisfy the interface
     */
    public <T> Optional<T> renderClass(Class<T> anInterface) {
        if (InterfaceContract.newInterfaceContract(anInterface).isSatisfiedBy(this)) {
            return InterfaceRenderer.newInterfaceRenderer(anInterface).render(this, null);
        } else {
            return Optional.absent();
        }
    }

    public String renderJson() {
        return renderAsString(new JsonRenderer());
    }

    public String renderXml() {
        return renderAsString(new XmlRenderer());
    }

    private String renderAsString(final Renderer renderer) {
        validateNamespaces(this);
        StringWriter sw = new StringWriter();
        renderer.render(this, sw);
        return sw.toString();
    }

}