package fr.kisuke.rest.resources;



import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
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
    private final String UPLOADED_FILE_PATH = "c:/temp/";

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

        String basename = FilenameUtils.getFullPath(filename)+ FilenameUtils.getBaseName(filename)+now.getMillis();
        String extension = FilenameUtils.getExtension(filename);

        String filenameNorm = basename+"."+extension;
        String filenameMed = basename+"-Med."+extension;
        String filenameLow = basename+"-Low."+extension;

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


        FileOutputStream fop1 = new FileOutputStream(file);

        fop1.write(content);
        fop1.flush();
        fop1.close();

        FileOutputStream fop2 = new FileOutputStream(filenameMed);

        fop2.write(content);
        fop2.flush();
        fop2.close();

        FileOutputStream fop3 = new FileOutputStream(filenameLow);

        fop3.write(content);
        fop3.flush();
        fop3.close();

        picture.setName(basename);
        picture.setNameHight(filenameNorm);
        picture.setNameMed(filenameMed);
        picture.setNameLow(filenameLow);
        picture.setPath(FilenameUtils.getFullPath(filename));
        picture.setName(FilenameUtils.getName(filename));

        this.pictureDao.save(picture);
    }

}