package com.samplecrud.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.samplecrud.constant.Variables;
import com.samplecrud.model.UsersBalance;
import com.samplecrud.service.UsersBalanceService;

@Controller
@RequestMapping("/employeebal")
public class UserBalanceController extends MyFirstController {
	
	private UsersBalanceService usersBalanceService;
	
	@Autowired(required=true)
	@Qualifier(value="usersBalanceService")
	public void setUsersBalanceService(UsersBalanceService usersBalanceService) {
		this.usersBalanceService = usersBalanceService;
	}

	
	@RequestMapping("/balanceinfo/{userid}")
    public String addAmount(@PathVariable("userid") int userid, HttpServletRequest request, @ModelAttribute("users_balance") UsersBalance ub, Model model){
		
		try{
		      if(request.getParameter("addbalancebtn") != null && request.getParameter("addbalancebtn").equals("Submit")){
		    	  
		    	  ub.setTypeoftxn("M");
		    	  this.usersBalanceService.addAmount(ub);
		    	  return "redirect:/employeebal/balanceinfo/"+userid;
		      }
		      else if(request.getParameter("withdrawbalancebtn") != null &&  request.getParameter("withdrawbalancebtn").equals("Submit")) {
		    	    ub.setTypeoftxn("W");
		    	    int rowCount = this.usersBalanceService.rowCount(userid);

		    	    System.out.println(rowCount );
				    if(rowCount > Variables.WITHDRAW_COUNT) {
				    	  ub.setWithdrawfee(10);
					}
				    
				    this.usersBalanceService.withdrawAmount(ub);
				    return "redirect:/employeebal/balanceinfo/"+userid;
		      }
		      
				
				
		      
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String errorMsg = "";
		List<UsersBalance>  usersBalancelist = new ArrayList<UsersBalance>(); 
		try{
			usersBalancelist = this.usersBalanceService.listUsersBalance(userid);
			model.addAttribute("usersBalancelist",usersBalancelist);//used to retrieve the info from db and display to jsp
			if(usersBalancelist.size() == 0)
				errorMsg = "No results found";
		}
		catch(Exception e){
			errorMsg = "connect to sql server";
		}
        model.addAttribute("user", this.userService.getUserById(userid));
        model.addAttribute("errorMsg", errorMsg);

        return "addinfo";
    }
	@RequestMapping(value="/transfer", method = {RequestMethod.POST, RequestMethod.GET})
	public String transfer(Model model,HttpServletRequest request) {
		String errormsg = "";
		
		if(request.getParameter("transfer") != null && request.getParameter("transfer").equals( "Submit")) {
			int fromuserid = Integer.parseInt(request.getParameter("fromaccount"));
			int touserid = Integer.parseInt(request.getParameter("toaccount"));
			double transferamount = Double.parseDouble(request.getParameter("transferamount"));
			double totalbal_fromuserid = Double.parseDouble(this.usersBalanceService.getbalance(fromuserid));
			
			double withdrawAmount = transferamount;
			
			int rowCount = this.usersBalanceService.rowCount(fromuserid);
			
			if(rowCount > Variables.WITHDRAW_COUNT) {
				withdrawAmount = transferamount + 10;
				UsersBalance ub1=new UsersBalance();
				ub1.setWithdrawfee(10);
			}
			
			if(totalbal_fromuserid < transferamount) {
				errormsg = "You have insufficient balance";
			}
			
			else{
				
				UsersBalance ub1=new UsersBalance();
				ub1.setUserid(fromuserid);
				ub1.setWithdrawamount(withdrawAmount);
				ub1.setTypeoftxn("WT");
				
				this.usersBalanceService.withdrawAmount(ub1);
				
				//int bankid_fromuserid = Integer.parseInt(this.bankService.listOfBanks());
			
				/*if((frombankid)!=(tobankid))
				{
					if(transferamount<2000)
					   ub1.setTransferfee(5); 
					if(transferamount<20000)
						ub1.setTransferfee(10);
					if(transferamount>20000)
						ub1.setTransferid(15);
				}*/
			
				
				UsersBalance ub=new UsersBalance();
				ub.setUserid(touserid);
				ub.setAddamount(transferamount);
				ub.setTypeoftxn("T");
				ub.setTransferid(fromuserid);
				this.usersBalanceService.addAmount(ub);
				
				return "redirect:/employee/list"; 
			}
		}
		model.addAttribute("errormsg", errormsg);
		model.addAttribute("userlist",this.userService.listUsers());
		//model.addAttribute("bankslist",this.bankService.listOfBanks());//////////
		
			
		return "transfer";
	}
	
	@RequestMapping(value="/getbalance", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public String getbalance(HttpServletRequest request) {
		int userid=Integer.parseInt(request.getParameter("userid"));
		String bal = this.usersBalanceService.getbalance(userid);
		return "{ \"balance\": \""+bal+"\", \"userid\": \""+userid+"\"  }";
	}
	 
}
