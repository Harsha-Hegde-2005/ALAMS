package com.alams.service.material;

import com.alams.model.Course;
import com.alams.model.Material;
import org.springframework.web.multipart.MultipartFile;

/**
 * DESIGN PATTERN: Factory Method Pattern
 *
 * Abstract factory for creating Material objects of different types.
 * Each subclass decides exactly what type of Material to create.
 *
 * DESIGN PRINCIPLE: Open/Closed Principle (OCP)
 * The factory is open for extension (add new material types by subclassing)
 * but closed for modification (existing factory logic never changes).
 */
public abstract class MaterialFactory {

    /**
     * Factory Method - subclasses override this to produce specific Material instances.
     */
    public abstract Material createMaterial(MultipartFile file, Course course, String storedPath);

    /**
     * Static factory selector - returns the correct factory based on file extension.
     * This keeps the caller decoupled from concrete factory implementations.
     */
    public static MaterialFactory getFactory(MultipartFile file) {
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (name.endsWith(".pdf")) {
            return new PdfMaterialFactory();
        } else if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
            return new PresentationMaterialFactory();
        } else if (name.endsWith(".doc") || name.endsWith(".docx")) {
            return new DocumentMaterialFactory();
        } else {
            return new GenericMaterialFactory();
        }
    }
}
