package fr.kisuke.rest.resources;



import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import com.sun.jersey.multipart.FormDataParam;
import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.dao.user.UserDao;
import fr.kisuke.entity.Pictures;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.apache.commons.io.FilenameUtils;



/**
 * Created by Bonheur on 27/12/2014.
 */
//@Produces({"application/xml"})
@Component
//@Scope("request")
@Path("/upload")
//@Controller
//@RequestMapping("")
public class UploadResource {
        private final String UPLOADED_FILE_PATH = "/srv/appli/images/";
//    private final String UPLOADED_FILE_PATH = "c:/temp/";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PictureDao pictureDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ObjectMapper mapper;


    @POST
    @Path("/file")
    @Consumes("multipart/form-data")
    @Produces("text/plain")
    public Response uploadFile(
            @FormDataParam("content") final InputStream uploadedInputStream,
            @FormDataParam("fileName") String fileName) throws IOException {

        //String uploadContent=IOUtils.toString(uploadedInputStream);
        this.logger.info("create an upload picture(): " + fileName);



        //convert the uploaded file to inputstream

        byte [] bytes = IOUtils.toByteArray(uploadedInputStream);

        //constructs upload file path
        fileName = UPLOADED_FILE_PATH + fileName;
        //  return Response.ok(uploadContent).build();
        writeFile(bytes,fileName);

        return Response.ok().build();
    }
    /**
     * header sample
     * {
     * 	Content-Type=[image/png],
     * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    //get uploaded filename, is there a easy way in RESTEasy?
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    //save to somewhere
    private void writeFile(byte[] content, String filename) throws IOException {
        UserDetails userdetails = isAdmin();
        DateTime now = new DateTime();

        String path = FilenameUtils.getFullPath(filename);
        String basename =  FilenameUtils.getBaseName(filename)+now.getMillis();
        String extension = FilenameUtils.getExtension(filename);

        String nameNorm = basename+"."+extension;
        String nameMed = basename+"-Med."+extension;
        String nameLow =  basename+"-Low."+extension;

        String filenameNorm = path + nameNorm;
        String filenameMed = path + nameMed;
        String filenameLow = path + nameLow;



        Pictures picture = new Pictures();
        //picture.setCreationDate(now);


        File file = new File(filenameNorm);
        if (!file.exists()) {
            file.createNewFile();
            recordFileOnDisk(content, file);
        }

        File fileMed = new File(filenameMed);
        if (!fileMed.exists()) {
            recordFileOnDisk(content, fileMed);

            // echelle <0 reduction
            // si on met une echelle >0 ca foire
            double echelleMed=0.75;

            changePictureQuality(extension, fileMed, echelleMed);
        }

        File fileLow = new File(filenameLow);
        if (!fileLow.exists()) {
            fileLow.createNewFile();
            recordFileOnDisk(content, fileLow);

            // echelle <0 reduction
            // si on met une echelle >0 ca foire
            double echelleLow=0.25;

            changePictureQuality(extension, fileLow, echelleLow);
        }

        //Persistence dans le BDD
        picture.setName(FilenameUtils.getName(filename));
        picture.setNameHight(nameNorm);
        picture.setNameMed(nameMed);
        picture.setNameLow(nameLow);
        picture.setPath(path);

        // String userName = userdetails.getUsername();
        if (null != userdetails ) {
            picture.setUser(userDao.findByName(userdetails.getUsername()));
        }

        this.pictureDao.save(picture);
    }

    private void changePictureQuality(String extension, File inputFile, double echelle) {
        try{
            BufferedImage bi= ImageIO.read(inputFile);
            System.out.println("Traitement de : " + inputFile);
            BufferedImage biNew = new BufferedImage((int) (bi.getWidth() * echelle),(int) (bi.getHeight() * echelle),
                    bi.getType());
            AffineTransform tx = new AffineTransform();
            tx.scale(echelle, echelle);
            AffineTransformOp op = new AffineTransformOp(tx,
                    AffineTransformOp.TYPE_BILINEAR);
            bi=op.filter(bi, biNew);
            ImageIO.write(bi, extension, inputFile);
        }
        catch (Exception e)
        {
            System.out.println("ERROR -- le fichier" + inputFile + " n'est pas une image");
        }
        //  inputFile.createNewFile();
    }

    private void recordFileOnDisk(byte[] content, File file) throws IOException {
        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();
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
