package no.nav.dolly.mapper;

import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
public class MapperFacadeConfig {

    @Autowired(required = false)
    private List<MappingStrategy> mappingStrategies;

    @Autowired(required = false)
    private List<CustomConverter> customConverters;

    @Bean
    MapperFacade mapperFacade() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            for (MappingStrategy mapper : mappingStrategies) {
                mapper.register(mapperFactory);
            }
        }

        if (nonNull(customConverters)) {
            for (CustomConverter converter : customConverters) {
                mapperFactory.getConverterFactory().registerConverter(converter);
            }
        }

        return mapperFactory.getMapperFacade();

    }
}
