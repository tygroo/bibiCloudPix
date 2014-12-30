package fr.kisuke.rest.resources;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.springframework.stereotype.Component;


/**
 * Created by Bonheur on 27/12/2014.
 */
@Component
@Path("/upload")
public class UploadResource {
    private final String UPLOADED_FILE_PATH = "c:/temp/";

    @POST
    @Path("/image-upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) throws IOException
    {
        //Get API input data
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        //Get file name
        String fileName = uploadForm.get("fileName").get(0).getBodyAsString();

        //Get file data to save
        List<InputPart> inputParts = uploadForm.get("attachment");

        for (InputPart inputPart : inputParts)
        {
            try
            {
                //Use this header for extra processing if required
                @SuppressWarnings("unused")
                MultivaluedMap<String, String> header = inputPart.getHeaders();

                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);
                // constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes, fileName);
                System.out.println("Success !!!!!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return Response.status(200)
                .entity("Uploaded file name : "+ fileName).build();
    }

    //Utility method
    private void writeFile(byte[] content, String filename) throws IOException
    {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }
}
