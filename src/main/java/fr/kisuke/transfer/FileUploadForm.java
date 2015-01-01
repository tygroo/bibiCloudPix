package fr.kisuke.transfer;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

/**
 * Created by Bonheur on 01/01/2015.
 */
public class FileUploadForm {

    public FileUploadForm() {
    }

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    @FormParam("uploadedFile")
    @PartType("application/octet-stream")
    public void setData(byte[] data) {
        this.data = data;
    }

}