/*package com.samplecrud.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.samplecrud.model.Bank;
import com.samplecrud.service.BankService;

@Controller
@RequestMapping("/bank")
public class BankController extends  MyFirstController{
private BankService bankService;
	
	@Autowired(required=true)
	@Qualifier(value="bankService")
	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	
	
	@RequestMapping("/list")
	public String BankControllerMethod(Model model) {
		@SuppressWarnings("unused")
		String errorMsg="";
	List<Bank>  bankslist = new ArrayList<Bank>(); 
	try{
		bankslist = this.bankService.listOfBanks();
		model.addAttribute("bankslist",bankslist);//used to retrieve the info from db and display to jsp
		if(bankslist.size() == 0)
			errorMsg = "No results found";
	}
	catch(Exception e){
		errorMsg = "connect to sql server";
	}
   // model.addAttribute("user", this.userService.getUserById(userid));
    //model.addAttribute("errorMsg", errorMsg);

    return "addinfo";///////////
}

}
*/