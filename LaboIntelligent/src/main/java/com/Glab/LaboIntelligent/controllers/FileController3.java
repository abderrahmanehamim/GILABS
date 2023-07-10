package com.Glab.LaboIntelligent.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Glab.LaboIntelligent.models.FileInfo;
import com.Glab.LaboIntelligent.repositories.AppUserRepository;
import com.Glab.LaboIntelligent.repositories.ArticlesRepository;
import com.Glab.LaboIntelligent.repositories.DepartmentRepository;
import com.Glab.LaboIntelligent.repositories.EtudiantRepository;
import com.Glab.LaboIntelligent.repositories.LaboratoiresRepository;
import com.Glab.LaboIntelligent.repositories.ProfesseurRepository;
import com.Glab.LaboIntelligent.services.FilesStorageService3;

@Controller
public class FileController3 {
	 @Autowired
	  FilesStorageService3 storageService;
		@Autowired
		private LaboratoiresRepository laboratoiresRepository;
		@Autowired
		private ArticlesRepository articlesRepository;
		@Autowired
		EtudiantRepository etudiantRepository;
		@Autowired
		ProfesseurRepository professeurRepository;
		@Autowired
		private AppUserRepository appUserRepository;

		@Autowired
		
		private DepartmentRepository departmentRepository;
	

	  @GetMapping("/files3/new")
	  public String newFile(Model model) {
			
	    return "addpdf3";
	  }

	  @PostMapping("/files3/upload")
	  public String uploadFile(Model model, @RequestParam("file") MultipartFile file) {
	    String message = "";

	    try {
	      storageService.save(file);
	  	
	      message = "Uploaded the file successfully: " + file.getOriginalFilename();
	      model.addAttribute("message", message);
	    } catch (Exception e) {
	    
	    	
	    	message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
	      model.addAttribute("message", message);
	    }
		
	    return "addpdf3";
	  }

	  @GetMapping("/files3")
	  public String getListFiles(Model model) {
	    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
	      String filename = path.getFileName().toString();
	      String url = MvcUriComponentsBuilder
	          .fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();

	      return new FileInfo(filename, url);
	    }).collect(Collectors.toList());

	    model.addAttribute("files", fileInfos);
		
	    return "allpdf3";
	  }

	  @GetMapping("/files3/{filename:.+}")
	  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
	    Resource file = storageService.load(filename);

	    
	    
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
	  }

	  @GetMapping("/files3/delete/{filename:.+}")
	  public String deleteFile(@PathVariable String filename, Model model, RedirectAttributes redirectAttributes) {
	    try {
	      boolean existed = storageService.delete(filename);

	      if (existed) {
	        redirectAttributes.addFlashAttribute("message", "Delete the file successfully: " + filename);
	      } else {
	        redirectAttributes.addFlashAttribute("message", "The file does not exist!");
	      }
	    } catch (Exception e) {
	      redirectAttributes.addFlashAttribute("message",
	          "Could not delete the file: " + filename + ". Error: " + e.getMessage());
	    }

	    return "redirect:/files3";
	  }

}
