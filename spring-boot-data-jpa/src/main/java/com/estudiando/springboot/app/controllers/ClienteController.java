package com.estudiando.springboot.app.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.estudiando.springboot.app.models.entity.Cliente;
import com.estudiando.springboot.app.models.service.IClienteService;
import com.estudiando.springboot.app.util.paginator.PageRender;



@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;
	
	private final Logger log = LoggerFactory.getLogger(getClass()); // Para hacer debug de los sitios de directorio
	
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id , Map<String , Object> model , RedirectAttributes flash) {
		
		Cliente cliente = clienteService.findOne(id);
		if(cliente==null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		
		model.put("cliente", cliente);
		model.put("titulo" , "Detalles del cliente " + cliente.getNombre());
		
		return "ver";
		
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page" , defaultValue = "0") int page , Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";

	}
	
	
	@RequestMapping(value="/form")
	public String crear(Map<String, Object> model) {
		
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
		
	}
	
	@RequestMapping(value="/form" , method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente , BindingResult result , Model model ,@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		if(!foto.isEmpty()) {
			
//			Path  directorioRecursos = Paths.get("src//main//resources//static//upload"); Ubicacion de guardado de imagenes en ruta local del proyecto
//			String rootPath = directorioRecursos.toFile().getAbsolutePath();
			
//			String rootPath = "C://Temp//uploads"; // Ruta externa al proyecto
			
			String uniqueFilename = UUID.randomUUID().toString() +"_" +foto.getOriginalFilename(); // Identificador unico 
			Path rootPath = Paths.get("uploads").resolve(uniqueFilename); //ruta relativa del archivo
			
			Path rootAbsolutePath = rootPath.toAbsolutePath();
			
			log.info("rootPath :" + rootPath);
			log.info("rootAbsolutePath :" + rootAbsolutePath);
			
			try {
				
//				byte[] bytes = foto.getBytes();
//				Path rutaCompleta = Paths.get(rootPath +"//" + foto.getOriginalFilename());
//				Files.write(rutaCompleta,bytes);
				
				Files.copy(foto.getInputStream(), rootAbsolutePath); //Hacer de esta manera o de la comentada anteriormente
				
				flash.addFlashAttribute("info" , "Has subido correctamente '" + foto.getOriginalFilename() + "'");
//				flash.addFlashAttribute("info" , "Has subido correctamente '" + rootAbsolutePath + "'"); // Para comprobar que efectivamente el archivo se esta subiendo con el UUID
				cliente.setFoto(foto.getOriginalFilename());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		String mensajeFlash = (cliente.getId() != null)? "Cliente editado con exito" : "Cliente creado con exito";
		
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
		
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id , RedirectAttributes flash) {
		if(id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito!");
		}
		return "redirect:/listar";
	}
	
	
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model , RedirectAttributes flash) {
		
		Cliente cliente = null;
		
		if(id > 0) {
			cliente = clienteService.findOne(id);
			if(cliente == null) {
				flash.addFlashAttribute("error", "El id del cliente no existe en la base de datos");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser cero");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}
}
