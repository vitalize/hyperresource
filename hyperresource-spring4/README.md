# hyperresource-spring4
Adaptors, plugins, etc for using the hyperresource libraries with spring4 based projects.  Many of these will work with spring5 as well, but probalby not the webflux stuff

## Components

### Message Converter
Register your message converter by wrapping a hyperresurce-assembler with a WriteOnlyHyperResourceMessageConverter
```
@Configuration
public class WebMVCConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(
            new WriteOnlyHyperResourceMessageConverter(
                new HALJSONJacksonSerializer()
            )
        );
    }
}
```

### Assembler Arg Resolver
