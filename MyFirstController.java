package com.samplecrud.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.TransactionException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.samplecrud.model.Bank;
import com.samplecrud.model.User;
import com.samplecrud.service.BankService;
import com.samplecrud.service.UserService;

@Controller
@RequestMapping("/employee")
public class MyFirstController {
	
	protected UserService userService;
	
	@Autowired(required=true)
	@Qualifier(value="userService")
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	protected BankService bankService;
	
	@Autowired(required=true)
	@Qualifier(value="bankService")
	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	
	
	@RequestMapping("/list") 
	public String myFirstMethod(Model model) {
		String errorMsg = "";
		List<User>  userlist = new ArrayList<User>();
		//List<Bank> bankslist = new ArrayList<Bank>();
		try{
			userlist = this.userService.listUsers();
			model.addAttribute("userlist",userlist);//used to retrieve the info from db and display to jsp
			if(userlist.size() == 0)
				errorMsg = "No results found";
		}
		catch(ObjectNotFoundException e){
			errorMsg = "User's are not been assigned to the banks";
		}
		catch(Exception e){
			errorMsg = "connect to sql server";
		}
		
		model.addAttribute("errorMsg",errorMsg);
		return "list";//fwd to jsp
	}
	
	@RequestMapping(value="/add", method = {RequestMethod.POST, RequestMethod.GET})
	public String addUsers(HttpServletRequest request, @ModelAttribute("users") User u, Model model) {
		try{
		      if(request.getParameter("submit").equals("Submit")){
		    	  //new person, add it
		    	  this.userService.addUser(u);
		      }
		      else if(request.getParameter("submit").equals("Update")) {
		    	  this.userService.updateUser(u);
		      }
		      return "redirect:/employee/list";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		List<Bank> bankslist = new ArrayList<Bank>();
		bankslist = this.bankService.listOfBanks();
		System.out.println("banks"+bankslist.toString());
		
		
		model.addAttribute("bankslist",bankslist);
		return "add";
	}
	
	@RequestMapping("/edit/{id}")
    public String editPerson(@PathVariable("id") int id, Model model){
        model.addAttribute("user", this.userService.getUserById(id));
        List<Bank> bankslist = new ArrayList<Bank>();
		bankslist = this.bankService.listOfBanks();
		model.addAttribute("bankslist",bankslist);
        return "add";
    }
	
	@RequestMapping(value="/remove/{id}" ,method = {RequestMethod.POST,RequestMethod.GET})
	public String removeUser(@PathVariable("id") int id){
	    this.userService.removeUser(id);
        return "redirect:/employee/list";
    }
	
}


