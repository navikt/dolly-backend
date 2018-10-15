package no.nav.dolly.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    private final static int NUMBER_OF_FIXED_THREAD_POOLS = 2;

    @Override
    public Executor getAsyncExecutor(){
        return new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(NUMBER_OF_FIXED_THREAD_POOLS));
    }
}
