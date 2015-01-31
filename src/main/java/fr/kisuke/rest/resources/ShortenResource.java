package fr.kisuke.rest.resources;

import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bonheur on 31/01/2015.
 */
@Component
@Path("/short")
public class ShortenResource {
    @Autowired
    private PictureDao pictureDao;

    @GET
    @Path("{shortenId}")
    @Produces("image/*")
    public Response getImage(@PathParam(value = "shortenId") String shortenId) {

        Pictures pictureToSend = getPictures(shortenId);

        //Construction de la requete du fichier
        String fileName = pictureToSend.getPath()+pictureToSend.getNameHight();
        String exactName = pictureToSend.getName();

        //Put some validations here such as invalid file name or missing file name
        return getResponse(fileName, exactName);
    }

    private Pictures getPictures(String shortenId) {
        //Recuperation de l'image de la bdd
        List<Pictures> pictures = pictureDao.findAll();
        List<Pictures> lstPict = new ArrayList<Pictures>();

        for(Pictures pict: pictures){
            if (StringUtils.containsOnly(shortenId,pict.getShortNameHight()) && pict.getIsHightShare()){
                lstPict.add(pict);
            }
            if (StringUtils.containsOnly(shortenId,pict.getShortNameMed()) && pict.getIsMediumShare()){
                lstPict.add(pict);
            }
            if (StringUtils.containsOnly(shortenId,pict.getShortNameLow()) && pict.getIsLowShare()){
                lstPict.add(pict);
            }
        }
        if (!lstPict.isEmpty()){
//            if (lstPict.size()==1){
            return lstPict.get(0);
//            }else{
            }
//        }
        return null;
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
