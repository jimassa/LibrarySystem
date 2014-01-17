package com.bam.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bam.dto.PasswordChange;
import com.bam.services.PasswordService;

/**
 * Servlet implementation class ResetServlet
 */
@WebServlet("/reset")
public class ResetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = (String) request.getParameter("token");
		String email = (String) request.getParameter("email");
		PasswordService ps = new PasswordService();
		HttpSession session= request.getSession();
		final String ERROR="This Link is no Longer valid please generate new token to change password";
		if(ps.isTokenValidAndMatch(token, email)){
			PasswordChange pc = ps.getToken(token);
			if(pc.getTokenExpirationTime().before(new Date())){
				request.setAttribute("error", ERROR);
				session.setAttribute("token", token);
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("reset_password.jsp");
			dispatcher.forward(request, response);
			return;
		}else{
			String path=request.getContextPath();
			response.sendRedirect(path+"/error");
			return;
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String token = (String)session.getAttribute("token");
		String password = request.getParameter("password");
		String rePassword= request.getParameter("re_password");
	}

}
