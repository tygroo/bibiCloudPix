package fr.kisuke.rest.resources;



import java.io.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import com.sun.jersey.multipart.FormDataParam;
import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String UPLOADED_FILE_PATH = "/srv/data/";//"c:/temp/";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PictureDao pictureDao;

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
        }

        File fileMed = new File(filenameMed);
        if (!fileMed.exists()) {
            fileMed.createNewFile();
        }

        File fileLow = new File(filenameLow);
        if (!fileLow.exists()) {
            fileLow.createNewFile();
        }


        recordFileOnDisk(content, file);

        recordFileOnDisk(content, fileMed);

        recordFileOnDisk(content, fileLow);

        picture.setName(FilenameUtils.getName(filename));
        picture.setNameHight(nameNorm);
        picture.setNameMed(nameMed);
        picture.setNameLow(nameLow);
        picture.setPath(path);

        this.pictureDao.save(picture);
    }

    private void recordFileOnDisk(byte[] content, File file) throws IOException {
        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();
    }

}