package com.apap.tutorial8.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial8.model.PasswordModel;
import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value="/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute UserRoleModel user, Model model) {
		String pattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}";
		String messages="";
		if(user.getPassword().matches(pattern)) {
			userService.addUser(user);
			messages=null;
		}
		else {
			messages="password tidak sesuai";
		}
		model.addAttribute("messages",messages);
		return "home";
	}
	
	@RequestMapping(value="/updatePassword", method=RequestMethod.GET)
	private String updatePassword() {
		return "update-password";
	}
	
	@RequestMapping(value="/submitUpdatePassword", method=RequestMethod.POST)
	private String submitUpdatePassword(@ModelAttribute PasswordModel pass, Model model, RedirectAttributes redirectAttrs) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserRoleModel user = userService.findByUsername(username);
		String messages = "";
		String pattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}";
		if(pass.getConfirmPass().equalsIgnoreCase(pass.getNewPassword())) {
			if(passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				if(pass.getNewPassword().matches(pattern)) {
					userService.changePassword(user, pass.getNewPassword());
					messages="Password Berhasil Diubah!";
				}
				else {
					messages ="Pasword anda tidak sesuai dengan ketentuan";
				}
			}
			else {
				messages="Terjadi Kesalahan";
			}
		}
		else {
			messages="Password Tidak Sesuai";
		}
		redirectAttrs.addFlashAttribute("messages", messages);
		return "redirect:/user/updatePassword";
	}
}
