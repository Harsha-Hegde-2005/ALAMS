package com.alams.controller;

import com.alams.model.*;
import com.alams.service.CourseService;
import com.alams.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

/**
 * MVC Controller: Course and Material management.
 */
@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired private CourseService courseService;
    @Autowired private UserService userService;

    // === Professor: create course ===
    @GetMapping("/create")
    public String createCoursePage() { return "professor/create-course"; }

    @PostMapping("/create")
    public String createCourse(@RequestParam String courseName,
                               @RequestParam String description,
                               Authentication auth,
                               RedirectAttributes attrs) {
        User professor = userService.findByUsername(auth.getName()).orElseThrow();
        courseService.createCourse(courseName, description, professor);
        attrs.addFlashAttribute("success", "Course '" + courseName + "' created successfully.");
        return "redirect:/dashboard";
    }

    // === View course details ===
    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model, Authentication auth) {
        Course course = courseService.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("materials", courseService.getMaterialsByCourse(id));
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return user.isProfessor() ? "professor/course-detail" : "student/course-detail";
    }

    // === Professor: upload material ===
    @PostMapping("/{id}/upload")
    public String uploadMaterial(@PathVariable Long id,
                                 @RequestParam MultipartFile file,
                                 RedirectAttributes attrs) {
        try {
            courseService.uploadMaterial(file, id);
            attrs.addFlashAttribute("success", "Material uploaded successfully.");
        } catch (Exception e) {
            attrs.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/courses/" + id;
    }

    @GetMapping("/materials/{materialId}/view")
    @ResponseBody
    public ResponseEntity<Resource> viewMaterial(@PathVariable Long materialId) throws Exception {
        Material material = courseService.findMaterialById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        Path uploadRoot = courseService.getUploadRoot();
        Path filePath = Path.of(material.getFilePath()).toAbsolutePath().normalize();
        if (!filePath.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid material path");
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("Material file is unavailable");
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(material.getFileName())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + material.getFileName() + "\"")
                .contentType(mediaType)
                .body(resource);
    }
}
