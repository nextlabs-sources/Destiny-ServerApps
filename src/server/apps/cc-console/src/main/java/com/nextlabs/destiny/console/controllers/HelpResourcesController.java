package com.nextlabs.destiny.console.controllers;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.services.HelpContentSearchService;

/**
 * REST controller for help resources.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("help-resources")
public class HelpResourcesController {

    @Autowired
    private HelpContentSearchService helpContentSearchService;

    @GetMapping("{name}")
    public void redirectToResource(HttpServletResponse response, @PathVariable("name") String name) throws IOException {
        response.sendRedirect(helpContentSearchService.getResourceUrl(name));
    }

}
