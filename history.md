# 0.2.1 - TBD
* this is a backwards incompatible version, as any release prior to 1.0 can be.
* Locale added as param to all write methods
* _locale and _contentLanguage are added as handlebars paths on the root context whenever locale is not null.  Use the public constants on HandelbarsSerializer prefixed with HBS_PATH_TO_ to reference these paths.
* Added ResponseContentTypeAdvice spring ControllerAdvice to ensure the content-type response header is set since it is not by default in spring (see https://jira.spring.io/browse/SPR-14802)  

# 0.1.1
* Initial release