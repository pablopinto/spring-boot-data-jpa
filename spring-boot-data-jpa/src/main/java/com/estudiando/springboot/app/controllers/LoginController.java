package com.estudiando.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(value = "logout", required=false) String logout,
			@RequestParam(value = "error",required=false) String error,
			Model model, 
			Principal principal , 
			RedirectAttributes flash) {
		
		if(principal != null) {
			flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
			return "redirect:/";
		}
		
		if(error != null) {
			model.addAttribute("error", "Ha introducido un usuario/contraseña incorrecto");
		}
		
		if(logout != null) {
			model.addAttribute("success", "Se ha cerrado la sesion satisfactoriamente");
		}
		
		
		return "login";
	}
}
