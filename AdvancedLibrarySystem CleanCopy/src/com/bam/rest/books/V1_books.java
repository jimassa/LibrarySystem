package com.bam.rest.books;


import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.bam.services.AddBookService;
import com.bam.dto.Books;
import com.bam.util.ToJSON;

import org.codehaus.jettison.json.JSONArray;

@Path("/v1/books")
public class V1_books {
	List<Books> books= null;
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String returnBooks() throws Exception{
		
		AddBookService abs = new AddBookService();
		ToJSON toJson = new ToJSON();
		
		JSONArray array = new JSONArray(); //JSON array to return
		
		//call the getbooks function from the Hibernate function
		books = abs.getbooks(null, null, null, null, null, null);
		array= toJson.booksToJSON(books);
		
		
		return array.toString();
	}
	
	
}