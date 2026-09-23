package com.kassidy.sales_tracker;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.UUID;
import javax.imageio.ImageIO;

@Controller
public class ProductController {
    private final ProductRepository productRepository;
    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @GetMapping("/")
    public String home(Model model, HttpSession session){
        String workspaceId = getOrCreateWorkspaceId(session);
        List<Product> products =
                productRepository.findByWorkspaceIdOrderByNameAsc(workspaceId);
        int totalItemsSold = 0;
        double totalRevenue = 0;
        for(Product product : products){
            totalItemsSold += product.getSoldCount();
            totalRevenue += product.getRevenue();
        }
        model.addAttribute("products", products);
        model.addAttribute("totalItemsSold", totalItemsSold);
        model.addAttribute("totalRevenue", totalRevenue);
        return "index";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam double price,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             HttpSession session)
            throws IOException {
        Product product = new Product(name, price);
        String workspaceId = getOrCreateWorkspaceId(session);
        product.setWorkspaceId(workspaceId);   
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5 * 1024 * 1024) {
                throw new IOException("Image must be 5 MB or smaller.");
            }
        
            BufferedImage uploadedImage;
            try (var inputStream = image.getInputStream()) {
                uploadedImage = ImageIO.read(inputStream);
            }
            if (uploadedImage == null) {
                throw new IOException("The uploaded file is not a supported image.");
            }
            Path uploadDirectory = Paths.get("uploads");
            Files.createDirectories(uploadDirectory);
        
            String fileName = UUID.randomUUID() + ".png";
            Path filePath = uploadDirectory.resolve(fileName);
            if (!ImageIO.write(uploadedImage, "png", filePath.toFile())) {
                throw new IOException("Could not save the image.");
            }
            product.setImageUrl("/uploads/" + fileName);
        }
        productRepository.save(product);
        return "redirect:/";
    }
    

    @PostMapping("/import")
    public String importProducts(
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        String workspaceId = getOrCreateWorkspaceId(session);
        //open excel file
        try (Workbook workbook =
                     new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
    
                //skip heading
                if (row.getRowNum() == 0) {
                    continue;
                }
                //get column a and b
                Cell nameCell = row.getCell(0);
                Cell priceCell = row.getCell(1);
    
                // Skip rows that are missing name/price
                if (nameCell == null || priceCell == null) {
                    continue;
                }
                String name;
                if (nameCell.getCellType() == CellType.STRING) {
                    name = nameCell
                            .getStringCellValue()
                            .trim();
                } else {
                    name = nameCell
                            .toString()
                            .trim();
                }
    
                //skip if row is empty
                if (name.isEmpty()) {
                    continue;
                }
                double price;
                //if price is set as a number
                if (priceCell.getCellType() == CellType.NUMERIC) {
                    price =
                            priceCell.getNumericCellValue();
                } else {
                    //if price is a string
                    String priceText =
                            priceCell
                                    .toString()
                                    .replace("$", "")
                                    .replace(",", "")
                                    .trim();
                    //convert to double
                    price =
                            Double.parseDouble(priceText);
                }
                Product product =
                        new Product(name, price);
                //make sure product is set to the user's page
                product.setWorkspaceId(workspaceId);
                productRepository.save(product);
            }
        }
    
        //return to the sales tracker.
        return "redirect:/";
    }



    @PostMapping("/plus/{id}")
    public String addSale(@PathVariable Long id, HttpSession session) {
        String workspaceId = (String) session.getAttribute("workspaceId");
        Product product =
                productRepository.findById(id).orElseThrow();
        if (workspaceId != null &&
                workspaceId.equals(product.getWorkspaceId())) {
            product.addSale();
            productRepository.save(product);
        }
        return "redirect:/#product-" + id;
    }


    @PostMapping("/minus/{id}")
    public String removeSale(@PathVariable Long id, HttpSession session) {
        String workspaceId = (String) session.getAttribute("workspaceId");
        Product product =
                productRepository.findById(id).orElseThrow();
        if (workspaceId != null &&
                workspaceId.equals(product.getWorkspaceId())) {
            product.removeSale();
            productRepository.save(product);
        }
        return "redirect:/#product-" + id;
    }


    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam double price,
            HttpSession session) {
        String workspaceId = (String) session.getAttribute("workspaceId");
        Product product =
                productRepository.findById(id).orElseThrow();
        if (workspaceId != null &&
                workspaceId.equals(product.getWorkspaceId())) {
            product.setName(name);
            product.setPrice(price);
            productRepository.save(product);
        }
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            HttpSession session) {
        String workspaceId = (String) session.getAttribute("workspaceId");
        Product product =
                productRepository.findById(id).orElseThrow();
    
        if (workspaceId != null &&
                workspaceId.equals(product.getWorkspaceId())) {
            productRepository.delete(product);
        }
        return "redirect:/";
    }
    

    private String getOrCreateWorkspaceId(HttpSession session) {
        String workspaceId = (String) session.getAttribute("workspaceId");
        if (workspaceId == null) {
            workspaceId = java.util.UUID.randomUUID().toString();
            session.setAttribute("workspaceId", workspaceId);
        }
        return workspaceId;
    }
}
