package com.kassidy.sales_tracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpSession;
//controller tells spring that this class handles requests coming from website
import org.springframework.stereotype.Controller;
//Model allows us to send Java data to the HTML page
import org.springframework.ui.Model;
//gives us annotations like @GetMapping
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
//@controller tells spring that this class controls what happens when someone interacts with out website
@Controller
public class ProductController {
    //this var gives this controller access to the product database
    private final ProductRepository productRepository;

    //constructor for product controller. spring automatically gives us the ProductRepository
    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    //@getmapping means when someone visits the https page it runs this method
    @GetMapping("/")
    public String home(Model model, HttpSession session){
    
        String workspaceId = (String) session.getAttribute("workspaceId");
    
        if (workspaceId == null) {
            workspaceId = java.util.UUID.randomUUID().toString();
            session.setAttribute("workspaceId", workspaceId);
        }
    
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

    //This method runs when the user submits
    
    // the "Add Product" form.
    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam double price,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             HttpSession session)
            throws IOException {

        // Create the new Product object.
        Product product = new Product(name, price);
        String workspaceId = (String) session.getAttribute("workspaceId");
        if (workspaceId == null) {
            workspaceId = java.util.UUID.randomUUID().toString();
            
            session.setAttribute("workspaceId", workspaceId);
        }

        product.setWorkspaceId(workspaceId);

        // Check whether the user actually selected an image.
        if (image != null && !image.isEmpty()) {

            // Create an "uploads" folder if it does not already exist.
            String uploadDirectory = "uploads/";

            File directory = new File(uploadDirectory);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Give the file a unique name so two images
            // with the same original filename do not overwrite each other.
            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + image.getOriginalFilename();

            // Save the image file into the uploads folder.
            Path filePath = Paths.get(uploadDirectory, fileName);

            Files.write(filePath, image.getBytes());

            // Save the path to the Product object.
            product.setImageUrl("/uploads/" + fileName);
        }

        // Save the Product object to the database.
        productRepository.save(product);

        return "redirect:/";
    }



    // This method runs when the user presses
    // the + button for a product.
    //
    // {id} represents the ID of whichever product
    // was clicked.
    @PostMapping("/plus/{id}")
    public String addSale(@PathVariable Long id, HttpSession session) {

        String workspaceId = (String) session.getAttribute("workspaceId");
    
        Product product =
                productRepository.findById(id).orElseThrow();
    
        // Only change the product if it belongs to this workspace.
        if (workspaceId != null &&
                workspaceId.equals(product.getWorkspaceId())) {
    
            product.addSale();
            productRepository.save(product);
        }
    
        return "redirect:/#product-" + id;
    }



    // This method runs when the user presses
    // the - button for a product.
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


    // This method runs when the user edits a product.
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


    // This method runs when the user presses
    // "Delete Product".
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
}
