package com.alams.service.material;

import com.alams.model.Course;
import com.alams.model.Material;
import org.springframework.web.multipart.MultipartFile;

/** Concrete Factory for PDF materials */
class PdfMaterialFactory extends MaterialFactory {
    @Override
    public Material createMaterial(MultipartFile file, Course course, String storedPath) {
        return new Material(
            file.getOriginalFilename(),
            storedPath,
            Material.MaterialType.PDF,
            course
        );
    }
}

/** Concrete Factory for Presentation materials (PPT/PPTX) */
class PresentationMaterialFactory extends MaterialFactory {
    @Override
    public Material createMaterial(MultipartFile file, Course course, String storedPath) {
        return new Material(
            file.getOriginalFilename(),
            storedPath,
            Material.MaterialType.PRESENTATION,
            course
        );
    }
}

/** Concrete Factory for Document materials (DOC/DOCX) */
class DocumentMaterialFactory extends MaterialFactory {
    @Override
    public Material createMaterial(MultipartFile file, Course course, String storedPath) {
        return new Material(
            file.getOriginalFilename(),
            storedPath,
            Material.MaterialType.DOCUMENT,
            course
        );
    }
}

/** Concrete Factory for generic/unknown file types */
class GenericMaterialFactory extends MaterialFactory {
    @Override
    public Material createMaterial(MultipartFile file, Course course, String storedPath) {
        return new Material(
            file.getOriginalFilename(),
            storedPath,
            Material.MaterialType.GENERIC,
            course
        );
    }
}
