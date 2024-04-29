package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.config.annotations.dashboard.SwaggerDocGetTraderPortfolio;
import ca.jrvs.apps.trading.config.annotations.dashboard.SwaggerDocGetTraderProfile;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.dto.portfolio.PortfolioView;
import ca.jrvs.apps.trading.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard")
@RestController
@RequestMapping(value = "/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @SwaggerDocGetTraderProfile
    @GetMapping(path = "/profile/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public TraderAccountView getTraderProfile(@PathVariable Integer traderId) {
        return dashboardService.getTraderAndAccount(traderId);
    }

    @SwaggerDocGetTraderPortfolio
    @GetMapping(path = "/portfolio/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public PortfolioView getPortfolio(@PathVariable Integer traderId) {
        return dashboardService.getPortfolioByTraderId(traderId);
    }
}
