package fr.kisuke.rest.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;

/**
 * Created by Bonheur on 27/12/2014.
 */
@Component
@Path("/upload")
public class UploadResource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
