package com.bam.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bam.dto.Books;
import com.bam.services.AddBookService;

/**
 * Servlet implementation class EditBookServlet
 */
@WebServlet("/edit_book")
public class EditBookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		int bookId=0;
		Books book=null;
		AddBookService bs= new AddBookService();
		if(session.getAttribute("admin")==null){
			response.sendRedirect("error");
			return;
		}else{
			
		try {
			 bookId= Integer.parseInt(request.getParameter("bookId"));
			 book=bs.getbooks(bookId, null, null, null, null, null).get(0);
		} catch (Exception e) {
			String path=request.getContextPath();
			response.sendRedirect(path+"/error");
			e.printStackTrace();
			return;
		}
		
		request.setAttribute("book", book);
		RequestDispatcher dispatcher = request.getRequestDispatcher("edit_book.jsp");
		dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String[]> map = request.getParameterMap();
		int bookId=Integer.parseInt(request.getParameter("id"))
				;
		AddBookService abs= new AddBookService();
		abs.editBook(map, bookId);
		
		response.sendRedirect("book_detail/"+bookId);
		return;
		
	}

}