package com.bam.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bam.dto.Library;
import com.bam.dto.Messages;
import com.bam.dto.Students;
import com.bam.services.HelperClass;
import com.bam.services.MessageService;
import com.bam.services.RegisterService;
import com.bam.services.StudentService;

/**
 * Servlet implementation class MessageServlet
 */
@WebServlet("/message")
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer requestURL = request.getRequestURL();
		String requestName=requestURL.toString();
		int start=requestName.lastIndexOf("message");
		String name=requestName.substring(start)+".jsp";
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(name);
		HttpSession session = request.getSession();
		session.setAttribute("active_tab", "message");
		
		MessageService msgServ = new MessageService();
		List<Messages> messagesin, messagesout;
		Students student= new Students();
		if (session.getAttribute("user")!=null){
		//get current user session ID
		student=(Students) session.getAttribute("user");
		}
		Integer from =null;
		Integer to=null;
		
		//select the message part and the messages that are by the current student
		if (requestName.substring(start).equals("message_outbox")){
			if(session.getAttribute("admin")!=null){
				from=0;
				to=null;
			}else{
				from=student.getStudentId();
			}
			//get all messages form the database
			messagesout=msgServ.getMessages(from, to, null);
			
			if (messagesout.isEmpty()){
				
			}
			else{
			session.setAttribute("messages",messagesout);
			}
			dispatcher.forward(request, response);
			return;
		}
		else if (requestName.substring(start).equals("message")){
			if(session.getAttribute("admin")!=null){
				to=0;
				from=null;
			}else{
			to=student.getStudentId();
			from=0;
			}
			//get all messages form the database
			messagesin=msgServ.getMessages(from, to, null);
			if (messagesin.isEmpty()){
				
			}
			else{
			session.setAttribute("messagesin",messagesin);
			}
			dispatcher.forward(request, response);
			return;
		}
		else if (requestName.substring(start).equals("message_compose")){
			if(session.getAttribute("admin")!=null && request.getQueryString()!=null){
				List<Students> students=null;
				try {
					int ID= Integer.parseInt(request.getParameter("to"));
					StudentService sc= new StudentService();
					students=sc.getStudent(ID);
					session.setAttribute("to", students.get(0));
					request.setAttribute("msg_subject", request.getParameter("sub"));
				} catch (Exception e) {
					e.printStackTrace();
					String path=request.getContextPath();
					response.sendRedirect(path+"/error");
					return;
				}
				
			}else{
				session.setAttribute("to", null);
			}
		}
		
		dispatcher.forward(request, response);
		return;
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get all the parameters from the request and validate them
				Map<String, String[]> map = (Map<String, String[]>)request.getParameterMap();
				ArrayList<String> error = new ArrayList<String>();
				
				
				
				//send back the parameters to the request so that they will stay on the form 
				for(Map.Entry<String, String[]> entry : map.entrySet()){
					request.setAttribute(entry.getKey(),entry.getValue()[0]);
				}
				
				//check who is sending the message, user or admin
				int to =-1;
				int from = -1;
				String fromString="";
				String toString="";
				HttpSession session = request.getSession();
				Students std= new Students();
				
				if(session.getAttribute("user")!=null){
					std= (Students) session.getAttribute("user");
					from= std.getStudentId();
					to =0;
					fromString=std.getFirstName() +" "+ std.getlName()+" ("+ std.getEmail()+")";
					toString="Admin";
				}
				else if (session.getAttribute("admin")!=null){
					Library lib = new Library();
					lib = (Library) session.getAttribute("admin");
					from =0;
					fromString="Admin";
					std=(Students) session.getAttribute("to");
					to=std.getStudentId();
					toString=std.getFirstName() +" "+ std.getlName()+" ("+ std.getEmail()+")";
	
				}
				
				//pass the parameters to the helper class for validation
				HelperClass hc = new HelperClass();
				error = hc.validate(map);

				//if it comes back with no error, save it to the db, if it does tell the user
				if (error.isEmpty()){
					MessageService message = new MessageService();
					
					try {
						message.saveData(map,from, to,fromString,toString);
						response.sendRedirect("message_outbox");
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else{
					request.setAttribute("error", error.get(0));
					
				}
				
	}

}