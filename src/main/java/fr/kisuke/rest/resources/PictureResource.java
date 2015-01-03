package fr.kisuke.rest.resources;

import fr.kisuke.JsonViews;
import fr.kisuke.dao.picture.PictureDao;
import fr.kisuke.entity.Pictures;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@Path("/picture")
public class PictureResource
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PictureDao pictureDao;

	@Autowired
	private ObjectMapper mapper;


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String list() throws JsonGenerationException, JsonMappingException, IOException
	{
		this.logger.info("list()");
		List<Pictures> allEntries = this.pictureDao.findAll();
		List<Pictures> entriesToShow = new ArrayList<Pictures>(0);

		UserDetails userDetails = this.isAdmin();

		ObjectWriter viewWriter = null;

		if (null != userDetails ) {
			for (GrantedAuthority authority : userDetails.getAuthorities()) {
				if (authority.toString().equals("admin")) {
					viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
					entriesToShow = allEntries;
				} else if (authority.toString().equals("user")) {
					viewWriter = this.mapper.writerWithView(JsonViews.User.class);
					for (Pictures pict : allEntries) {
						if (userDetails.getUsername() == pict.getUser().getUsername()) {
							entriesToShow.add(pict);
						}
					}
				}
			}
		}else {
			viewWriter = this.mapper.writerWithView(JsonViews.User.class);
			for( Pictures pict: allEntries ){
				if (null == pict.getUser()){
					entriesToShow.add(pict);
				}
			}
		}
		return viewWriter.writeValueAsString(entriesToShow);
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Pictures read(@PathParam("id") Long id)
	{
		this.logger.info("read(id)");

		Pictures picture = this.pictureDao.find(id);


		if (picture == null) {
			throw new WebApplicationException(404);
		}
		return picture;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Pictures create(Pictures picture)
	{
		this.logger.info("create(): " + picture);

		return this.pictureDao.save(picture);
	}


	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Pictures update(@PathParam("id") Long id, Pictures picture)
	{
		this.logger.info("update(): " + picture);

		return this.pictureDao.save(picture);
	}


	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public void delete(@PathParam("id") Long id)
	{
		this.logger.info("delete(id)");

		this.pictureDao.delete(id);
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