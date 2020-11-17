package no.nav.dolly.config.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class AddAuthorizationToRouteFilter extends ZuulFilter {
    private final GenerateToken generateToken;
    private final String route;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (ctx.get("api") != null) && ctx.get("api").equals(route);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + generateToken.getToken());
        return null;
    }
}
