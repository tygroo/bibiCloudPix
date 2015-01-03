package fr.kisuke.rest.resources;

import fr.kisuke.JsonViews;
import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        Pictures pictureToSend = getPictures(imageId);


        //Construction de la requete du fichier
        String fileName = pictureToSend.getPath()+pictureToSend.getNameHight();
        String exactName = pictureToSend.getName();

        //Put some validations here such as invalid file name or missing file name
        return getResponse(fileName, exactName);
    }

    private Pictures getPictures(Long imageId) {
        //Recuperation de l'image de la bdd
        Pictures picture = pictureDao.find(imageId);
        Pictures pictureToSend = null;

        UserDetails userDetails = this.isAdmin();

        if (null != userDetails ) {
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (authority.toString().equals("admin")) {
                    pictureToSend = picture;
                } else if (authority.toString().equals("user")) {

                        if (userDetails.getUsername() == picture.getUser().getUsername()) {
                            pictureToSend = picture;
                        }

                }
            }
        }else {
                if (null == picture.getUser()){
                    pictureToSend = picture;
                }
        }
        return pictureToSend;
    }

    @GET
    @Path("getImageMed/{imageId}")
    @Produces("image/*")
    public Response getImageMed(@PathParam(value = "imageId") Long imageId) {

        //Recuperation de l'image de la bdd
        Pictures pictureToSend = getPictures(imageId);


        //Construction de la requete du fichier
        String fileName = pictureToSend.getPath()+pictureToSend.getNameMed();
        String exactName = pictureToSend.getName();

        //Put some validations here such as invalid file name or missing file name
        return getResponse(fileName, exactName);
    }

    @GET
    @Path("getImageLow/{imageId}")
    @Produces("image/*")
    public Response getImageLow(@PathParam(value = "imageId") Long imageId) {

        //Recuperation de l'image de la bdd
        Pictures pictureToSend = getPictures(imageId);


        //Construction de la requete du fichier
        String fileName = pictureToSend.getPath()+pictureToSend.getNameLow();
        String exactName = pictureToSend.getName();
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

    private UserDetails isAdmin()
    {
        //UserDetails userDetails = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
            return null;
        }else {
            return (UserDetails) principal;
        }
    }

}
