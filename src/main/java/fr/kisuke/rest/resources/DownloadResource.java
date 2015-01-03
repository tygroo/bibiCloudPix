package fr.kisuke.rest.resources;

import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Created by Bonheur on 02/01/2015.
 */
@Component
@Path("/files")
public class DownloadResource {
    @Autowired
    private PictureDao pictureDao;

    @GET
    @Path("getImage/{imageId}")
    @Produces("image/*")
    public Response getImage(@PathParam(value = "imageId") Long imageId) {

        //Recuperation de l'image de la bdd
        Pictures picture = pictureDao.find(imageId);

        //Construction de la requete du fichier
        String fileName = picture.getPath()+picture.getNameHight();
        String exactName = picture.getName();

        //Put some validations here such as invalid file name or missing file name
        return getResponse(fileName, exactName);
    }

    @GET
    @Path("getImageMed/{imageId}")
    @Produces("image/*")
    public Response getImageMed(@PathParam(value = "imageId") Long imageId) {

        //Recuperation de l'image de la bdd
        Pictures picture = pictureDao.find(imageId);

        //Construction de la requete du fichier
        String fileName = picture.getPath()+picture.getNameMed();
        String exactName = picture.getName();

        //Put some validations here such as invalid file name or missing file name
        return getResponse(fileName, exactName);
    }

    @GET
    @Path("getImageLow/{imageId}")
    @Produces("image/*")
    public Response getImageLow(@PathParam(value = "imageId") Long imageId) {

        //Recuperation de l'image de la bdd
        Pictures picture = pictureDao.find(imageId);

        //Construction de la requete du fichier
        String fileName = picture.getPath()+picture.getNameLow();
        String exactName = picture.getName();
        return getResponse(fileName, exactName);
    }

    private Response getResponse(String fileName, String exactName) {
        //Put some validations here such as invalid file name or missing file name
        if(fileName == null || fileName.isEmpty())
        {
            Response.ResponseBuilder response = Response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        File repositoryFile = new File(fileName);
        if (repositoryFile != null) {

            Response.ResponseBuilder response = Response.ok((Object) repositoryFile);
            response.header("Content-Disposition", "attachment; filename="+exactName+"");
            return response.build();
        }
        return Response.noContent().build();
    }
}
