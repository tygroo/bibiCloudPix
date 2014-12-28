package fr.kisuke.rest.resources;

import com.sun.jersey.core.header.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.InputStream;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Bonheur on 27/12/2014.
 */
@Component
@Path("/upload")
public class UploadResource {
    private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://Users/nikos/Desktop/Upload_Files/";

    /**
     * Upload a File
     */

//    @POST
//    public Response uploadFile(
//            @FormDataParam("file") InputStream fileInputStream,
//            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
//
//        String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ contentDispositionHeader.getFileName();
//
//        // save the file to the server
//        saveFile(fileInputStream, filePath);
//
//        String output = "File saved to server location : " + filePath;
//
//        return Response.status(200).entity(output).build();
//
//    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {

        try {
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
